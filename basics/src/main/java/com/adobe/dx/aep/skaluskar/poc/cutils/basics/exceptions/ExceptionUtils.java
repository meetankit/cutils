/**
 * ExceptionUtils.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions;

/**
 * @author skaluska
 *
 */
public class ExceptionUtils
{
  public static Throwable userReportableError(Exception cuException)
  {
    Throwable firstUserError = null;
    for (Throwable e = cuException; e != null; e = e.getCause())
    {
      if ((e instanceof CuException) ||
          (e instanceof CuRuntimeException))
      {
        firstUserError = e;
      }
    }
    return firstUserError;
  }

  public static Throwable firstExternalError(Exception cuException)
  {
    Throwable firstExtErr = null;
    boolean prevErrorWasApp = false;
    for (Throwable e = cuException; e != null; e = e.getCause())
    {
      if ((e instanceof CuException) ||
          (e instanceof CuRuntimeException))
      {
        prevErrorWasApp = true;
      }
      else
      {
        if (prevErrorWasApp)
          firstExtErr = e;
        prevErrorWasApp = false;
      }
    }
    return firstExtErr;
  }
}
