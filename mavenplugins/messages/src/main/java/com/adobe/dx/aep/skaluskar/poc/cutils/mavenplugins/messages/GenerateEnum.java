package com.adobe.dx.aep.skaluskar.poc.cutils.mavenplugins.messages;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @phase generate-sources
 * @goal generateMessages
 */
public class GenerateEnum extends AbstractMojo
{
  private static final String ENUM_BEGIN = "package %s;\n"
                                             + "import java.util.Locale;\n"
                                             + "import java.util.ResourceBundle;\n"
                                             + "public enum %s implements %s\n"
                                             + "{\n";

  private static final String ENUM_ITEM  = "\t%s%s(\"%s\"),\n";

  private static final String ENUM_END   = "\t;\n\tprivate String                errorCode;\n"
                                             + "\tprivate static ResourceBundle msgBundle = ResourceBundle.getBundle(\"%s\",\n"
                                             + "\t\tLocale.getDefault());\n"
                                             + "\tprivate %s(String m)\n"
                                             + "\t{\n\t\terrorCode = m;\n\t}\n"
                                             + "\tpublic String getMessage()\n"
                                             + "\t{\n\t\treturn msgBundle.getString(errorCode);\n\t}\n}";

  /**
   * This should be the path to the message file under the resources directory,
   * e.g., com/x/y/messages.properties
   *
   * @parameter
   */
  private String              baseDir;

  /**
   * This should be the path to the message file under the resources directory,
   * e.g., com/x/y/messages.properties
   * 
   * @parameter
   */
  private String              resourcePath;

  /**
   * Location of the generated enum
   * 
   * @parameter
   */
  private File                genDir;

  /**
   * Package name for generated enum
   * 
   * @parameter
   */
  private String              genPackage;

  /**
   * Name for generated enum
   * 
   * @parameter
   */
  private String              enumName;

  /**
   * Module code (typically 2 or 3 letter prefix).
   * This is optional.
   * 
   * @parameter
   */
  private String              moduleCode;

  /**
   * Interface to be implemented by the enum.
   * Should be the complete name including package.
   * 
   * @parameter
   */
  private String              messageInterface;

  private BufferedReader openMessageFile() throws FileNotFoundException
  {
    String messageFile = resourcePath;
    File mFile = new File(messageFile);
    if (!mFile.exists() && !mFile.isAbsolute())
    {
      messageFile = baseDir + File.separator + "src" + File.separator + "main"
          + File.separator + "resources" + File.separator + messageFile;
    }

    return new BufferedReader(new FileReader(messageFile));
  }

  private BufferedOutputStream openEnumFile() throws IOException
  {
    String enumDir = genDir.getAbsolutePath() + File.separator
        + genPackage.replace('.', File.separatorChar);
    File dir = new File(enumDir);
    if (!dir.isAbsolute())
    {
      enumDir = baseDir + File.separator + enumDir;
      dir = new File(enumDir);
    }
    dir.mkdirs();

    String enumFile = enumDir + File.separator + enumName + ".java";
    File file = new File(enumFile);
    if (file.exists()) file.delete();
    file.createNewFile();
    return new BufferedOutputStream(new FileOutputStream(enumFile));
  }

  public void execute() throws MojoExecutionException
  {
    try
    {
      BufferedReader in = openMessageFile();
      BufferedOutputStream out = openEnumFile();
      String enumBegin = String.format(ENUM_BEGIN, genPackage, enumName, messageInterface);
      out.write(enumBegin.getBytes());

      /* optionally prefix module code */
      String modulePrefix = "";
      if ((moduleCode != null) && (moduleCode.length() > 0))
        modulePrefix = moduleCode + "_";

      while (in.ready())
      {
        String line = in.readLine();
        if ((line != null) && (line.length() > 0) && line.contains("=")
            && !line.startsWith("#"))
        {
          String[] parts = line.split("=");
          String newItem = String.format(ENUM_ITEM, modulePrefix, parts[0],
              parts[0]);
          out.write(newItem.getBytes());
        }
      }
      in.close();

      String[] resourceNames = resourcePath.split("\\.");
      String resourceName = resourceNames[0].replace(File.separatorChar, '.');
      resourceName = resourceName.replace('/', '.');
      String enumEnd = String.format(ENUM_END, resourceName, enumName);
      out.write(enumEnd.getBytes());
      out.flush();
      out.close();
    }
    catch (Exception e)
    {
      throw new MojoExecutionException("Error processing messages", e);
    }
  }
}
