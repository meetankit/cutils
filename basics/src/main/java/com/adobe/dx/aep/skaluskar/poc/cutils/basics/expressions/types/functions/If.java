/**
 * If.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.functions;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class If extends Function
{
  private static final String NAME     = "if";
  private static final int    MIN_ARGS = 3;
  private static final int    MAX_ARGS = 3;

  @Override
  public String getName()
  {
    return NAME;
  }

  @Override
  public int getMinArgs()
  {
    return MIN_ARGS;
  }

  @Override
  public int getMaxArgs()
  {
    return MAX_ARGS;
  }

  @Override
  public Value evaluate(List<Value> arguments)
  {
    Value cond = arguments.get(0);

    if (cond.coerceToBoolean())
      return arguments.get(1);
    else
      return arguments.get(2);
  }
}
