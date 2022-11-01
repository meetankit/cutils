/**
 * Index.java
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
public class Index extends Operator
{
  public Index()
  {
    super(OperatorType.INDEX, Dty.UNKNOWN, 2, 2);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    Value arg1 = args.get(0);
    Value arg2 = args.get(1);

    if ((arg2.getType() != Dty.INTEGER)
        || (arg1.getType() != Dty.JSON_ARRAY))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg2.getType().value(),
          getOperatorType().value());

    List<?> list = arg1.coerceToList();
    int index = (Integer) arg2.getValue() - 1;
    if ((list == null) || (index >= list.size()))
      throw new CuRuntimeException(
          CUMessages.CU_NO_VALUE_WITH_SPECIFIED_INDEX_index_FOUND_IN_value,
          String.valueOf(index), arg1.getStringRepresentation());
    Object value = list.get(index);
    return new Value(Value.inferDataType(value), value);
  }
}
