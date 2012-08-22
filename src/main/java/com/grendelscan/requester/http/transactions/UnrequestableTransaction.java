package com.grendelscan.requester.http.transactions;

public class UnrequestableTransaction extends Exception
{

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
