package com.grendelscan.utils;

public class ArrayUtils
{

	public static byte[] copyOf(byte[] original)
	{
		return copyOfRange(original, 0, original.length);	
	}
	
	public static byte[] copyOfRange(byte[] original, int from, int to)
	{
		if (from < 0 || from > original.length)
		{
			throw new ArrayIndexOutOfBoundsException(from + " is less than zero or greater than the length of " + original.length);
		}
		if (from > to)
		{
			throw new IllegalArgumentException(from + " is greater than " + to);
		}
		int length = to - from + 1;
		byte[] copy = new byte[length];
		for (int i = 0; i < copy.length; i++)
        {
	        if (i + from >= original.length)
	        {
	        	copy[i] = 0;
	        }
	        else
	        {
	        	copy[i] = original[i + from];
	        }
        }
		return copy;
	}

}
