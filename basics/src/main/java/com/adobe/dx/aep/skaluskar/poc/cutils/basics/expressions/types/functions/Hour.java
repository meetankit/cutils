/**
 * Hour.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.functions;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Value;

/**
 * @author sanjay
 *
 */
public class Hour extends Function
{
  private static final String NAME     = "hour";
  private static final int    MIN_ARGS = 2;
  private static final int    MAX_ARGS = 2;

  @Override
  public String getName()
  {
    return NAME;
  }

  @Override
  public int getMinArgs()
  {
    return MIN_ARGS;
  }

  @Override
  public int getMaxArgs()
  {
    return MAX_ARGS;
  }

  @Override
  public Value evaluate(List<Value> arguments)
  {
    Value s = arguments.get(0);
    Dty argType = s.getType();
    if ((argType != Dty.LONG) && (argType != Dty.DATE))
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_FUNCTION_func,
          s.getType().value(), NAME);

    Value timeZone = arguments.get(1);
    if (timeZone.getType() != Dty.STRING)
      throw new CuRuntimeException(
          CUMessages.CU_INCOMPATIBLE_TYPE_typ_FOR_FUNCTION_func,
          timeZone.getType().value(), NAME);

    /* get calendar instance */
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone((String) timeZone.getValue()));
    if (argType.isNumeric())
    {
      cal.setTimeInMillis(s.coerceToLong());
    }
    else
    {
      cal.setTime(s.coerceToDate());
    }

    return new Value(Dty.INTEGER, cal.get(Calendar.HOUR_OF_DAY));
  }
}
