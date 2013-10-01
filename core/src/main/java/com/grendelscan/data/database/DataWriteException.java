package com.grendelscan.data.database;

public class DataWriteException extends Exception
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -781392570370194502L;

    public DataWriteException()
    {
    }

    public DataWriteException(final String arg0)
    {
        super(arg0);
    }

    public DataWriteException(final String arg0, final Throwable arg1)
    {
        super(arg0, arg1);
    }

    public DataWriteException(final Throwable arg0)
    {
        super(arg0);
    }

}
