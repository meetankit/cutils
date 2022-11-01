/**
 * Value.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.serde.SerDeUtils;

import lombok.Getter;

/**
 * @author sanjay
 *
 */
@Getter
public class Value
{
  private static final char               ESCAPE_CHAR       = '\\';

  public static final String              UTC_DATE_DAY      = "yyyy-MM-dd";

  public static final String              UTC_DATE_DAY_TIME =
      "yyyy-MM-dd'T'HH:mm:ss";

  public static final String              UTC_DATE_FULL     =
      "yyyy-MM-dd'T'HH:mm:ssXXX";

  private static final Map<Class<?>, Dty> inferredType      =
      new HashMap<Class<?>, Dty>();

  static
  {
    inferredType.put(Integer.class, Dty.INTEGER);
    inferredType.put(Long.class, Dty.LONG);
    inferredType.put(Double.class, Dty.DOUBLE);
    inferredType.put(String.class, Dty.STRING);
    inferredType.put(Boolean.class, Dty.BOOLEAN);
    inferredType.put(Date.class, Dty.DATE);
    inferredType.put(Map.class, Dty.JSON_STRUCT);
    inferredType.put(ArrayList.class, Dty.JSON_ARRAY);
  }

  private Dty    type;

  private Object value;

  private String stringRepresentation;

  public Value(Dty t, Object v)
  {
    assign(t, v, v.toString());
  }

  public Value(Dty t, Object v, String rep)
  {
    assign(t, v, rep);
  }

  public void assign(Dty t, Object v, String rep)
  {
    validateValue(t, v);
    type = t;
    value = v;
    stringRepresentation = rep;
    if (type == Dty.STRING)
    {
      String s = coerceToString();
      value = replaceEscapedChars(s);
    }
  }

  public static void validateValue(Dty t, Object v)
  {
    if (t.equals(Dty.UNKNOWN))
      throw new CuRuntimeException(CUMessages.CU_UNKNOWN_DATATYPE);
    else if ((t.equals(Dty.STRING) && !(v instanceof String))
        || (t.equals(Dty.INTEGER) && !(v instanceof Integer))
        || (t.equals(Dty.LONG) && !(v instanceof Long))
        || (t.equals(Dty.DOUBLE) && !(v instanceof Double))
        || (t.equals(Dty.BOOLEAN) && !(v instanceof Boolean))
        || (t.equals(Dty.DATE) && !(v instanceof Date))
        || (t.equals(Dty.JSON_STRUCT) && !(v instanceof String)
            && !(v instanceof Map))
        || (t.equals(Dty.JSON_ARRAY) && !(v instanceof String)
            && !(v instanceof ArrayList<?>)))
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          String.valueOf(v), t.value());
  }

  public int coerceToInt()
  {
    int intValue;
    switch (type)
    {
    case INTEGER:
      intValue = ((Integer) value).intValue();
      break;
    case LONG:
      intValue = ((Long) value).intValue();
      break;
    case DOUBLE:
      intValue = ((Double) value).intValue();
      break;
    default:
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), "int");
    }
    return intValue;
  }

  public long coerceToLong()
  {
    long longValue;
    switch (type)
    {
    case INTEGER:
      longValue = ((Integer) value).longValue();
      break;
    case LONG:
      longValue = ((Long) value).longValue();
      break;
    case DOUBLE:
      longValue = ((Double) value).longValue();
      break;
    default:
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), "long");
    }

    return longValue;
  }

  public double coerceToDouble()
  {
    double doubleValue;
    switch (type)
    {
    case INTEGER:
      doubleValue = ((Integer) value).doubleValue();
      break;
    case LONG:
      doubleValue = ((Long) value).doubleValue();
      break;
    case DOUBLE:
      doubleValue = ((Double) value).doubleValue();
      break;
    default:
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), "double");
    }

    return doubleValue;
  }

  public String coerceToString()
  {
    if (type != Dty.STRING)
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), "string");
    return (String) value;
  }

  private static String replaceEscapedChars(String s)
  {
    StringBuilder sb = new StringBuilder();

    int pos = 0;
    while (pos < s.length())
    {
      char c = s.charAt(pos);
      if (c == ESCAPE_CHAR)
      {
        pos++;
        c = s.charAt(pos);
        if (c == 'u')
        {
          pos++;
          int hex = Integer.parseInt(s.substring(pos, pos + 4), 16);
          sb.append((char) hex);
          pos += 3;
        }
        else if (c == 'n')
          sb.append('\n');
        else if (c == 't')
          sb.append('\t');
        else if (c == 'r')
          sb.append('\r');
        else
          sb.append(c);
      }
      else
        sb.append(c);
      pos++;
    }

    return sb.toString();
  }

  public boolean coerceToBoolean()
  {
    boolean returnValue;

    switch (type)
    {
    case BOOLEAN:
      returnValue = (Boolean) value;
      break;
    case INTEGER:
      returnValue = ((Integer) value).intValue() != 0;
      break;
    case LONG:
      returnValue = ((Long) value).longValue() != 0;
      break;
    case DOUBLE:
      returnValue = ((Double) value).doubleValue() != 0;
      break;
    case DATE:
    case STRING:
      returnValue = (value != null);
      break;
    default:
      returnValue = false;
    }
    return returnValue;
  }

  public Date coerceToDate()
  {
    if (type != Dty.DATE)
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), "date");
    return (Date) value;
  }

  public static Date parseDate(String dateLiteral, String pattern)
      throws CuRuntimeException
  {
    SimpleDateFormat fmt = new SimpleDateFormat(pattern);
    Date d;
    try
    {
      d = fmt.parse(dateLiteral);
    }
    catch (ParseException e)
    {
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          e, dateLiteral, "date");
    }
    return d;
  }

  public Map<?, ?> coerceToMap()
  {
    if (type != Dty.JSON_STRUCT)
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), type.value());

    Map<?, ?> objectMap = null;
    if (value instanceof Map)
    {
      /* nothing to do if value is already a map */
      objectMap = (Map<?, ?>) value;
    }
    else
    {
      objectMap = (Map<?, ?>) SerDeUtils.deserializeFromJson((String) value,
          Map.class);
    }
    return objectMap;
  }

  public List<?> coerceToList()
  {
    if (type != Dty.JSON_ARRAY)
      throw new CuRuntimeException(
          CUMessages.CU_INVALID_VALUE_value_FOR_DATATYPE_type,
          value.toString(), type.value());

    List<?> list = null;
    if (value instanceof ArrayList)
    {
      /* nothing to do if value is already a list */
      list = (List<?>) value;
    }
    else
    {
      list = (List<?>) SerDeUtils.deserializeFromJson((String) value,
          List.class);
    }
    return list;
  }

  public static Dty inferDataType(Object o)
  {
    return inferredType.get(o.getClass());
  }
}
