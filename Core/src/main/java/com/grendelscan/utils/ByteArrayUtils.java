/**
 * 
 */
package com.grendelscan.utils;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author david
 *
 */
public class ByteArrayUtils
{
	private ByteArrayUtils()
	{
		
	}
	
	public static byte[][] split(byte[] array, byte ch)
	{
		ArrayList<byte[]> results = new ArrayList<byte[]>(2);
		int start = 0;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == ch)
			{
				results.add(Arrays.copyOfRange(array, start, i));
				start = i + 1;
			}
		}
		results.add(Arrays.copyOfRange(array, start, array.length));
		return results.toArray(new byte[results.size()][]);
	}

	public static byte[][] splitOnFirst(byte[] array, byte ch)
	{
		int split = -1;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == ch)
			{
				split = i;
				break;
			}
		}
		byte[][] results = new byte[2][];
		if (split == 0)
		{
			results[0] = new byte[0];
			results[1] = Arrays.copyOfRange(array, split + 1, array.length);
			return results;
		}
		else if (split > 0)
		{
			results[0] = Arrays.copyOfRange(array, 0, split);
			results[1] = Arrays.copyOfRange(array, split + 1, array.length);
			return results;
		}
		results[0] = array;
		return results;
	}
}
