/**
 * SplitterTest.java
 */
package com.adobe.dx.aep.poc.cutils.basics.test.expressions;

import java.io.Reader;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;

import com.adobe.dx.aep.poc.cutils.basics.helpers.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 *
 */
public class SplitterTest
{
  private static final TestInputs INPUTS[] = {
      new TestInputs("f1,f2,f3", ',', 10, 3,
          new String[] { "f1", "f2", "f3" }),
      new TestInputs("f1\tf2\tf3", '\t', 10, 3,
          new String[] { "f1", "f2", "f3" }),
      new TestInputs("f1 f2 f3", ' ', 10, 3,
          new String[] { "f1", "f2", "f3" }),
      new TestInputs("f1\\,,f2,f3", ',', 4, 5,
          new String[] { "f1,", "f2", "f3" }),
      new TestInputs("f1,f2,f3", ',', 2, 3,
          new String[] { "f1", "f2", "f3" }),
      new TestInputs("f1\\\\,f2,f3\"", ',', 3, 5,
          new String[] { "f1\\", "f2", "f3\"" }),
      new TestInputs("\"f1,,\",f2,\"f3,\"", ',', 10, 7,
          new String[] { "\"f1,,\"", "f2", "\"f3,\"" }),
  };

  @Test
  public void test1()
  {
    for (TestInputs s : INPUTS)
    {
      int max = s.getMaxFragments();
      String input = s.getString();
      char delim = s.getDelimiter();
      String expectedFields[] = s.getExpectedfields();
      int f2o = s.getField2offset();
      int numFields = Math.min(max, expectedFields.length);

      Reader reader = new StringReader(input);
      String fields[] = new String[numFields];
      StringUtils.splitter(reader, delim, true,
          t -> processRecord(t.fragmentNo, t.fragment, t.offset, f2o, fields,
              numFields), max, false, null);

      for (int i = 0; i < numFields; i++)
      {
        Assert.assertEquals(s.getExpectedfields()[i], fields[i]);
        // System.out.print(fields[i] + ",");
      }
      // System.out.println();
    }
  }

  private void processRecord(int pos, String fragment, int offset,
      int expectedOffset, String fields[], int maxFragments)
  {
    fields[pos] = fragment;
    Assert.assertTrue(pos < maxFragments);
    if (pos == 1)
      Assert.assertEquals(offset, expectedOffset);
  }

  @Getter(value = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class TestInputs
  {
    private String       string;

    private char         delimiter;

    private int          maxFragments;

    private int          field2offset;

    private final String expectedfields[];
  }
}
