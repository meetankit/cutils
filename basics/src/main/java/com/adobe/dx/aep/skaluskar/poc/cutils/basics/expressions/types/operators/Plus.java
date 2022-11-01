/**
 * Plus.java
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
public class Plus extends Operator
{
  public Plus()
  {
    super(OperatorType.PLUS, Dty.UNKNOWN, 2, 2);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    /* if first arg is numeric do numeric promotions */
    if (args.get(0).getType().isNumeric())
      args = performNumericPromotions(args);
    else
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_OPERATOR_op,
          args.get(0).getType().value(), getOperatorType().value());

    Value arg1 = args.get(0);
    Value arg2 = args.get(1);

    if (!arg1.getType().equals(arg2.getType()))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg2.getType().value(),
          getOperatorType().value());

    Value result = null;
    switch (arg1.getType())
    {
    case INTEGER:
      result = new Value(Dty.INTEGER, arg1.coerceToInt() +
          arg2.coerceToInt());
      break;
    case LONG:
      result = new Value(Dty.LONG, arg1.coerceToLong() +
          arg2.coerceToLong());
      break;
    case DOUBLE:
      result = new Value(Dty.DOUBLE, arg1.coerceToDouble() +
          arg2.coerceToDouble());
      break;
    default:
      break;
    }
    return result;
  }

}
