/**
 * 
 */
package com.grendelscan.commons.formatting;

/**
 * @author david
 * 
 */
public class DataFormatException extends Exception
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public DataFormatException(final String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DataFormatException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public DataFormatException(final Throwable cause)
    {
        super(cause);
    }

}
