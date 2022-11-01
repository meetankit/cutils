/**
 * In.java
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
public class In extends Operator
{
  public In()
  {
    super(OperatorType.IN, Dty.BOOLEAN, 2, Integer.MAX_VALUE);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    /* if first arg is numeric do numeric promotions */
    if (args.get(0).getType().isNumeric())
      args = performNumericPromotions(args);

    Value exprToFind = args.get(0);
    Dty exprType = exprToFind.getType();
    boolean found = false;
    for (int i = 1; i < args.size(); i++)
    {
      Value exprInList = args.get(i);

      if (!exprInList.getType().equals(exprType))
        throw new CuRuntimeException(
            CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
            exprType.value(), exprInList.getType().value(),
            getOperatorType().value());

      if (exprToFind.getValue().equals(exprInList.getValue()))
        found = true;
    }
    return new Value(Dty.BOOLEAN, found);
  }
}
