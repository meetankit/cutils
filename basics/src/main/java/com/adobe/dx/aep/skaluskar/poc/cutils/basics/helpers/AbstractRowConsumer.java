/**
 * AbstractRowConsumer.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

import lombok.Getter;

/**
 * @author skaluska
 * 
 *         This class, along with RowBuffer, provides a way to speed up
 *         processing of rows produced by a producer, when the processing is a
 *         bottleneck.
 * 
 *         Expected usage is as follows: the producer keeps adding rows to a
 *         RowBuffer shared by the consuming (processing) threads. The shared
 *         RowBuffer is initialized and passed to the consumer. The consumer
 *         extends this class to define the actual processing (processRows) or
 *         the "application".
 * 
 *         The main value of this class is to monitor the load, create more
 *         threads if needed and distribute the processing work amongst those
 *         threads. Creation of threads is sensitive to things like: whether the
 *         processing requires order to be preserved (in which case there can be
 *         only 1 thread), no of processors, current system CPU load and any
 *         application constraints. Distribution of work takes care of tuning
 *         the polling frequency.
 * 
 *         There are some hooks for the application (in the form of abstract
 *         methods) to control the maximum number of threads (passed as an
 *         argument), when new threads should be forked (okayToStartNewThread),
 *         any pre / post processing before / after threads do their work (setup
 *         / postProcess) and pre / post processing before / after each
 *         individual thread does its work.
 */
public abstract class AbstractRowConsumer<RowType> implements Runnable
{
  private static final float  MAX_THREADS_MULTIPLIER = (float) 4.0;
  private static final long   MIN_SLEEP_MSECS        = 50;
  private static final long   INIT_SLEEP_MSECS       = MIN_SLEEP_MSECS * 8;
  private static final long   MAX_SLEEP_MSECS        = MIN_SLEEP_MSECS * 64;
  private static final int    MAX_SLEEP_COUNT        = 1000000;
  private static final float  REDUCTION_FACTOR       = (float) 0.9;
  private static final double MAX_SYSTEM_CPU_LOAD    = 0.9;

  /* derived fields */
  private RowBuffer<RowType>  rowBuffer;

  /* runtime state */
  private AtomicBoolean       eof;
  private boolean             preserveOrder          = true;
  private Thread[]            consumerThreads;

  @Getter
  private int                 numThreads;
  @Getter
  private int                 maxThreads;

  private Logger              logger                 = LoggerFactory.getLogger(
      AbstractRowConsumer.class);

  protected AbstractRowConsumer(RowBuffer<RowType> inputRowBuffer,
      boolean pOrder, int maxThreadsPerCpu)
  {
    commonConstructor(inputRowBuffer, pOrder, maxThreadsPerCpu);
  }

  protected AbstractRowConsumer(RowBuffer<RowType> inputRowBuffer,
      boolean pOrder)
  {
    commonConstructor(inputRowBuffer, pOrder,
        Math.round(MAX_THREADS_MULTIPLIER));
  }

  private void commonConstructor(RowBuffer<RowType> inputRowBuffer,
      boolean pOrder, int max_threads)
  {
    rowBuffer = inputRowBuffer;
    eof = new AtomicBoolean();
    preserveOrder = pOrder;
    int numProcessors = Runtime.getRuntime().availableProcessors();
    maxThreads = max_threads * numProcessors;
    consumerThreads = new Thread[maxThreads];
    numThreads = 0;
    setup();
    logger.debug(
        "Initialized - numProcessors: {}, maxThreads: {}",
        String.valueOf(numProcessors), String.valueOf(maxThreads),
        String.valueOf(preserveOrder));
  }

