/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.data;

/**
 * @author dbyrne
 *
 */
public class ImmutableDataException extends RuntimeException
{

	/**
	 * 
	 */
	public ImmutableDataException()
	{
	}

	/**
	 * @param message
	 */
	public ImmutableDataException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ImmutableDataException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ImmutableDataException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
