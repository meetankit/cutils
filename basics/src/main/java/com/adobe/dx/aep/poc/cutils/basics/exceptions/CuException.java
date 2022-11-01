/**
 * CuException.java
 */
package com.adobe.dx.aep.poc.cutils.basics.exceptions;

import com.adobe.dx.aep.poc.cutils.basics.interfaces.IErrorMessage;

/**
 * @author admin
 *
 *         Common checked exception class for all components
 */
public class CuException extends Exception
{
  /** CuException.java */
  private static final long serialVersionUID = 1L;

  private IErrorMessage     message;

  public IErrorMessage getMessageEnum()
  {
    return message;
  }

  /**
   * Constructor for CuException
   * 
   * Constructs an exception with a message
   * 
   * @param m
   *          - message
   */
  public CuException(IErrorMessage m)
  {
    super(m.getMessage());
    message = m;
  }

  /**
   * Constructor for CuException
   * 
   * Constructs an exception with a message and optional message fields
   * 
   * @param m
   *          - message
   * @param strings
   *          - list of message field values
   */
  public CuException(IErrorMessage m, String... strings)
  {
    super(String.format(m.getMessage(), (Object[]) strings));
    message = m;
  }

  /**
   * Constructor for CuException
   * 
   * Constructs an exception with a message and cause
   * 
   * @param m
   *          - message
   * @param e
   *          - cause
   */
  public CuException(IErrorMessage m, Exception e)
  {
    super(m.getMessage(), e);
    message = m;
  }

  /**
   * Constructor for CuException
   * 
   * Constructs an exception with a message, cause and optional message fields
   * 
   * @param m
   *          - message
   * @param e
   *          - cause
   * @param arg
   *          - list of message field values
   */
  public CuException(IErrorMessage m, Exception e, String... strings)
  {
    super(String.format(m.getMessage(), (Object[]) strings), e);
    message = m;
  }
}
