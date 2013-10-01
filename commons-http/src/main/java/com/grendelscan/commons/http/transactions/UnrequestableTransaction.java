package com.grendelscan.commons.http.transactions;

public class UnrequestableTransaction extends Exception
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UnrequestableTransaction.class);

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public UnrequestableTransaction(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnrequestableTransaction(String arg0)
	{
		super(arg0);
	}

}
