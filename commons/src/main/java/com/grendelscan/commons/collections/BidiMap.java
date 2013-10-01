/**
 * 
 */
package com.grendelscan.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author david
 * 
 */
public class BidiMap<K, V> implements Map<K, V>
{
    private class BidiEntry implements java.util.Map.Entry<K, V>
    {

        private final K key;
        private V value;

        BidiEntry(final K key, final V value)
        {
            this.key = key;
            this.value = value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Map.Entry#getKey()
         */
        @Override
        public K getKey()
        {
            return key;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Map.Entry#getValue()
         */
        @Override
        public V getValue()
        {
            return value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        @Override
        public V setValue(final V value)
        {
            return this.value = value;
        }

    }

    private final HashMap<K, V> forward;
    private final HashMap<V, K> reverse;

    private final ArrayList<K> order;

    public BidiMap()
    {
        this(10);
    }

    public BidiMap(final int size)
    {
        forward = new HashMap<K, V>(size);
        reverse = new HashMap<V, K>(size);
        order = new ArrayList<K>();
    }

    public void changeKey(final K oldKey, final K newKey)
    {
        int pos = order.indexOf(oldKey);
        if (pos < 0)
        {
            return;
        }

        order.remove(oldKey);
        order.add(pos, newKey);

        V value = forward.remove(oldKey);
        forward.put(newKey, value);
        reverse.put(value, newKey);
    }

    @Override
    public void clear()
    {
        forward.clear();
        reverse.clear();
        order.clear();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return forward.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return forward.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        LinkedHashSet<java.util.Map.Entry<K, V>> s = new LinkedHashSet<java.util.Map.Entry<K, V>>(order.size());
        for (K key : order)
        {
            s.add(new BidiEntry(key, forward.get(key)));
        }
        return forward.entrySet();
    }

    @Override
    public boolean equals(final Object o)
    {
        return forward.equals(o);
    }

    @Override
    public V get(final Object key)
    {
        return forward.get(key);
    }

    public K getKey(final V value)
    {
        return reverse.get(value);
    }

    public List<K> getSortedKeys()
    {
        return order;
    }

    public List<V> getSortedValues()
    {
        ArrayList<V> values = new ArrayList<V>(order.size());
        for (K key : order)
        {
            values.add(forward.get(key));
        }
        return values;
    }

    public V getValue(final K key)
    {
        return get(key);
    }

    @Override
    public int hashCode()
    {
        return forward.hashCode();
    }

    @Override
    public boolean isEmpty()
    {
        return forward.isEmpty();
    }

    @Override
    public LinkedHashSet<K> keySet()
    {
        LinkedHashSet<K> s = new LinkedHashSet<K>(order.size());
        s.addAll(order);
        return s;
    }

    @Override
    public V put(final K key, final V value)
    {
        reverse.put(value, key);
        if (!order.contains(key))
        {
            order.add(key);
        }
        return forward.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m)
    {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            forward.put(entry.getKey(), entry.getValue());
            reverse.put(entry.getValue(), entry.getKey());
            order.add(entry.getKey());
        }
    }

    @Override
    public V remove(final Object key)
    {
        V value = forward.get(key);
        reverse.remove(value);
        order.remove(key);
        return forward.remove(key);
    }

    public K removeValue(final V value)
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
    public int size()
    {
        return forward.size();
    }

    @Override
    public Collection<V> values()
    {
        return forward.values();
    }

}
