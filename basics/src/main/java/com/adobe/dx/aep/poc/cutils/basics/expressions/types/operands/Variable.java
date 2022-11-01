/**
 * Variable.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types.operands;

import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Expression;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;
import com.adobe.dx.aep.poc.cutils.basics.expressions.types.VariableValues;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sanjay
 *
 * Represents a variable in an expression.
 */
@Getter
@Setter
public class Variable extends Expression
{
  private String name;

  public Variable(String nm, Dty t)
  {
    super(nm, t);
    name = nm;
  }

  public Variable(String nm)
  {
    this(nm, Dty.UNKNOWN);
  }

  @Override
  public Value evaluate(VariableValues vv)
  {
    return vv.getValue(name);
  }

  @Override
  public String print(int indent)
  {
    return getStringRepresentation();
  }
}
