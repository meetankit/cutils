/**
 * Ip2Country.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.functions;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;
import com.adobe.dx.aep.poc.cutils.basics.helpers.IPUtils;

/**
 * @author sanjay
 *
 */
public class Ip2Country extends Function
{
  private static final String NAME             = "ip2country";
  private static final int    MIN_ARGS         = 1;
  private static final int    MAX_ARGS         = 1;

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
    if (s.getType() != Dty.STRING)
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_FUNCTION_func,
          s.getType().value(), NAME);

    return new Value(Dty.STRING, IPUtils.getCountry(s.coerceToString()));
  }
}
