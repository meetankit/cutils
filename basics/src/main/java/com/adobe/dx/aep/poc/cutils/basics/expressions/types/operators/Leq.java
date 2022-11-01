/**
 * Leq.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.operators;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Operator;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.OperatorType;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class Leq extends Operator
{
  public Leq()
  {
    super(OperatorType.LEQ, Dty.BOOLEAN, 2, 2);
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

    boolean result = false;
    switch (arg1.getType())
    {
    case INTEGER:
      result = arg1.coerceToInt() <= arg2.coerceToInt();
      break;
    case LONG:
      result = arg1.coerceToLong() <= arg2.coerceToLong();
      break;
    case DOUBLE:
      result = arg1.coerceToDouble() <= arg2.coerceToDouble();
      break;
    case STRING:
      result = arg1.coerceToString().compareTo(arg2.coerceToString()) <= 0;
      break;
    case BOOLEAN:
      /* true > false */
      result = !(arg1.coerceToBoolean() && (!arg2.coerceToBoolean()));
      break;
    case DATE:
      result = arg1.coerceToDate().compareTo(arg2.coerceToDate()) <= 0;
      break;
    default:
      break;
    }
    return new Value(Dty.BOOLEAN, result);
  }
}
