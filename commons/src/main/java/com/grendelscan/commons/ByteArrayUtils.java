/**
 * 
 */
package com.grendelscan.commons;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author david
 * 
 */
public class ByteArrayUtils
{

    public static byte[][] split(final byte[] array, final byte ch)
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

    public static byte[][] splitOnFirst(final byte[] array, final byte ch)
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

    private ByteArrayUtils()
    {

    }
}
