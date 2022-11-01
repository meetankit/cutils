package com.adobe.dx.aep.poc.cutils.basics.test.expressions;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.dx.aep.poc.cutils.basics.expressions.parser.ExprFactory;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Expression;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.VariableValues;

public class ParserTest
{
  private static Logger logger = LoggerFactory.getLogger(ParserTest.class);

  private VariableValues variables;

  @Before
  public void setVariableValues()
  {
    variables = new VariableValues();
    
    /* add an int variable */
    Value intValue = new Value(Dty.INTEGER, 4);
    variables.addValue("intX", intValue);
    
    /* add an long variable */
    Value longValue = new Value(Dty.LONG, 4L);
    variables.addValue("longX", longValue);

    /* add an double variable */
    Value doubleValue = new Value(Dty.DOUBLE, 4.0);
    variables.addValue("doubleX", doubleValue);

    /* add an string variable */
    Value stringValue = new Value(Dty.STRING, "S");
    variables.addValue("stringX", stringValue);
    
    /* add an date variable */
    Date dt = Value.parseDate("2017-10-30T10:30:22+05:30", Value.UTC_DATE_FULL);
    Value dateValue = new Value(Dty.DATE, dt);
    variables.addValue("dateX", dateValue);

    /* add a JSON struct */
    Value jsonValue = new Value(Dty.JSON_STRUCT, "{\"str\": \"STRING\", \"num\": 4, \"list\": [1, 2]}");
    variables.addValue("jsonX", jsonValue);
    
    /* add a JSON array */
    Value arrayValue = new Value(Dty.JSON_ARRAY, "[1, 2, 3, 4]");
    variables.addValue("arrayX", arrayValue);
  }

  @Test
  public void testParseAndEval()
  {
    for (int i = 0; i < SampleData.TEST_EXPRESSIONS.length; i++)
    {
      String expr = SampleData.TEST_EXPRESSIONS[i];
      Expression ex = ExprFactory.createExpression(expr);
      Assert.assertNotNull(ex);

      Value v = ex.evaluate(variables);
      logger.debug("Evaluated expression [{}] as [{}]", expr, v.getStringRepresentation());
      Assert.assertTrue(v.coerceToBoolean());
    }
  }
}
