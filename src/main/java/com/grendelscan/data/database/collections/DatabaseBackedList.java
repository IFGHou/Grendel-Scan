/**
 * 
 */
package com.grendelscan.data.database.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.scan.Scan;


/**
 * @author david
 * @param <T>
 * 
 */
public class DatabaseBackedList<T> extends DatabaseBackedCollection implements List<T>
{

	protected List<T>						cache;

	public DatabaseBackedList(String uniqueName)
	{
		this(uniqueName + "list", 10);
	}

	public DatabaseBackedList(String uniqueName, int initialSize)
	{
		super(uniqueName + "list");
		populateCache(initialSize);
	}

	@Override
	public void add(int index, T element)
	{
		synchronized (this)
		{
			cache.add(index, element);
			commitCache();
		}
	}

	@Override
	public boolean add(T e)
	{
		synchronized (this)
		{
			boolean b = cache.add(e);
			commitCache();
			return b;
		}
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		synchronized (this)
		{
			boolean b = cache.addAll(c);
			commitCache();
			return b;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		synchronized (this)
		{
			boolean b = cache.addAll(index, c);
			commitCache();
			return b;
		}
	}

	@Override
	public void clear()
	{
		synchronized (this)
		{
			cache.clear();
			commitCache();
		}
	}

	@Override
	public boolean contains(Object o)
	{
		synchronized (this)
		{
			return cache.contains(o);
		}
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		synchronized (this)
		{
			return cache.containsAll(c);
		}
	}

	@Override
	public T get(int index)
	{
		synchronized (this)
		{
			return cache.get(index);
		}
	}

	@Override
	public int indexOf(Object o)
	{
		return cache.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		synchronized (this)
		{
			return cache.isEmpty();
		}
	}

	@Override
	public Iterator<T> iterator()
	{
		synchronized (this)
		{
			return cache.iterator();
		}
	}

	@Override
	public int lastIndexOf(Object o)
	{
		synchronized (this)
		{
			return cache.lastIndexOf(o);
		}
	}

	@Override
	public ListIterator<T> listIterator()
	{
		synchronized (this)
		{
			return cache.listIterator();
		}
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		synchronized (this)
		{
			return cache.listIterator(index);
		}
	}

	@Override
	public T remove(int index)
	{
		synchronized (this)
		{
			T t = cache.remove(index);
			commitCache();
			return t;
		}
	}

	@Override
	public boolean remove(Object o)
	{
		synchronized (this)
		{
			boolean b = cache.remove(o);
			commitCache();
			return b;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		synchronized (this)
		{
			boolean b = cache.removeAll(c);
			commitCache();
			return b;
		}
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		synchronized (this)
		{
			boolean b = cache.retainAll(c);
			commitCache();
			return b;
		}
	}

	@Override
	public T set(int index, T element)
	{
		synchronized (this)
		{
			T t = cache.set(index, element);
			commitCache();
			return t;
		}
	}

	@Override
	public int size()
	{
		synchronized (this)
		{
			int s = cache.size();
			return s;
		}
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		synchronized (this)
		{
			return cache.subList(fromIndex, toIndex);
		}
	}

	@Override
	public Object[] toArray()
	{
		synchronized (this)
		{
			return cache.toArray();
		}
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		synchronized (this)
		{
			return cache.toArray(a);
		}
	}

	protected void commitCache()
	{
		Scan.getInstance().getTestData().setObject(collectionName, cache);
	}

	@SuppressWarnings("unchecked")
	private void populateCache(int initialSize)
	{
		try
		{
			cache = (List<T>) Scan.getInstance().getTestData().getObject(collectionName);
		}
		catch (DataNotFoundException e)
		{
			cache = new ArrayList<T>(initialSize);
		}
	}

}
