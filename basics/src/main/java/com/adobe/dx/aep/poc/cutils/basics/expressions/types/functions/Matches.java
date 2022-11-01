/**
 * Matches.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.functions;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class Matches extends Function
{
  private static final String NAME     = "matches";
  private static final int    MIN_ARGS = 2;
  private static final int    MAX_ARGS = 2;

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
    Value s = arguments.get(0);
    Value regex = arguments.get(1);
    if (s.getType() != Dty.STRING)
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_FUNCTION_func,
          s.getType().value(), NAME);
    if (regex.getType() != Dty.STRING)
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_FUNCTION_func,
          regex.getType().value(), NAME);
    return new Value(Dty.BOOLEAN,
        ((String) s.getValue()).matches((String) regex.getValue()));
  }
}
