/**
 * NumericUtils.java
 */
package com.adobe.dx.aep.poc.cutils.basics.helpers;

/**
 * @author admin
 *
 */
public class NumericUtils
{
  public static Long coerceToLong(Object o)
  {
    if (o instanceof Long)
      return (Long) o;
    else if (o instanceof Integer)
      return Long.valueOf(((Integer) o).longValue());
    else if (o instanceof Short)
      return Long.valueOf(((Short) o).longValue());
    else
      return null;
  }

  public static Double coerceToDouble(Object o)
  {
    if (o instanceof Short)
      return Double.valueOf(((Short) o).doubleValue());
    else if (o instanceof Long)
      return Double.valueOf(((Long) o).doubleValue());
    else if (o instanceof Integer)
      return Double.valueOf(((Integer) o).doubleValue());
    else if (o instanceof Float)
      return Double.valueOf(((Float) o).doubleValue());
    else if (o instanceof Double)
      return (Double) o;
    else
      return null;
  }
}
