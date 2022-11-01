/**
 * WordSplitterTest.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.test.expressions;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers.WordSplitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author admin
 *
 */
public class WordSplitterTest
{
  private static final TestInputs INPUTS[] = {
      new TestInputs("passportnumber", new String[] {"passport", "number"}),
      new TestInputs("City_Firstname_Data-set5-17", new String[] {"first", "name", "city", "data", "set"}),
      new TestInputs("passport number", new String[] {"passport", "number"}),
      new TestInputs("nationalidentifier", new String[] {"national", "identifier"}),
      new TestInputs("nationalID#90210", new String[] {"national", "id"}),
      new TestInputs("firstname", new String[] {"first", "name"}),
      new TestInputs("name & ADDRESS", new String[] {"name", "address"}),
      new TestInputs("healthCardnumber", new String[] {"health", "card", "number"}),
      new TestInputs("PhoneNumber", new String[] {"phone", "number"}),
      new TestInputs("city-name", new String[] {"city", "name"}),
      new TestInputs("ACCOUNT.NO", new String[] {"account", "no"}),
      new TestInputs("NameOfCity", new String[] {"name", "of", "city"}),
      new TestInputs("CityName", new String[] {"name", "city"}),
      new TestInputs("City_FirstName_data-set3-10", new String[] {"first", "name", "city", "data", "set"}),
  };

  @Test
  public void test1()
  {
    WordSplitter ws = WordSplitter.getInstance();
    for (TestInputs s : INPUTS)
    {
      String input = s.input;
      List<String> output = ws.extractWords(input, false);
      for (String o : s.outputs)
        Assert.assertTrue(o + " not in " + input, output.contains(o));
    }
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class TestInputs
  {
    private String input;
    
    private String outputs[];
  }
}
