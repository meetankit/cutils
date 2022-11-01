/**
 * Month.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "month")
@XmlEnum
public enum Month
{
  @XmlEnumValue("JANUARY")
  JANUARY("JANUARY", 1),
  @XmlEnumValue("FEBRUARY")
  FEBRUARY("FEBRUARY", 2),
  @XmlEnumValue("MARCH")
  MARCH("MARCH", 3),
  @XmlEnumValue("APRIL")
  APRIL("APRIL", 4),
  @XmlEnumValue("MAY")
  MAY("MAY", 5),
  @XmlEnumValue("JUNE")
  JUNE("JUNE", 6),
  @XmlEnumValue("JULY")
  JULY("JULY", 7),
  @XmlEnumValue("AUGUST")
  AUGUST("AUGUST", 8),
  @XmlEnumValue("SEPTEMBER")
  SEPTEMBER("SEPTEMBER", 9),
  @XmlEnumValue("OCTOBER")
  OCTOBER("OCTOBER", 10),
  @XmlEnumValue("NOVEMBER")
  NOVEMBER("NOVEMBER", 11),
  @XmlEnumValue("DECEMBER")
  DECEMBER("DECEMBER", 12);

  private final String value;
  private int          monthNumber;

  Month(String v, int month)
  {
    value = v;
    monthNumber = month;
  }

  public String value()
  {
    return value;
  }

  public int getMonthNumber()
  {
    return monthNumber;
  }

  public static Month fromValue(String v)
  {
    if (v == null)
      return null;

    for (Month c : Month.values())
    {
      if (c.value.equals(v))
      {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  public static Month fromNumber(int no)
  {
    for (Month m : Month.values())
    {
      if (m.monthNumber == no)
        return m;
    }
    throw new IllegalArgumentException(String.valueOf(no));
  }
}