  private double systemCpuLoad()
  {
    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
        OperatingSystemMXBean.class);
    return osBean.getSystemCpuLoad();
  }

  public synchronized void startConsumerThread()
  {
    /*
     * Always start the first thread. Additional threads should be started if
     * order of processing doesn't need to be preserved, we haven't exceeded the
     * maximum allowed threads and system CPU load isn't too high.
     * 
     * Additional check: make sure the application is ready
     */
    double systemLoad = systemCpuLoad();
    if (((numThreads == 0) ||
        ((numThreads < maxThreads)
            && !preserveOrder
            && (systemLoad < MAX_SYSTEM_CPU_LOAD)))
        && okayToStartNewThread())
    {
      consumerThreads[numThreads] = new Thread(this);
      consumerThreads[numThreads].start();
      logger.debug("Started new consumer thread no: {}, id: {}, "
          + "maxThreads: {}, systemLoad: {}",
          String.valueOf(numThreads),
          String.valueOf(consumerThreads[numThreads].getId()),
          String.valueOf(maxThreads), String.valueOf(systemLoad));
      numThreads++;
    }
  }

  public void endConsumerThreads()
  {
    /*
     * the main thread returns when all the threads are done so it is safe to
     * declare that we are at EOF
     */
    setEof();

    for (int threadNo = 0; threadNo < numThreads; threadNo++)
    {
      Thread thread = consumerThreads[threadNo];
      while (thread.isAlive())
      {
        try
        {
          thread.join();
          logger.trace("Joined consumer thread no: {}, id: {}",
              String.valueOf(threadNo), String.valueOf(thread.getId()));
        }
        catch (InterruptedException e)
        {
        }
      }
    }
    postProcess();
  }

  private void setEof()
  {
    eof.set(true);
  }

  private List<RowType> nextInputBatch()
  {
    List<RowType> ret = rowBuffer.nextBatch();

    logger.trace(
        "Returned batch - thread: {}, size: {}\n",
        String.valueOf(Thread.currentThread().getId()),
        (ret == null) ? "0" : String.valueOf(ret.size()));
    return ret;
  }

  @Override
  public void run()
  {
    /* initialize */
    Object context = threadInit();

    long sleepTime = INIT_SLEEP_MSECS;
    int sleepNo = 0;
    int rowsProcessed = 0;

    /*
     * We need to first check EOF and then the accumulated work every time
     * because the producer adds new work and finally signals EOF after all work
     * has been added to the row buffer.
     */
    boolean atEof = eof.get();
    int accumulatedWork = rowBuffer.getNumRows();
    while (!atEof || (accumulatedWork > 0))
    {
      List<RowType> rowsToProcess = nextInputBatch();
      int batchSize = (rowsToProcess == null) ? 0 : rowsToProcess.size();
      int newAccumulatedWork;
      if (batchSize > 0)
      {
        /* process the next batch */
        processRows(context, rowsToProcess);
        rowsProcessed += batchSize;
        newAccumulatedWork = rowBuffer.getNumRows();
        logger.debug(
            "Processed batch - thread: {}, size: {}, accumulatedWork: {}, newAccumulatedWork: {}\n",
            String.valueOf(Thread.currentThread().getId()),
            String.valueOf(batchSize), String.valueOf(accumulatedWork),
            String.valueOf(newAccumulatedWork));

        /*
         * consider starting another thread if accumulated work isn't reducing
         * fast enough
         */
        if (newAccumulatedWork > REDUCTION_FACTOR * accumulatedWork)
        {
          startConsumerThread();
        }
      }
      else
      {
        /* sleep for some time if there are no more rows */
        logger.debug(
            "Sleeping - thread: {}, sleepNo: {}, sleepTime: {}, "
                + "rowsProcessed: {}, rowNum: {}, buffer size: {}\n",
            String.valueOf(Thread.currentThread().getId()),
            String.valueOf(sleepNo), String.valueOf(sleepTime),
            String.valueOf(rowsProcessed), String.valueOf(batchSize),
            String.valueOf(rowBuffer.getNumRows()));

        if (sleepNo > MAX_SLEEP_COUNT)
          throw new CuRuntimeException(
              CUMessages.CU_INTERNAL_OPERATION);

        try
        {
          Thread.sleep(sleepTime);
        }
        catch (InterruptedException e)
        {
          throw new CuRuntimeException(
              CUMessages.CU_INTERNAL_OPERATION);
        }

        /* adjust the sleep time - ignore the first iteration */
        newAccumulatedWork = rowBuffer.getNumRows();
        if (sleepNo > 0)
        {
          if (newAccumulatedWork > accumulatedWork)
          {
            sleepTime = sleepTime / 2;
            if (sleepTime < MIN_SLEEP_MSECS)
            {
              sleepTime = MIN_SLEEP_MSECS;
            }
          }
          else if ((newAccumulatedWork == 0)
              || (newAccumulatedWork < 0.5 * accumulatedWork))
          {
            sleepTime = sleepTime * 2;
            if (sleepTime > MAX_SLEEP_MSECS)
              sleepTime = MAX_SLEEP_MSECS;
          }
        }

        logger.debug(
            "Adjustments - thread: {}, accumulatedWork: {}, newAccumulatedWork: {}, sleepTime: {}\n",
            String.valueOf(Thread.currentThread().getId()),
            String.valueOf(accumulatedWork), String.valueOf(newAccumulatedWork),
            String.valueOf(sleepTime));

        sleepNo++;
      }

      atEof = eof.get();
      accumulatedWork = rowBuffer.getNumRows();
    }

    /* finish */
    threadEnd(context);

    logger.debug(
        "Exiting consumer thread: {}, sleepNo: {}, rowsProcessed: {}\n",
        String.valueOf(Thread.currentThread().getId()), String.valueOf(sleepNo),
        String.valueOf(rowsProcessed));
  }

  /**
   * setup before the processing threads are created.
   */
  protected abstract void setup();

  /**
   * Initializations before processing any rows for the thread.
   * 
   * @return any context for the current thread that is needed while processing
   *         rows.
   */
  protected abstract Object threadInit();

  /**
   * Checks if it is okay to start a new thread. This is an additional option in
   * case there are some constraints related to processing that need to restrict
   * spawning new threads.
   * 
   * @return true if it is okay to start a new consumer thread
   */
  protected abstract boolean okayToStartNewThread();

  /**
   * Processes the batch of rows passed in.
   * 
   * @param ctx
   *          - context returned by init
   * @param rowsToProcess
   *          - a list containing the input rows
   */
  protected abstract void processRows(Object ctx, List<RowType> rowsToProcess);

  /**
   * Final computation after all the rows are processed.
   * 
   * @param ctx
   *          - context returned by init
   */
  protected abstract void threadEnd(Object ctx);

  /**
   * post processing after all the threads are done
   */
  protected abstract void postProcess();
}
