/**
 * Between.java
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
public class Between extends Operator
{
  public Between()
  {
    super(OperatorType.BETWEEN, Dty.BOOLEAN, 3, 3);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    /* if first arg is numeric do numeric promotions */
    if (args.get(0).getType().isNumeric())
      args = performNumericPromotions(args);
    else if (args.get(0).getType().equals(Dty.BOOLEAN))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_OPERATOR_op,
          args.get(0).getType().value(), getOperatorType().value());

    Value arg1 = args.get(0);
    Value arg2 = args.get(1);
    Value arg3 = args.get(2);

    if (!arg1.getType().equals(arg2.getType()))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg2.getType().value(),
          getOperatorType().value());

    if (!arg1.getType().equals(arg3.getType()))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg3.getType().value(),
          getOperatorType().value());

    boolean result = false;
    switch (arg1.getType())
    {
    case INTEGER:
      result = (arg2.coerceToInt() <= arg1.coerceToInt()) &&
          (arg1.coerceToInt() <= arg3.coerceToInt());
      break;
    case LONG:
      result = (arg2.coerceToLong() <= arg1.coerceToLong()) &&
          (arg1.coerceToLong() <= arg3.coerceToLong());
      break;
    case DOUBLE:
      result = (arg2.coerceToDouble() <= arg1.coerceToDouble()) &&
          (arg1.coerceToDouble() <= arg3.coerceToDouble());
      break;
    case STRING:
      result = (arg2.coerceToString().compareTo(arg1.coerceToString()) <= 0)
          && (arg1.coerceToString().compareTo(arg3.coerceToString()) <= 0);
      break;
    case DATE:
      result = (arg2.coerceToDate().compareTo(arg1.coerceToDate()) <= 0) &&
          (arg2.coerceToDate().compareTo(arg1.coerceToDate()) <= 0);
      break;
    default:
      break;
    }
    return new Value(Dty.BOOLEAN, result);
  }
}
