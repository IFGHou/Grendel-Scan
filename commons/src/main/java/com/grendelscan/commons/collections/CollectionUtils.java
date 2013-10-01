package com.grendelscan.commons.collections;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils
{
    public static <T> Collection<T> arrayToCollection(final T[] array)
    {
        int length = array == null ? 0 : array.length;
        ArrayList<T> al = new ArrayList<T>(length);
        for (int i = 0; i < length; i++)
        {
            al.add(array[i]);
        }
        return al;
    }

    public static boolean containsStringIgnoreCase(final Iterable<String> collection, final String value)
    {
        boolean contained = false;
        for (String s : collection)
        {
            if (s.equalsIgnoreCase(value))
            {
                contained = true;
                break;
            }
        }
        return contained;
    }

    public static String[] toStringArray(final Iterable<String> collection)
    {
        // Unfortunately, Iterable has no size method
        int size = 0;
        for (@SuppressWarnings("unused")
        String s : collection)
        {
            size++;
        }

        String strings[] = new String[size];
        int index = 0;
        for (String s : collection)
        {
            strings[index++] = s;
        }
        return strings;
    }

}
