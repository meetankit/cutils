/**
 * SerDeUtils.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.serde;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers.StringUtils;

/**
 * @author skaluska
 * 
 */
public class SerDeUtils
{
  /**
   * Serializes specified object into XML using JAXB marshaller.
   * 
   * @param inputObject
   *          - input object to serialize
   * @param classes
   *          - list of classes present in the input object
   * 
   * @return string containing the serialized XML in UTF-8 encoding
   */
  public static String serializeUsingJaxb(Object inputObject,
      Class<?>... classes)
  {
    String outputString = null;

    if (inputObject != null)
    {
      try
      {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        JAXBContext jaxbCtx = JAXBContext.newInstance(classes);
        Marshaller m = jaxbCtx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(inputObject, bout);
        byte[] outputBytes = bout.toByteArray();
        outputString = new String(outputBytes, StandardCharsets.UTF_8);
      }
      catch (JAXBException e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }

    return outputString;
  }

  /**
   * Serializes specified object into XML to the specified file.
   * 
   * @param inputObject
   *          - input object to serialize
   * @param outputFile
   *          - file to serialize to
   * @param classes
   *          - list of classes present in the input object
   */
  public static void serializeXmlToFile(Object inputObject, String outputFile,
      Class<?>... classes)
  {
    if (inputObject != null)
    {
      BufferedWriter writer = null;
      try
      {
        writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(outputFile), StandardCharsets.UTF_8));

        JAXBContext jaxbCtx = JAXBContext.newInstance(classes);
        Marshaller m = jaxbCtx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(inputObject, writer);
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

  /**
   * Deserializes an object using JAXB unmarshaller from an input XML string.
   * 
   * @param inputString
   *          - input string containing the object XML in UTF-8 encoding
   * @param classes
   *          - list of classes needed to de-serialize
   * @return deserialized object
   */
  public static Object deSerializeUsingJaxb(String inputString,
      Class<?>... classes)
  {
    Object output = null;

    if (!StringUtils.isNullOrEmpty(inputString))
    {
      try
      {
        StringReader xmlReader = new StringReader(new String(inputString));
        JAXBContext jaxbCtx = JAXBContext.newInstance(classes);
        Unmarshaller um = jaxbCtx.createUnmarshaller();
        output = um.unmarshal(xmlReader);
      }
      catch (JAXBException e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }

    return output;
  }

  /**
   * Deserializes an object using JAXB unmarshaller from an input file.
   * 
   * @param inputFile
   *          - input file containing the object XML in UTF-8 encoding
   * @param classes
   *          - list of classes needed to de-serialize
   * @return deserialized object
   */
  public static Object deSerializeXmlFromFile(String inputFile,
      Class<?>... classes)
  {
    Object output = null;

    if (!StringUtils.isNullOrEmpty(inputFile))
    {
      BufferedReader reader = null;
      try
      {
        reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(inputFile), StandardCharsets.UTF_8));
        JAXBContext jaxbCtx = JAXBContext.newInstance(classes);
        Unmarshaller um = jaxbCtx.createUnmarshaller();
        output = um.unmarshal(reader);
      }
      catch (Exception e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
      finally
      {
        try
        {
          if (reader != null)
            reader.close();
        }
        catch (IOException e)
        {
          throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
        }
      }
    }

    return output;
  }

  /**
   * Serializes specified object to a JSON string. Returns empty string if
   * object is null.
   * 
   * @param o
   *          - object to be serialized
   * 
   * @return serialized JSON string
   */
  public static String serializeToJson(Object o)
  {
    ObjectMapper om = new ObjectMapper();
    String s = "";
    try
    {
      if (o != null)
      {
        s = om.writeValueAsString(o);
      }
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }

    return s;
  }

  /**
   * Serializes specified object to a JSON string. Returns empty string if
   * object is null.
   * 
   * @param o
   *          - object to be serialized
   * 
   * @param prettyPrint
   *          - whether output string should be formatted to be readable
   * 
   * @return serialized JSON string
   */
  public static String serializeToJson(Object o, boolean prettyPrint)
  {
    ObjectMapper om = new ObjectMapper();
    String s = "";
    try
    {
      if (o != null)
      {
        if (prettyPrint)
          s = om.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        else
          s = om.writeValueAsString(o);
      }
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }

    return s;
  }

  /**
   * De-serializes object from a JSON string. Returns null if string is null or
   * empty.
   * 
   * @param json
   *          - string to be de-serialized
   * 
   * @return object after de-serialization
   */
  public static Object deserializeFromJson(String json, Class<?> valueType)
  {
    Object ret = null;
    ObjectMapper om = new ObjectMapper();

    if (!StringUtils.isNullOrEmpty(json))
    {
      try
      {
        ret = om.readValue(json, valueType);
      }
      catch (Exception e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }

    return ret;
  }

  /**
   * De-serializes map from a JSON string. Returns null if string is null or
   * empty. The values are represented as string, so if they are objects they
   * will be stored in the map as JSON strings.
   * 
   * @param json
   *          - map to be de-serialized
   * 
   * @return map after de-serialization
   */
  public static Map<String, Object> deserializeMapFromJson(String json,
      Class<?> valueClass)
  {
    Map<String, Object> ret = null;
    ObjectMapper om = new ObjectMapper();

    if (!StringUtils.isNullOrEmpty(json))
    {
      try
      {
        ret = new HashMap<String, Object>();
        JsonNode o = om.readTree(json);
        Iterator<String> allFields = o.fieldNames();
        while (allFields.hasNext())
        {
          String key = allFields.next();
          Object value = om.treeToValue(o.get(key), valueClass);
          ret.put(key, value);
        }
      }
      catch (Exception e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }

    return ret;
  }
}
