/**
 * Field.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.operators;

import java.util.List;
import java.util.Map;

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
public class Field extends Operator
{
  public Field()
  {
    super(OperatorType.FIELD, Dty.UNKNOWN, 2, 2);
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    Value arg1 = args.get(0);
    Value arg2 = args.get(1);

    if ((arg2.getType() != Dty.STRING)
        || (arg1.getType() != Dty.JSON_STRUCT))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPES_typ1_typ2_FOR_OPERATOR_op,
          arg1.getType().value(), arg2.getType().value(),
          getOperatorType().value());

    Map<?, ?> jsonObject = arg1.coerceToMap();
    String fieldName = (String) arg2.getValue();
    if (!jsonObject.containsKey(fieldName))
      throw new CuRuntimeException(
          CUMessages.CU_NO_FIELD_WITH_SPECIFIED_NAME_name_FOUND_IN_value,
          fieldName, arg1.getStringRepresentation());
    Object value = jsonObject.get(fieldName);
    return new Value(Value.inferDataType(value), value);
  }
}
