/**
 * StreamConsumer.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

/**
 * @author skaluska
 *
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamConsumer
 */
public class StreamConsumer implements Runnable
{
  /** file name for the log */
  private String              logFileName;
  /** stream to consume */
  private InputStream         stream;
  private boolean             writeOutput;
  /** logger */
  private static final Logger logger = LoggerFactory
                                         .getLogger(StreamConsumer.class
                                             .getName());

  /**
   * Constructor for StreamConsumer
   */
  public StreamConsumer(String name, InputStream stream)
  {
    this.logFileName = name;
    writeOutput = (name != null);
    this.stream = stream;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    try
    {
      FileOutputStream fs = null;
      BufferedOutputStream s = null;

      if (writeOutput)
      {
        // create log file
        File logFile = new File(logFileName);
        if (logFile.exists())
          logFile.delete();
        logFile.createNewFile();
        logger.debug(String.format("Created log file [%s]\n", logFileName));

        // open log file for writing
        fs = new FileOutputStream(logFile);
        s = new BufferedOutputStream(fs);
      }

      // consume input stream and write to log
      BufferedInputStream inStream = new BufferedInputStream(stream);
      int ls_str;
      while ((ls_str = inStream.read()) != -1)
      {
        if (s != null)
          s.write(ls_str);
      }

      if (s != null)
        s.close();
      if (fs != null)
        fs.close();
    }
    catch (Exception e)
    {
      System.err.println("Error consuming process output");
      e.printStackTrace();
    }
  }
}
