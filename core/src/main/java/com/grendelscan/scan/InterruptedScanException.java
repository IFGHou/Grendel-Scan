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
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
    public InterruptedScanException()
    {
    }

    /**
     * @param message
     */
    public InterruptedScanException(final String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InterruptedScanException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public InterruptedScanException(final Throwable cause)
    {
        super(cause);
    }

}
