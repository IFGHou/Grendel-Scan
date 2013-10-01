package com.grendelscan.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a first step in a more proper cache based on specific purpose and integrated into the database.
 * 
 * @author David Byrne
 * 
 */
public class StringCache
{
    private final List<byte[]> values;

    // private final Map<byte[], Integer> indexes;
    // private int lastIndex;

    public StringCache()
    {
        // lastIndex = 0;
        // indexes = new HashMap<byte[], Integer>();
        values = new ArrayList<byte[]>(500);
    }

    public int addValue(final byte[] value)
    {
        int index = 0;
        synchronized (values)
        {
            if (values.contains(value))
            {
                index = values.indexOf(value);
            }
            else
            {
                // index = lastIndex++;
                values.add(value);
                index = values.size() - 1;
                // indexes.put(value, index);
            }
        }
        return index;
    }

    public int addValue(final String value)
    {
        return addValue(value.getBytes(StringUtils.getDefaultCharset()));
    }

    public byte[] getBytes(final int index)
    {
        return values.get(index);
    }

    public String getString(final int index)
    {
        return new String(getBytes(index), StringUtils.getDefaultCharset());
    }
}
