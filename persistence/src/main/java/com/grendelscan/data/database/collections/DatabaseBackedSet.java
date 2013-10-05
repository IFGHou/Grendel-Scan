/**
 * 
 */
package com.grendelscan.data.database.collections;

import java.util.Collection;
import java.util.Set;

/**
 * @author david
 * 
 */
public class DatabaseBackedSet<T> extends DatabaseBackedList<T> implements Set<T>
{

    /**
     * @param uniqueName
     */
    public DatabaseBackedSet(final String uniqueName)
    {
        super(uniqueName);
    }

    /**
     * @param uniqueName
     */
    public DatabaseBackedSet(final String uniqueName, final int initialSize)
    {
        super(uniqueName, initialSize);
    }

    @Override
    public void add(final int index, final T element)
    {
        synchronized (this)
        {
            if (!cache.contains(element))
            {
                cache.add(index, element);
                commitCache();
            }
        }
    }

    @Override
    public boolean add(final T element)
    {
        synchronized (this)
        {
            if (!cache.contains(element))
            {
                cache.add(element);
                commitCache();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends T> c)
    {
        synchronized (this)
        {
            boolean changed = false;
            for (T t : c)
            {
                if (!cache.contains(t))
                {
                    changed = true;
                    cache.add(t);
                }
            }
            if (changed)
            {
                commitCache();
            }
            return changed;
        }
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c)
    {
        int i = index;
        boolean changed = false;
        for (T t : c)
        {
            if (!cache.contains(t))
            {
                changed = true;
                cache.add(i++, t);
            }
        }
        if (changed)
        {
            commitCache();
        }
        return changed;
    }

}
