/**
 * TaskParallelizer.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

/**
 * @author admin
 *
 *         Parallelizes a list of tasks that implement Callable.
 */
public class TaskParallelizer<Result, Task extends Callable<Result>>
{
  private static final float DEFAULT_THREADS_PER_CPU = (float) 2.0;

  private static Logger      logger                  = LoggerFactory
      .getLogger(TaskParallelizer.class);

  private List<Task>         tasks;

  private float              maxThreadsMultiplier;

  private boolean            continueAfterException;

  public TaskParallelizer(List<Task> taskList)
  {
    this(taskList, DEFAULT_THREADS_PER_CPU);
  }

  public TaskParallelizer(List<Task> taskList, float maxThreadsPerCpu)
  {
    this(taskList, maxThreadsPerCpu, false);
  }

  public TaskParallelizer(List<Task> taskList, float maxThreadsPerCpu,
      boolean finishExecInSpiteOfException)
  {
    tasks = taskList;
    maxThreadsMultiplier = maxThreadsPerCpu;
    continueAfterException = finishExecInSpiteOfException;
  }

  @SuppressWarnings({ "unchecked" })
  public List<Result> run()
  {
    List<Result> taskResults = new ArrayList<Result>();
    ExecutorService execService = null;

    try
    {
      /* set up exec service */
      int numProcessors = Runtime.getRuntime().availableProcessors();
      logger.trace("Numprocessors=" + numProcessors);
      execService = Executors.newFixedThreadPool(Math.round(
          maxThreadsMultiplier * numProcessors));

      /* set up tracking data */
      Future<Result> futures[] = new Future[tasks.size()];

      /* kick off each task */
      int workerNo = 0;
      for (Task t : tasks)
      {
        if (!Thread.currentThread().isInterrupted())
          futures[workerNo++] = execService.submit(t);
        else
          break;
      }

      int numExceptions = 0;
      for (int i = 0; i < futures.length; i++)
      {
        Future<Result> f = futures[i];
        if (f != null)
        {
          try
          {
            /*
             * Cancel ongoing operations if the current thread was interrupted
             * or if there was an exception, unless the caller specifically
             * asked for execution to continue even after an exception or the
             * task is already done.
             */
            if (Thread.currentThread().isInterrupted() ||
                ((numExceptions > 0) && !continueAfterException && !f.isDone()))
            {
              f.cancel(true);
            }
            else
            {
              Result taskResult = f.get();
              if (taskResult != null)
                taskResults.add(taskResult);
            }
          }
          catch (Exception e)
          {
            numExceptions++;
            logger.warn("Exception in task - id: {}, msg: {}", i,
                e.getMessage());
            e.printStackTrace();
          }
        }
      }

      if (numExceptions > 0)
        throw new CuRuntimeException(CUMessages.CU_num_TASKS_HAD_EXCEPTIONS,
            String.valueOf(numExceptions));
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
    finally
    {
      /* shut down the exec service if required */
      if (execService != null)
        execService.shutdown();
    }

    if (Thread.currentThread().isInterrupted())
      throw new CuRuntimeException(CUMessages.CU_USER_CANCELLED_OPERATION);

    return taskResults;
  }
}
