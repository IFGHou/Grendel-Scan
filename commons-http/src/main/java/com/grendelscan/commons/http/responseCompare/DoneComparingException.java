package com.grendelscan.commons.http.responseCompare;

@SuppressWarnings("serial")
public class DoneComparingException extends Exception
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DoneComparingException.class);

	public DoneComparingException(String string)
    {
        super(string);
    }
}
