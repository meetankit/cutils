/**
 * Function.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.functions;

import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.expressions.interfaces.IFunction;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public abstract class Function implements IFunction
{
  @Override
  public String getName()
  {
    throw new CuRuntimeException(
        CUMessages.CU_UNIMPLEMENTED_OPERATION_op_FOR_FUNCTION_func,
        "getName", this.getClass().getSimpleName());
  }

  @Override
  public int getMinArgs()
  {
    throw new CuRuntimeException(
        CUMessages.CU_UNIMPLEMENTED_OPERATION_op_FOR_FUNCTION_func,
        "getMinArgs", this.getClass().getSimpleName());
  }

  @Override
  public int getMaxArgs()
  {
    throw new CuRuntimeException(
        CUMessages.CU_UNIMPLEMENTED_OPERATION_op_FOR_FUNCTION_func,
        "getMaxArgs", this.getClass().getSimpleName());
  }

  @Override
  public Value evaluate(List<Value> arguments)
  {
    throw new CuRuntimeException(
        CUMessages.CU_UNIMPLEMENTED_OPERATION_op_FOR_FUNCTION_func,
        "evaluate", this.getClass().getSimpleName());
  }
}
