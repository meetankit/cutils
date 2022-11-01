/**
 * DayTime.java
 */
package com.adobe.dx.aep.poc.cutils.basics.types;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dayTime")
public class DayTime
{
  private static final String DAY_SEPARATOR  = "-";
  private static final String TIME_SEPARATOR = ":";
  public static final String  TZ_GMT         = "GMT";

  @XmlElement(name = "day")
  private int                 dayOfMonth;

  private Month               month;

  private int                 year;

  @XmlElement(name = "hour")
  private int                 hourOfDay;

  @XmlElement(name = "min")
  private int                 mins;

  /**
   * @return the dayOfMonth
   */
  public int getDayOfMonth()
  {
    return dayOfMonth;
  }

  /**
   * @param dayOfMonth
   *          the dayOfMonth to set
   */
  public void setDayOfMonth(int day)
  {
    this.dayOfMonth = day;
  }

  /**
   * @return the month
   */
  public Month getMonth()
  {
    return month;
  }

  /**
   * @param month
   *          the month to set
   */
  public void setMonth(Month month)
  {
    this.month = month;
  }

  /**
   * @return the year
   */
  public int getYear()
  {
    return year;
  }

  /**
   * @param year
   *          the year to set
   */
  public void setYear(int year)
  {
    this.year = year;
  }

  /**
   * @return the hourOfDay
   */
  public int getHourOfDay()
  {
    return hourOfDay;
  }

  /**
   * @param hourOfDay
   *          the hourOfDay to set
   */
  public void setHourOfDay(int hour)
  {
    this.hourOfDay = hour;
  }

  /**
   * @return the mins
   */
  public int getMins()
  {
    return mins;
  }

  /**
   * @param mins
   *          the mins to set
   */
  public void setMins(int min)
  {
    this.mins = min;
  }

  public DayTime(int y, Month mo, int d, int h, int m)
  {
    year = y;
    month = mo;
    dayOfMonth = d;
    hourOfDay = h;
    mins = m;
  }

  public Date toDate()
  {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TZ_GMT));
    cal.set(year, month.getMonthNumber() - 1, dayOfMonth, hourOfDay, mins);
    Date sqlDate = new Date(cal.getTimeInMillis());
    return sqlDate;
  }

  public Timestamp toTimestamp()
  {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TZ_GMT));
    cal.set(year, month.getMonthNumber() - 1, dayOfMonth, hourOfDay, mins);
    Timestamp ts = new Timestamp(cal.getTimeInMillis());
    return ts;
  }

  public DayTime(Date d, TimeZone tz)
  {
    Calendar cal = Calendar.getInstance(tz);
    cal.setTimeInMillis(d.getTime());

    /*
     * subtract any DST offset and the offset for the timezone to get GMT time
     */
    cal.add(Calendar.MILLISECOND, -(cal.get(Calendar.DST_OFFSET) + tz
        .getRawOffset()));

    year = cal.get(Calendar.YEAR);
    month = Month.fromNumber(cal.get(Calendar.MONTH) + 1);
    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
    mins = cal.get(Calendar.MINUTE);
  }

  public DayTime(Timestamp ts, TimeZone tz)
  {
    Calendar cal = Calendar.getInstance(tz);
    cal.setTimeInMillis(ts.getTime());

    /*
     * subtract any DST offset and the offset for the timezone to get GMT time
     */
    cal.add(Calendar.MILLISECOND, -(cal.get(Calendar.DST_OFFSET) + tz
        .getRawOffset()));

    year = cal.get(Calendar.YEAR);
    month = Month.fromNumber(cal.get(Calendar.MONTH) + 1);
    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
    mins = cal.get(Calendar.MINUTE);
  }

  /**
   * Convenience method to construct a DayTime based on the current system time
   * and default timezone.
   */
  public DayTime()
  {
    this(new Timestamp(System.currentTimeMillis()), TimeZone.getDefault());
  }

  public static DayTime fromGmtDate(Date d)
  {
    DayTime ret = null;
    if (d != null)
      ret = new DayTime(d, TimeZone.getTimeZone(TZ_GMT));
    return ret;
  }

  public static DayTime fromDate(Date d, TimeZone tz)
  {
    DayTime ret = null;
    if (d != null)
      ret = new DayTime(d, tz);
    return ret;
  }

  public static DayTime fromGmtTimestamp(Timestamp ts)
  {
    DayTime ret = null;
    if (ts != null)
      ret = new DayTime(ts, TimeZone.getTimeZone(TZ_GMT));
    return ret;
  }

  public static DayTime fromTimestamp(Timestamp ts, TimeZone tz)
  {
    DayTime ret = null;
    if (ts != null)
      ret = new DayTime(ts, tz);
    return ret;
  }

  public static Date safeDate(DayTime d)
  {
    Date ret = null;
    if (d != null)
      ret = d.toDate();
    return ret;
  }

  public static Timestamp safeTimestamp(DayTime d)
  {
    Timestamp ret = null;
    if (d != null)
      ret = d.toTimestamp();
    return ret;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%2d", dayOfMonth));
    sb.append(DAY_SEPARATOR);
    sb.append(month.value().substring(0, 3));
    sb.append(DAY_SEPARATOR);
    sb.append(year);
    sb.append(TIME_SEPARATOR);
    sb.append(String.format("%2d", hourOfDay));
    sb.append(TIME_SEPARATOR);
    sb.append(String.format("%2d", mins));

    return sb.toString();
  }
}
