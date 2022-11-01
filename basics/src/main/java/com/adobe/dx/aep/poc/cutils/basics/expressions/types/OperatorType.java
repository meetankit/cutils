/**
 * OperatorType.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.types;

import lombok.Getter;

/**
 * @author sanjay
 *
 */
public enum OperatorType
{
  /* equals */
  EQ("=="),
  /* not equals */
  NEQ("!="),
  /* less than */
  LT("<"),
  /* greater than */
  GT(">"),
  /* less than or equal to */
  LEQ("<="),
  /* greater than or equal to */
  GEQ(">="),
  /* in */
  IN("IN"),
  /* not in */
  NIN("NOT IN"),
  /* between */
  BETWEEN("BETWEEN"),
  /* AND */
  AND("AND"),
  /* OR */
  OR("OR"),
  /* NOT */
  NOT("!"),
  /* PLUS */
  PLUS("+"),
  /* MINUS */
  MINUS("-"),
  /* multiply */
  MULT("*"),
  /* divide */
  DIV("/"),
  /* modulus */
  MOD("%"),
  /* is null */
  NULL("IS NULL"),
  /* function */
  FUNCTION("FUNCTION"),
  /* field of a JSON object */
  FIELD("."),
  /* index within a JSON array */
  INDEX("[]");

  @Getter
  private String stringRepresentation;
  
  private OperatorType(String rep)
  {
    stringRepresentation = rep;
  }

  public String value()
  {
    return name();
  }

  public static OperatorType fromValue(String v)
  {
    return valueOf(v);
  }
}
