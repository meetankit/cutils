/**
 * Constant.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operands;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Expression;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.VariableValues;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sanjay
 *
 * Represents a constant value - either a literal or a computed constant.
 */
@Getter
@Setter
public class Constant extends Expression
{
  private Value value;

  public Constant(String s, Value v)
  {
    super(s, v.getType());
    value = v;
  }

  public Constant(String s, Dty t, Object v)
  {
    super(s, t);
    value = new Value(t, v, s);
  }

  @Override
  public Value evaluate(VariableValues vv)
  {
    return value;
  }

  @Override
  public String print(int indent)
  {
    return getStringRepresentation();
  }
}
