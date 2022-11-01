/**
 * ParserUtils.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.parser;

import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Expression;

/**
 * @author sanjay
 *
 */
public class ParserUtils
{
  public static final int INVALID_POSITION = -1;

  public static ExprParserVal newLexerNode(Expression e)
  {
    return new ExprParserVal(e);
  }
}
