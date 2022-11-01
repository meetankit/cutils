/**
 * Dty.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

/**
 * @author sanjay
 *
 * Data type for an expression
 */
public enum Dty
{
  INTEGER,
  LONG,
  DOUBLE,
  STRING,
  BOOLEAN,
  DATE,
  JSON_STRUCT,
  JSON_ARRAY,
  UNKNOWN;

  public String value()
  {
    return name();
  }

  public static Dty fromValue(String v)
  {
    return valueOf(v);
  }

  public static String[] stringvalues()
  {
    String[] ret = new String[Dty.values().length];

    int i = 0;
    for (Dty s : Dty.values())
      ret[i++] = s.value();

    return ret;
  }

  public boolean isNumeric()
  {
    return (this == INTEGER) || (this == LONG) || (this == DOUBLE);
  }
}
