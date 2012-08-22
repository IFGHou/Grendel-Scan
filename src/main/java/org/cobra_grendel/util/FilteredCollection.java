/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Oct 8, 2005
 */
package org.cobra_grendel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredCollection implements Collection
{
	private final ObjectFilter filter;
	private final Collection sourceCollection;
	
	public FilteredCollection(Collection sourceCollection, ObjectFilter filter)
	{
		this.filter = filter;
		this.sourceCollection = sourceCollection;
	}
	
	@Override public boolean add(Object o)
	{
		return sourceCollection.add(filter.encode(o));
	}
	
	@Override public boolean addAll(Collection c)
	{
		boolean result = false;
		Iterator i = c.iterator();
		while (i.hasNext())
		{
			if (add(i.next()))
			{
				result = true;
			}
		}
		return result;
	}
	
	@Override public void clear()
	{
		Object[] values = this.toArray();
		for (int i = 0; i < values.length; i++)
		{
			sourceCollection.remove(filter.encode(values[i]));
		}
	}
	
	@Override public boolean contains(Object o)
	{
		return sourceCollection.contains(filter.encode(o));
	}
	
	@Override public boolean containsAll(Collection c)
	{
		Iterator i = c.iterator();
		while (i.hasNext())
		{
			if (!contains(i.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override public boolean isEmpty()
	{
		Iterator i = sourceCollection.iterator();
		while (i.hasNext())
		{
			if (filter.decode(i.next()) != null)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override public Iterator iterator()
	{
		final Iterator sourceIterator = sourceCollection.iterator();
		return new Iterator()
		{
			private Boolean hasNext;
			private Object next;
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#hasNext()
			 */
			@Override public boolean hasNext()
			{
				if (hasNext == null)
				{
					scanNext();
				}
				return hasNext.booleanValue();
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#next()
			 */
			@Override public Object next()
			{
				if (hasNext == null)
				{
					scanNext();
				}
				if (Boolean.FALSE.equals(hasNext))
				{
					throw new NoSuchElementException();
				}
				Object next = this.next;
				hasNext = null;
				return next;
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#remove()
			 */
			@Override public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
			private void scanNext()
			{
				while (sourceIterator.hasNext())
				{
					Object item = filter.decode(sourceIterator.next());
					if (item != null)
					{
						hasNext = Boolean.TRUE;
						next = item;
					}
				}
				hasNext = Boolean.FALSE;
			}
		};
	}
	
	@Override public boolean remove(Object o)
	{
		return sourceCollection.remove(filter.encode(o));
	}
	
	@Override public boolean removeAll(Collection c)
	{
		boolean result = false;
		Iterator i = c.iterator();
		while (i.hasNext())
		{
			if (remove(i.next()))
			{
				result = true;
			}
		}
		return result;
	}
	
	@Override public boolean retainAll(Collection c)
	{
		boolean result = false;
		Object[] values = this.toArray();
		for (int i = 0; i < values.length; i++)
		{
			if (!c.contains(values[i]))
			{
				if (remove(values[i]))
				{
					result = true;
				}
			}
		}
		return result;
	}
	
	@Override public int size()
	{
		int count = 0;
		Iterator i = sourceCollection.iterator();
		while (i.hasNext())
		{
			if (filter.decode(i.next()) != null)
			{
				count++;
			}
		}
		return count;
	}
	
	@Override public Object[] toArray()
	{
		return this.toArray(new Object[0]);
	}
	
	@Override public Object[] toArray(Object[] a)
	{
		Collection bucket = new ArrayList();
		Iterator i = sourceCollection.iterator();
		while (i.hasNext())
		{
			Object item = filter.decode(i.next());
			if (item != null)
			{
				bucket.add(item);
			}
		}
		return bucket.toArray(a);
	}
}
