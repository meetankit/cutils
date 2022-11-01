/**
 * Eq.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Operator;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.OperatorType;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class Eq extends Operator
{
  public Eq()
  {
    super(OperatorType.EQ, Dty.BOOLEAN, 2, 2);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    /* if first arg is numeric do numeric promotions */
    if (args.get(0).getType().isNumeric())
      args = performNumericPromotions(args);

    Value arg1 = args.get(0);
    Value arg2 = args.get(1);

    if (!arg1.getType().equals(arg2.getType()))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg2.getType().value(),
          getOperatorType().value());

    return new Value(Dty.BOOLEAN, arg1.getValue().equals(arg2.getValue()));
  }
}
