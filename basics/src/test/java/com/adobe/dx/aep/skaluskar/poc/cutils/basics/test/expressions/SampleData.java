/**
 * SampleData.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.test.expressions;

/**
 * @author sanjay
 *
 */
public class SampleData
{
  public static final String TEST_EXPRESSIONS[] = {
      /* literals */
      "intX==+4",
      "intX<=4",
      "longX >= -4L",
      "longX < 888888l",
      "doubleX < 4.099e1",
      "doubleX > 4.099e-1",
      "doubleX < +100000.99e+2",
      "stringX != 'foo'",
      "stringX != 'foo\\n'",
      "stringX != 'foo\\t'",
      "stringX != 'foo\\r'",
      "stringX != 'foo\\\\'",
      "stringX == '\\u0053' /* the escape is for S */",
      "true",
      "!false",
      "dateX >= 2015-3-12",
      "dateX > 2007-2-07T07:12:01",
      "dateX == 2017-10-30T10:30:22+05:30",
      "matches(stringX, 'S.*')",
      "matches('foobar', '.+bar')",
      "if(matches('foobar', 'xx*'), 0, 1)",

      /* arithmetic terms */
      "intX * 10",
      "intX % 3",
      "intX / longX",
      "(intX + doubleX*2)",
      "intX - intX/2",
      "length(stringX)",

      /* arithmetic expressions */
      "doubleX + length(stringX)*2",
      "(longX / 2*intX) - 24*doubleX",
      "intX / 3 + intX % 3",
      "doubleX*longX - (257/(jsonX.num % 23))",
      "if(intX*23, doubleX, 0)",

      /* relational operators */
      "intX <= 4",
      "longX < 5",
      "intX == 4",
      "doubleX > 3",
      "doubleX >= 3.5",
      "intX != 3",
      "jsonX.num == 4",
      "jsonX.list[2] == 2",
      "arrayX[intX - 1] == 3",

      /* boolean terms */
      "(intX > 2) and (stringX != 'foo')",
      "!(longX == 2)",
      "(intX==4 or longX > 7) AND !(intX == 2)",
      "unknown is null",
      "stringX is NOT Null",
      "intX between 3 and 5.0",
      "dateX between 2015-3-12 AND 2018-1-1",
      "(stringX between 'A' and 'Z')",
      "intX in {4, 5, 7}",
      "stringX NOT IN {'A', 'B'}",

      /* boolean expressions */
      "(intX > 5) or !(longX==1)",
      "(intX == longX) OR ((doubleX > 100) and ! (intX))",
      "(doubleX > 0) and (intX IN {1, 2, 4, 8}) or (stringX between 'A' AND 'C')",
      "(intX is null) or (longX > 0.0) and intX not in {1,2, 3}",
      "if(intX > 0, intX < 5, intX > 5)",

      /* additional functions */
      "year(dateX, 'IST') == 2017",
      "month(dateX, 'IST') == 10",
      "monthName(dateX, 'IST') == 'OCTOBER'",
      "day(dateX, 'IST') == 30",
      "dayName(dateX, 'IST') == 'MONDAY'",
      "hour(dateX, 'IST') == 10",
      "min(dateX, 'IST') == 30",
      "sec(dateX, 'IST') == 22",
      "ip2city('216.58.196.174')",
      "ip2country('216.58.196.174')"
  };
}
