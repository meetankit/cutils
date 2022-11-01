/**
 * Not.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Operator;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.OperatorType;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class Not extends Operator
{
  public Not()
  {
    super(OperatorType.NOT, Dty.BOOLEAN, 1, 1);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    Value arg1 = args.get(0);

    return new Value(Dty.BOOLEAN, !arg1.coerceToBoolean());
  }
}
