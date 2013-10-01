/**
 * 
 */
package com.grendelscan.utils.collections;

import java.util.*;

/**
 * @author david
 *
 */
public class BidiMap<K, V> implements Map<K, V>
{
	private final HashMap <K, V> forward;
	private final HashMap <V, K> reverse;
	private final ArrayList<K> order;
	
	public BidiMap()
	{
		this(10);
	}
	
	public BidiMap(int size)
	{
		forward = new HashMap <K, V>(size);
		reverse = new HashMap <V, K>(size);
		order = new ArrayList<K>();
	}

	@Override
	public int size()
	{
		return forward.size();
	}

	@Override
	public boolean isEmpty()
	{
		return forward.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return forward.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return forward.containsValue(value);
	}

	@Override
	public V get(Object key)
	{
		return forward.get(key);
	}

	@Override
	public V put(K key, V value)
	{
		reverse.put(value,  key);
		if (!order.contains(key))
		{
			order.add(key);
		}
		return forward.put(key, value);
	}

	@Override
	public V remove(Object key)
	{
		V value = forward.get(key);
		reverse.remove(value);
		order.remove(key);
		return forward.remove(key);
	}

	public K removeValue(V value)
	{
		K key = reverse.remove(value);
		if (key != null)
		{
			forward.remove(key);
			order.remove(key);
		}
		return key;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		for(Entry<? extends K, ? extends V> entry: m.entrySet())
		{
			forward.put(entry.getKey(), entry.getValue());
			reverse.put(entry.getValue(), entry.getKey());
			order.add(entry.getKey());
		}
	}

	@Override
	public void clear()
	{
		forward.clear();
		reverse.clear();
		order.clear();
	}

	@Override
	public LinkedHashSet<K> keySet()
	{
		LinkedHashSet<K> s = new LinkedHashSet<K>(order.size());
		s.addAll(order);
		return s;
	}

	@Override
	public Collection<V> values()
	{
		return forward.values();
	}
	
	private class BidiEntry implements java.util.Map.Entry<K, V>
	{

		private K key;
		private V value;
		
		BidiEntry(K key, V value)
		{
			this.key = key;
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getKey()
		 */
		@Override
		public K getKey()
		{
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getValue()
		 */
		@Override
		public V getValue()
		{
			return value;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		@Override
		public V setValue(V value)
		{
			return this.value = value;
		}
		
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		LinkedHashSet<java.util.Map.Entry<K, V>> s = new LinkedHashSet<java.util.Map.Entry<K, V>>(order.size());
		for(K key: order)
		{
			s.add(new BidiEntry(key, forward.get(key)));
		}
		return forward.entrySet();
	}

	@Override
	public boolean equals(Object o)
	{
		return forward.equals(o);
	}

	@Override
	public int hashCode()
	{
		return forward.hashCode();
	}
	
	public V getValue(K key)
	{
		return get(key);
	}
	
	public K getKey(V value)
	{
		return reverse.get(value);
	}
	
	public void changeKey(K oldKey, K newKey)
	{
		int pos = order.indexOf(oldKey);
		if (pos < 0)
			return;
		
		order.remove(oldKey);
		order.add(pos, newKey);
		
		V value = forward.remove(oldKey);
		forward.put(newKey, value);
		reverse.put(value, newKey);
	}
	
	public List<V> getSortedValues()
	{
		ArrayList<V> values = new ArrayList<V>(order.size());
		for (K key: order)
		{
			values.add(forward.get(key));
		}
		return values;
	}
	
	public List<K> getSortedKeys()
	{
		return order;
	}
	
}
