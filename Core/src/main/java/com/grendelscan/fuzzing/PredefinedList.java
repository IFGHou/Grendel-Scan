package com.grendelscan.fuzzing;

public class PredefinedList implements FuzzVector
{
	private String[] strings;
	public String[] getStrings()
    {
    	return strings;
    }

	private int current;
	
	public PredefinedList(String[] strings)
    {
	    super();
	    this.strings = strings;
	    reset();
    }

	@Override
	public boolean done()
	{
		return current >= strings.length;
	}
	
	@Override
	public String getNextValue()
	{
		String value = null;
		if (!done())
		{
			value = strings[current++];
		}
		return value;
	}
	
	@Override
	public void reset()
	{
		current = 0;
	}
}
