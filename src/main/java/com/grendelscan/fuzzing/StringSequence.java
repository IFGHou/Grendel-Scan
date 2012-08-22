package com.grendelscan.fuzzing;

import java.util.Arrays;

public class StringSequence implements FuzzVector
{
	private int minDigits, maxDigits;
	private char[] characters;
	private int[] current;
	private boolean done;
	
	
	public int getMinDigits()
    {
    	return minDigits;
    }

	public int getMaxDigits()
    {
    	return maxDigits;
    }

	public char[] getCharacters()
    {
    	return characters;
    }

	@Override
	public String getNextValue()
	{
		String value = arrayToString(current);
		increment(current);
		return value;
	}
	
	private void increment(int[] values)
	{
		int index = 0;
		if (max(values))
		{
			done = true;
		}
		else
		{
			while (true && index < values.length)
			{
				if (++values[index] >= characters.length)
				{
					values[index] = 0;
					index++;
				}
				else
				{
					break;
				}
			}
		}
	}
	
	private boolean max(int[] values)
	{
		for (int i = 0; i < values.length; i++)
        {
	        if (values[i] < characters.length)
	        {
	        	return false;
	        }
        }
		return true;
	}
	
	private String arrayToString(int[] values)
	{
		String value = "";
		for (int i = values.length - 1; i >= 0 ; i--)
        {
			if (values[i] >= 0)
			{
				value += characters[values[i]];
			}
        }
		return value;
	}
	
	
	public StringSequence(char[] characters, int maxDigits, int minDigits)
    {
	    this.characters = characters;
	    Arrays.sort(this.characters);
	    this.maxDigits = maxDigits;
	    this.minDigits = minDigits;
	    reset();
    }

	@Override
	public void reset()
	{
		done = false;
	    current = new int[maxDigits];
	    for (int i = 0; i < current.length; i++)
        {
	    	if (i < minDigits)
	    	{
			    current[i] = 0;
	    	}
	    	else
	    	{
	    		current[i] = -1;
	    	}
        }
	}

	@Override
	public boolean done()
    {
	    return done;
    }
	
}
