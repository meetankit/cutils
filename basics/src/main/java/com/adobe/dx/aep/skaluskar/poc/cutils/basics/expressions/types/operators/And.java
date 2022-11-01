/**
 * And.java
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
public class And extends Operator
{
  public And()
  {
    super(OperatorType.AND, Dty.BOOLEAN, 2, 2);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    Value arg1 = args.get(0);
    Value arg2 = args.get(1);

    return new Value(Dty.BOOLEAN, arg1.coerceToBoolean()
        && arg2.coerceToBoolean());
  }
}
