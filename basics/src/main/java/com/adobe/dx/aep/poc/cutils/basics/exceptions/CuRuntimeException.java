/**
 * CuRuntimeException.java
 */
package com.adobe.dx.aep.poc.cutils.basics.exceptions;

import com.adobe.dx.aep.poc.cutils.basics.interfaces.IErrorMessage;

/**
 * @author admin
 *
 *         Common unchecked exception class for all components
 */
public class CuRuntimeException extends RuntimeException
{
  /** CuException.java */
  private static final long serialVersionUID = 1L;

  private IErrorMessage        message;

  public IErrorMessage getMessageEnum()
  {
    return message;
  }

  /**
   * Constructor for CuRuntimeException
   * 
   * Constructs an exception with a message
   * 
   * @param m
   *          - message
   * 
   */
  public CuRuntimeException(IErrorMessage m)
  {
    super(m.getMessage());
    message = m;
  }

  /**
   * Constructor for CuRuntimeException
   * 
   * Constructs an exception with a message and optional message fields
   * 
   * @param m
   *          - message
   * @param strings
   *          - list of message field values
   */
  public CuRuntimeException(IErrorMessage m, String... strings)
  {
    super(String.format(m.getMessage(), (Object[]) strings));
    message = m;
  }

  /**
   * Constructor for CuRuntimeException
   * 
   * Constructs an exception with a message and cause
   * 
   * @param m
   *          - message
   * @param e
   *          - cause
   */
  public CuRuntimeException(IErrorMessage m, Exception e)
  {
    super(m.getMessage(), e);
    message = m;
  }

  /**
   * Constructor for CuRuntimeException
   * 
   * Constructs an exception with a message, cause and optional message fields
   * 
   * @param m
   *          - message
   * @param e
   *          - cause
   * @param strings
   *          - list of message field values
   */
  public CuRuntimeException(IErrorMessage m, Exception e, String... strings)
  {
    super(String.format(m.getMessage(), (Object[]) strings), e);
    message = m;
  }
}
