/**
 * FunctionWrapper.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.interfaces.IFunction;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Expression;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Operator;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.OperatorType;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sanjay
 *
 */
public class FunctionWrapper extends Operator
{
  /** list of known functions indexed by name */
  private static final Map<String, IFunction> knownFunctions;

  static
  {
    ServiceLoader<IFunction> functionLoader = ServiceLoader.load(
        IFunction.class);
    knownFunctions = new HashMap<String, IFunction>();
    if (functionLoader != null)
    {
      for (IFunction f : functionLoader)
      {
        knownFunctions.put(f.getName(), f);
      }
    }
  }

  @Getter
  @Setter
  private String name;

  public FunctionWrapper(String fname, List<Expression> args)
  {
    super(OperatorType.FUNCTION, Dty.UNKNOWN, 0, Integer.MAX_VALUE);
    name = fname;
    if (!knownFunctions.containsKey(name))
      throw new CuRuntimeException(CUMessages.CU_NO_SUCH_FUNCTION_func, name);
    if (args != null)
    {
      for (Expression arg : args)
        addArg(arg);
    }
  }

  @Override
  public String getOperatorRepresentation()
  {
    return name;
  }

  @Override
  public Value evaluateOperator(List<Value> args)
  {
    IFunction func = knownFunctions.get(name);
    int numArgs = (args == null) ? 0 : args.size();
    if ((numArgs < func.getMinArgs()) || (numArgs > func.getMaxArgs()))
      throw new CuRuntimeException(
          CUMessages.CU_WRONG_NUMBER_OF_ARGUMENTS_num_FOR_FUNCTION_func,
          String.valueOf(numArgs), name);
    return func.evaluate(args);
  }
}
