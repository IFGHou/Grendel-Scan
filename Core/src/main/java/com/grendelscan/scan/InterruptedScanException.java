/**
 * 
 */
package com.grendelscan.scan;

/**
 * @author david
 *
 */
public class InterruptedScanException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public InterruptedScanException()
	{
	}

	/**
	 * @param message
	 */
	public InterruptedScanException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public InterruptedScanException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InterruptedScanException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
