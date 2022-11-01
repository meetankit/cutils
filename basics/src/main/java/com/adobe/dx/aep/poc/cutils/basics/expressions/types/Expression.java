/**
 * Expression.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sanjay
 *
 *         Represents an expression.
 */
@Getter
public abstract class Expression
{
  protected static final String INDENT_STR       = "| ";

  protected static final int    INDENTATION_INCR = INDENT_STR.length();

  /*
   * The data type may be known in some cases, e.g., in case of constants or
   * certain operators. In most other cases it is unknown.
   */
  private Dty                   dty;

  @Setter
  private String                stringRepresentation;

  protected Expression(String str, Dty t)
  {
    dty = t;
    stringRepresentation = str;
  }

  protected String indentation(int n)
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < n; i += INDENTATION_INCR)
      sb.append(INDENT_STR);
    return sb.toString();
  }

  public abstract String print(int indent);

  public abstract Value evaluate(VariableValues vv);
}
