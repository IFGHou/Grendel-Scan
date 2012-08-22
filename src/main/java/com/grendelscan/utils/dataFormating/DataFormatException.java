/**
 * 
 */
package com.grendelscan.utils.dataFormating;

/**
 * @author david
 *
 */
public class DataFormatException extends Exception
{


	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @param message
	 */
	public DataFormatException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public DataFormatException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}


}
