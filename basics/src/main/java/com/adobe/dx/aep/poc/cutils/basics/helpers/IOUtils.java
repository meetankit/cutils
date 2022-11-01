/**
 * IOUtils.java
 */
package com.adobe.dx.aep.poc.cutils.basics.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;

/**
 * @author admin
 *
 *         Some IO related utilities
 */
public class IOUtils
{
  /**
   * createNewFile
   * 
   * Creates a new file at the specified path. If the file already exists, it is
   * deleted and re-created. If some of the directories on the path don't exist
   * they are created.
   * 
   * @arg path - path where the file is to be created
   * 
   */
  static public void createNewFile(String path)
  {
    File file = new File(path);
    File dir = file.getParentFile();
    if (file.exists())
      file.delete();
    if ((dir != null) && !dir.exists())
      dir.mkdirs();
    try
    {
      file.createNewFile();
    }
    catch (IOException e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
    file.setReadable(true, true);
    file.setWritable(true);
  }

  /**
   * Deletes a directory including its subtree (equivalent of an rm -r). It does
   * nothing if the specified path doesn't correspond to a directory.
   * 
   * @param path
   *          - path of the directory to be deleted.
   */
  static public void deleteDir(String path)
  {
    File file = new File(path);
    if (file.exists() && file.isDirectory())
    {
      for (File f : file.listFiles())
      {
        if (f.isDirectory())
          deleteDir(f.getAbsolutePath());
        else
          f.delete();
      }
      file.delete();
    }
  }

  /**
   * createDir
   * 
   * Creates a directory at the specified path if it doesn't exist. If some of
   * the directories on the path don't exist they are created.
   * 
   * @arg path - path where the dir is to be created
   * 
   */
  static public void createDir(String path)
  {
    File file = new File(path);
    if (!file.exists())
      file.mkdirs();
  }

  /**
   * Writes content to a file. Creates the file (and missing directories) if it
   * doesn't exist, overwrites it if it exists.
   * 
   * @param path
   *          - path where file is to be written
   * @param content
   *          - content to be written
   */
  public static void writeToFile(String path, String content)
  {
    BufferedWriter writer = null;
    try
    {
      IOUtils.createNewFile(path);
      writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(path), StandardCharsets.UTF_8));
      writer.append(content);
      writer.flush();
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
    finally
    {
      try
      {
        if (writer != null)
          writer.close();
      }
      catch (IOException e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }
  }
}
