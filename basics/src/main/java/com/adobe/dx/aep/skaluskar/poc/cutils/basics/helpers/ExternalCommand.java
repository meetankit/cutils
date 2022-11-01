/**
 * ExternalCommand.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

/**
 * @author skaluska
 *
 */
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

public class ExternalCommand
{
  private static final String CMD_ERROR_FILE_FORMAT  = "cu-%s-err.log";
  private static final String CMD_OUTPUT_FILE_FORMAT = "cu-%s-out.log";
  /** for log messages */
  private static final Logger logger                 = Logger.getLogger(
      ExternalCommand.class.getName());

  /* inputs */
  /** external command to be executed */
  private String              command;
  /** directory in which command is to be executed */
  private String              directory;
  /** short one-word description of the task - used in any log file names */
  private String              taskName;
  /** wait for the command to complete - default is TRUE */
  private boolean             waitForCompletion;
  /** log output of the command - default is TRUE */
  private boolean             logOutput;

  /* derived */
  private String              outputFileName;
  private String              errorFileName;
  private Process             process;

  /**
   * Constructor for ExternalCommand
   */
  public ExternalCommand(String command, String directory, String task)
  {
    this.command = command;
    this.directory = directory;
    taskName = task;
    waitForCompletion = true;
    logOutput = true;
  }

  /**
   * @return the command
   */
  public String getCommand()
  {
    return command;
  }

  /**
   * @param command
   *          the command to set
   */
  public void setCommand(String command)
  {
    this.command = command;
  }

  /**
   * @return the directory
   */
  public String getDirectory()
  {
    return directory;
  }

  /**
   * @param directory
   *          the directory to set
   */
  public void setDirectory(String directory)
  {
    this.directory = directory;
  }

  /**
   * @return the taskName
   */
  public String getTaskName()
  {
    return taskName;
  }

  /**
   * @param taskName
   *          the taskName to set
   */
  public void setTaskName(String logName)
  {
    this.taskName = logName;
  }

  /**
   * @return the waitForCompletion
   */
  public boolean isWaitForCompletion()
  {
    return waitForCompletion;
  }

  /**
   * @param waitForCompletion
   *          the waitForCompletion to set
   */
  public void setWaitForCompletion(boolean waitForCompletion)
  {
    this.waitForCompletion = waitForCompletion;
  }

  public boolean isLogOutput()
  {
    return logOutput;
  }

  public void setLogOutput(boolean logOutput)
  {
    this.logOutput = logOutput;
  }

  /**
   * @return the outputFileName
   */
  public String getOutputFileName()
  {
    return outputFileName;
  }

  /**
   * @param outputFileName
   *          the outputFileName to set
   */
  public void setOutputFileName(String outputFileName)
  {
    this.outputFileName = outputFileName;
  }

  /**
   * @return the errorFileName
   */
  public String getErrorFileName()
  {
    return errorFileName;
  }

  /**
   * @param errorFileName
   *          the errorFileName to set
   */
  public void setErrorFileName(String errorFileName)
  {
    this.errorFileName = errorFileName;
  }

  /**
   * @return the process
   */
  public Process getProcess()
  {
    return process;
  }

  /**
   * execute Executes the command
   */
  public void execute()
  {
    // set environment
    Map<String, String> envMap = System.getenv();
    String[] env = new String[envMap.size()];
    int envPos = 0;
    for (Entry<String, String> envEntry : envMap.entrySet())
    {
      env[envPos++] = envEntry.getKey() + "=" + envEntry.getValue();
    }

    try
    {
      int ret;
      logger.finest(String.format("Executing...\n\t%s\n", command));
      process = Runtime.getRuntime().exec(command, env,
          new File(directory));

      if (logOutput)
      {
        errorFileName = directory + File.separator
            + String.format(CMD_ERROR_FILE_FORMAT, taskName);
        outputFileName = directory + File.separator
            + String.format(CMD_OUTPUT_FILE_FORMAT, taskName);
      }

      StreamConsumer errorConsumer = new StreamConsumer(errorFileName,
          process.getErrorStream());
      StreamConsumer outputConsumer = new StreamConsumer(outputFileName,
          process.getInputStream());
      Thread errorThread = new Thread(errorConsumer);
      errorThread.start();
      Thread outputThread = new Thread(outputConsumer);
      outputThread.start();
      if (waitForCompletion)
      {
        ret = process.waitFor();
        errorThread.join();
        outputThread.join();
        if (ret != 0)
        {
          throw new CuRuntimeException(
              CUMessages.CU_ERROR_err_EXECUTING_INTERNAL_COMMAND,
              Integer.toString(ret));
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("Error running: " + command);
      for (int i = 0; i < env.length; i++)
        System.err.println(env[i]);
      throw new CuRuntimeException(
          CUMessages.CU_ERROR_err_EXECUTING_INTERNAL_COMMAND, e.getMessage());
    }
  }
}
