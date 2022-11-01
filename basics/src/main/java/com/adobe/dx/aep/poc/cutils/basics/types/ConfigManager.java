/**
 * ConfigManager.java
 */
package com.adobe.dx.aep.poc.cutils.basics.types;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.helpers.StringUtils;

/**
 * @author admin
 *
 *         Helper functions to manage configuration
 */
public class ConfigManager
{
  public static Configuration getConfigForSubSystem(Configuration config,
      String subSystem)
  {
    return (config == null) ? null : config.subset(subSystem);
  }

  public static Configuration init(String configFile)
  {
    Configuration config = null;
    try
    {
      Parameters params = new Parameters();
      if (StringUtils.isNullOrEmpty(configFile))
      {
        BasicConfigurationBuilder<PropertiesConfiguration> builder =
            new BasicConfigurationBuilder<PropertiesConfiguration>(
                PropertiesConfiguration.class)
                    .configure(params.basic()
                        .setListDelimiterHandler(
                            new DefaultListDelimiterHandler(','))
                        .setThrowExceptionOnMissing(true));
        config = builder.getConfiguration();
      }
      else
      {
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                PropertiesConfiguration.class)
                    .configure(params.properties()
                        .setListDelimiterHandler(
                            new DefaultListDelimiterHandler(
                                StringUtils.CHAR_COMMA))
                        .setFileName(configFile));
        config = builder.getConfiguration();
      }
    }
    catch (RuntimeException e)
    {
      throw new CuRuntimeException(CUMessages.CU_ERROR_READING_CONFIG_FILE_file,
          e, configFile);
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_ERROR_READING_CONFIG_FILE_file,
          e, configFile);
    }

    return config;
  }

  public static void addProperty(Configuration config, String subSystem,
      String name, String value)
  {
    Configuration c = config.subset(subSystem);
    c.addProperty(name, value);
  }

  public static String getProperty(Configuration config, String subSystem,
      String name)
  {
    Configuration c = config.subset(subSystem);
    return c.getString(name);
  }

  public static void checkParam(Configuration config, String paramName,
      String prefix)
  {
    if (!config.containsKey(paramName))
      throw new CuRuntimeException(CUMessages.CU_MISSING_CONFIGURATION_FOR_prop,
          prefix + StringUtils.STR_PERIOD + paramName);
  }

  public static boolean containsParam(Configuration config, String paramName)
  {
    return config.containsKey(paramName);
  }
}
