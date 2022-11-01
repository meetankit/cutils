/**
 * IFunction.java
 */
package com.adobe.dx.aep.poc.cutils.basics.expressions.interfaces;

import java.util.List;

import com.adobe.dx.aep.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public interface IFunction
{
  public String getName();

  public int getMinArgs();

  public int getMaxArgs();

  public Value evaluate(List<Value> arguments);
}
