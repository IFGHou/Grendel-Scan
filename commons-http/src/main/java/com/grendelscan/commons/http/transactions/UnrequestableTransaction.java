package com.grendelscan.commons.http.transactions;

public class UnrequestableTransaction extends Exception
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public UnrequestableTransaction(final String arg0)
    {
        super(arg0);
    }

    public UnrequestableTransaction(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
