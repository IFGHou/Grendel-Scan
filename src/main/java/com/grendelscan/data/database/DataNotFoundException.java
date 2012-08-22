package com.grendelscan.data.database;

public class DataNotFoundException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public DataNotFoundException()
	{
		super();
	}

	public DataNotFoundException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public DataNotFoundException(String arg0)
	{
		super(arg0);
	}

	public DataNotFoundException(Throwable arg0)
	{
		super(arg0);
	}

}
