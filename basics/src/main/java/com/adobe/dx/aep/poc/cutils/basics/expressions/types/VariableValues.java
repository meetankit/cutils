/**
 * VariableValues.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sanjay
 *
 */
public class VariableValues
{
  private Map<String, Value> values;

  public VariableValues()
  {
    values = new HashMap<String, Value>();
  }

  public void addValue(String var, Value val)
  {
    values.put(var, val);
  }

  public Value getValue(String var)
  {
    return values.get(var);
  }
}
