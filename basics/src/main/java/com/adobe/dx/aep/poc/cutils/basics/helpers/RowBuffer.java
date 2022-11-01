/**
 * RowBuffer.java
 */
package com.adobe.dx.aep.poc.cutils.basics.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skaluska
 * 
 *         This class, along with AbstractRowConsumer, provides a way to speed
 *         up processing of rows produced by a producer, when the processing is
 *         a bottleneck.
 * 
 *         Expected usage is as follows: the producer keeps adding rows to
 *         RowBuffer shared by the consuming (processing) threads. The shared
 *         RowBuffer is initialized and passed to the consumer.
 * 
 *         The main value of this class is to buffer rows in batches of maximum
 *         size (as specified to the constructor), in a way that minimizes
 *         synchronization bottlenecks. The maximum batch size should be chosen
 *         to be small (compared to the total number of rows) to ensure an even
 *         distribution of work across all the consuming threads. At the same
 *         time, it should be large enough so that each consuming thread has a
 *         sizable amount of work to do and spends a small fraction of its time
 *         polling for new work - that will depend on the nature of processing.
 */
public class RowBuffer<RowType>
{
  private int                 maxBatchSize;

  private List<List<RowType>> outputQueue;

  private List<RowType>       currentBatch;

  private int                 numCurrentRows;

  public RowBuffer(int batchSize)
  {
    maxBatchSize = batchSize;
    initBuffer();
  }

  private void initBuffer()
  {
    currentBatch = new ArrayList<RowType>();
    outputQueue = new ArrayList<List<RowType>>();
    numCurrentRows = 0;
  }

  /**
   * Returns the number of rows currently in the buffer
   */
  public synchronized int getNumRows()
  {
    return numCurrentRows;
  }

  /**
   * Adds a new row at the last position (end of the list).
   * 
   * @param row
   *          - row to be added
   */
  public synchronized void addRow(RowType row)
  {
    /* add the row to the current batch */
    currentBatch.add(row);
    numCurrentRows++;

    /*
     * if the current batch is full, move it to the output queue and create a
     * new batch
     */
    if (currentBatch.size() == maxBatchSize)
    {
      outputQueue.add(currentBatch);
      currentBatch = new ArrayList<RowType>();
    }
  }

  /**
   * Get the next batch of rows; a batch contains <= maxBatchSize rows.
   * 
   * @return list of rows
   */
  public synchronized List<RowType> nextBatch()
  {
    List<RowType> ret = null;

    /* get the oldest batch from the output queue */
    if (outputQueue.size() > 0)
    {
      ret = outputQueue.remove(0);
    }
    else
    {
      /* return the current batch and allocate a new one */
      ret = currentBatch;
      currentBatch = new ArrayList<RowType>();
    }
    numCurrentRows -= ret.size();

    return ret;
  }
}
