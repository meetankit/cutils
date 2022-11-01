/**
 * Operator.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

import java.util.ArrayList;
import java.util.List;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.poc.cutils.basics.helpers.StringUtils;

import lombok.Getter;

/**
 * @author sanjay
 *
 */
@Getter
public abstract class Operator extends Expression
{
  private static final String SPACE             = " ";

  private static final String STR_REP_BEGIN     = "(\"";

  private static final String STR_REP_END       = "\")";

  private static final String ARGLIST_BEGIN     = "(";

  private static final String ARGLIST_SEPARATOR = ", ";

  private static final String ARGLIST_END       = ")";

  private OperatorType        operatorType;

  protected List<Expression>  args;

  protected int               minArgs;

  protected int               maxArgs;

  protected boolean           nullArgsAllowed;

  protected Operator(OperatorType oType, Dty dtype, int min, int max)
  {
    this(oType, dtype, min, max, false);
  }

  protected Operator(OperatorType oType, Dty dtype, int min, int max,
      boolean nullArgs)
  {
    super(oType.getStringRepresentation(), dtype);
    operatorType = oType;
    minArgs = min;
    maxArgs = max;
    nullArgsAllowed = nullArgs;
  }

  public void addArg(Expression arg)
  {
    if (args == null)
      args = new ArrayList<Expression>();

    if (args.size() >= maxArgs)
      throw new CuRuntimeException(
          CUMessages.CU_TOO_MANY_ARGUMENTS_FOR_OPERATOR_op,
          getOperatorRepresentation());

    args.add(arg);
    setStringRepresentation(computeStringRepresentation());
  }

  public void addArgs(Expression... operands)
  {
    if (operands.length > 0)
    {
      for (Expression o : operands)
        addArg(o);
    }
  }

  private String computeStringRepresentation()
  {
    StringBuilder sb = new StringBuilder();
    if (args != null)
    {
      int numArgs = args.size();
      boolean unaryOrMultiArg = (minArgs != maxArgs) || (minArgs == 1)
          || (minArgs > 2);
      boolean binary = (minArgs == maxArgs) && (minArgs == 2);

      /* unary operator or varying number of args */
      if (unaryOrMultiArg)
      {
        /* add operator as prefix */
        sb.append(getOperatorRepresentation());
      }

      /* add opening parenthesis */
      sb.append(ARGLIST_BEGIN);

      /* add operator either as prefix or infix */
      for (int i = 0; i < numArgs; i++)
      {
        if (unaryOrMultiArg && (i > 0) && (i < numArgs))
        {
          /* add arg separator */
          sb.append(ARGLIST_SEPARATOR);
        }
        else if (binary && (i == 1))
        {
          /* binary operator - add operator as infix */
          sb.append(SPACE);
          sb.append(getOperatorRepresentation());
          sb.append(SPACE);
        }

        /* add arguments */
        sb.append(args.get(i).getStringRepresentation());
      }

      /* add closing parenthesis */
      sb.append(ARGLIST_END);
    }
    else
      sb.append(getOperatorRepresentation());
    return sb.toString();
  }

  @Override
  public String print(int indent)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getOperatorRepresentation());
    sb.append(STR_REP_BEGIN);
    sb.append(getStringRepresentation());
    sb.append(STR_REP_END);
    sb.append(", dataType: ");
    sb.append(getDty().value());
    if (args != null)
    {
      int newIndent = indent + INDENTATION_INCR;
      String newIndentStr = indentation(newIndent);
      for (Expression arg : args)
      {
        sb.append(StringUtils.STR_NEWLINE);
        sb.append(newIndentStr);
        sb.append(arg.print(newIndent));
      }
    }
    return sb.toString();
  }

  @Override
  public Value evaluate(VariableValues vv)
  {
    int numArgs = args.size();
    if ((numArgs < minArgs) || (numArgs > maxArgs))
      throw new CuRuntimeException(
          CUMessages.CU_WRONG_NUMBER_OF_ARGUMENTS_num_FOR_OPERATOR_op,
          String.valueOf(args.size()), getOperatorRepresentation());

    /* get the argument values */
    List<Value> argValues = new ArrayList<Value>();
    for (Expression arg : args)
    {
      Value argValue = arg.evaluate(vv);
      if ((argValue == null) && !nullArgsAllowed)
        throw new CuRuntimeException(
            CUMessages.CU_UNEXPECTED_NULL_ARGUMENT_VALUE_FOR_OPERATOR_op,
            getOperatorRepresentation());
      argValues.add(argValue);
    }

    /* evaluate the operator */
    return evaluateOperator(argValues);
  }

  /**
   * Converts all arguments to a common numeric type by promoting some of them
   * as needed (e.g., int to long, long to double, etc.).
   * 
   * @param argValues
   *          - list of argument values
   * @return converted argument values (can be the input list if no conversions
   *         are needed)
   * @throws CuRuntimeException
   *           if any argument value is not numeric
   */
  public List<Value> performNumericPromotions(List<Value> argValues)
  {
    List<Value> newArgValues = argValues;

    /* check if there are heterogenous values */
    int numArgs = argValues.size();
    int numInts = 0;
    int numLongs = 0;
    int numDoubles = 0;
    for (Value arg : argValues)
    {
      if (arg.getType().equals(Dty.INTEGER))
        numInts++;
      else if (arg.getType().equals(Dty.LONG))
        numLongs++;
      else if (arg.getType().equals(Dty.DOUBLE))
        numDoubles++;
      else
        throw new CuRuntimeException(
            CUMessages.CU_FOUND_VALUE_value_OF_TYPE_typ_WHEN_EXPECTING_A_NUMERIC_VALUE,
            arg.getStringRepresentation(), arg.getType().value());
    }

    if ((numArgs != numInts)
        && (numArgs != numLongs)
        && (numArgs != numDoubles))
    {
      /* numeric promotions are needed */
      Dty targetType = Dty.DOUBLE;
      if (numDoubles == 0) // the values are a mix of int and long
        targetType = Dty.LONG;

      newArgValues = new ArrayList<Value>();
      for (Value arg : argValues)
      {
        Value newValue = arg;
        if (arg.getType() != targetType)
        {
          if (targetType == Dty.DOUBLE)
            newValue = new Value(Dty.DOUBLE,
                Double.valueOf(arg.coerceToDouble()));
          else
            newValue = new Value(Dty.LONG,
                Long.valueOf(arg.coerceToLong()));
        }
        newArgValues.add(newValue);
      }
    }
    return newArgValues;
  }

  public abstract Value evaluateOperator(List<Value> argValues);

  public String getOperatorRepresentation()
  {
    return operatorType.getStringRepresentation();
  }
}
