/**
 * ExprFactory.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Expression;

/**
 * @author sanjay
 *
 */
public class ExprFactory
{
  private static Logger logger = LoggerFactory.getLogger(ExprFactory.class);
  
  public static Expression createExpression(String expr)
  {
    ExprParser p = new ExprParser(expr, logger.isTraceEnabled());
    if (p.yyparse() != 0)
      throw new CuRuntimeException(CUMessages.CU_ERROR_PARSING_EXPRESSION_expr, expr);
    Expression parseTree = (Expression) p.yyval.obj;
    
    logger.debug("Parsed expr: [{}]", expr);
    logger.debug("Printing parse tree\n{}", parseTree.print(0));
    
    return parseTree;
  }

  /**
   * main
   */
  public static void main(String[] args)
  {
    createExpression(args[0]);
  }
}
