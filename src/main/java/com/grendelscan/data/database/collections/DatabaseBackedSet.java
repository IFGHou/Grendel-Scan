/**
 * 
 */
package com.grendelscan.data.database.collections;

import java.util.*;


/**
 * @author david
 *
 */
public class DatabaseBackedSet<T> extends DatabaseBackedList<T> implements Set<T>
{

	
	/**
	 * @param uniqueName
	 */
	public DatabaseBackedSet(String uniqueName)
	{
		super(uniqueName);
	}

	/**
	 * @param uniqueName
	 */
	public DatabaseBackedSet(String uniqueName, int initialSize)
	{
		super(uniqueName, initialSize);
	}

	@Override
	public void add(int index, T element)
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
	public boolean add(T element)
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
	public boolean addAll(Collection<? extends T> c)
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
			if (changed) commitCache();
			return changed;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		int i = index;
		boolean changed = false;
		for (T t: c)
		{
			if (!cache.contains(t))
			{
				changed = true;
				cache.add(i++, t);
			}
		}
		if (changed) commitCache();
		return changed;
	}

}
