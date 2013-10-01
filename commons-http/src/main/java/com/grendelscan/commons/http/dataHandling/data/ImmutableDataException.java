/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.data;

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
    public ImmutableDataException(final String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ImmutableDataException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ImmutableDataException(final Throwable cause)
    {
        super(cause);
    }

}
