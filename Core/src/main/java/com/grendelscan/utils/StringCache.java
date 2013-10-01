package com.grendelscan.utils;

import java.util.ArrayList;
import java.util.List;
/**
 * This is a first step in a more proper cache based on specific 
 * purpose and integrated into the database.
 * @author David Byrne
 *
 */
public class StringCache
{
	private List<byte[]> values;
//	private final Map<byte[], Integer> indexes;
//	private int lastIndex;
	
		
	public StringCache()
    {
//		lastIndex = 0;
//		indexes = new HashMap<byte[], Integer>();
		values = new ArrayList<byte[]>(500);
    }

	public int addValue(String value)
	{
		return addValue(value.getBytes(StringUtils.getDefaultCharset()));
	}
	
	public int addValue(byte[] value)
	{
		int index = 0;
		synchronized(values)
		{
			if (values.contains(value))
			{
				index = values.indexOf(value);
			}
			else
			{
//				index = lastIndex++;
				values.add(value);
				index = values.size() - 1;
//				indexes.put(value, index);
			}
		}
		return index;
	}
		
	public byte[] getBytes(int index)
	{
		return values.get(index);
	}
	
	public String getString(int index)
	{
		return new String(getBytes(index), StringUtils.getDefaultCharset());
	}
}
