/**
 * 
 */
package com.grendelscan.data.database.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.scan.Scan;

/**
 * @author david
 * 
 */
public class DatabaseBackedMap<K, V> extends DatabaseBackedCollection implements DatabaseUser, Map<K, V>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBackedMap.class);
	private Database					database;
	private Map<K, V> cache;
	
	public DatabaseBackedMap(String uniqueName)
	{
		super(uniqueName + "map");
		cache = new HashMap<K, V>();
		database = Scan.getInstance().getTestData().getDatabase();
		initializeDatabase(uniqueName);
	}


	@SuppressWarnings("unchecked")
	private void initializeDatabase(String uniqueName)
	{
		LOGGER.debug("Initializing database for database-backed map: " + collectionName);
		try
		{
			if (!database.tableExists(collectionName))
			{
				String tableQuery =
						"CREATE TABLE " + collectionName + " (name blob, value blob, PRIMARY KEY (name))";
				String indexQuery = "CREATE INDEX IDX_DEFAULT_" + collectionName + " ON " + collectionName + " (name)";
				database.execute(tableQuery);
				database.execute(indexQuery);
			}
			else
			{
				Object[][] content = database.selectAll("SELECT name, value FROM " + collectionName, new Object[0]);
				for (Object[] row: content)
				{
					cache.put((K)row[0], (V)row[1]); 
				}
			}
		}
		catch (DataNotFoundException e)
		{
			LOGGER.info("No data found in " + collectionName + " collection. This may not be a problem.", e);
		}
		catch (Throwable e)
		{
			LOGGER.error("Problem with creating database for " + uniqueName + " database backed map: " + e.toString(), e);
			System.exit(1);
		}
	}

	@Override
	public Set<K> keySet()
	{
		synchronized(this)
		{
			return cache.keySet();
		}
	}

	private V privatePut(K name, V value)
	{
		V oldValue = cache.get(name);
		cache.put(name, value);
		removeFromDb(name);
		CommandJob job = new CommandJob("INSERT INTO " + collectionName +
				" (name, value) VALUES (?, ?)", new Object[] { name, value });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			LOGGER.error("Problem saving string: " + e.toString(), e);
		}
		return oldValue;
	}
	
	@Override
	public V put(K name, V value)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Null is not a legal name");
		}
		if (value == null)
		{
			throw new IllegalArgumentException("Null is not a legal value");
		}
		synchronized(this)
		{
			return privatePut(name, value);
		}
	}


	private void removeFromDb(Object name)
	{
		CommandJob job = new CommandJob("DELETE FROM " + collectionName +
				" WHERE name = ?", new Object[] { name });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			LOGGER.error("Problem deleting from " + collectionName + ": " + e.toString(), e);
		}
	}

	@Override
	public void shutdown(boolean gracefully) throws InterruptedException
	{
		LOGGER.debug("Shutting down transaction record");
		database.stop(gracefully).join();
	}

	@Override
	public int size()
	{
		synchronized(this)
		{
			return cache.size();
		}
	}

	@Override
	public Collection<V> values()
	{
		synchronized(this)
		{
			return cache.values();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		synchronized(this)
		{
			return cache.size() > 0;
		}
	}

	@Override
	public boolean containsKey(Object key)
	{
		synchronized(this)
		{
			return cache.containsKey(key);
		}
	}

	@Override
	public V get(Object key)
	{
		synchronized(this)
		{
			return cache.get(key);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value)
	{
		synchronized(this)
		{
			return cache.containsValue(value);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		synchronized (this)
		{
			for (K key : m.keySet())
			{
				privatePut(key, m.get(key));
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear()
	{
		synchronized (this)
		{
			cache.clear();
			CommandJob job = new CommandJob("DELETE FROM " + collectionName, new Object[0]);
			try
			{
				database.execute(job);
			}
			catch (Throwable e)
			{
				LOGGER.error("Problem deleting from " + collectionName + ": " + e.toString(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		synchronized (this)
		{
			return cache.entrySet();
		}
	}



	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key)
	{
		synchronized (this)
		{
			V old = cache.get(key);
			cache.remove(key);
			removeFromDb(key);
			return old;
		}
	}

}
