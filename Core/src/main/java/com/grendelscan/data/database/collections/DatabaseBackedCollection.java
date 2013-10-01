/**
 * 
 */
package com.grendelscan.data.database.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david
 *
 * @param <T>
 */
public abstract class DatabaseBackedCollection
{
	protected final String collectionName;
	protected static final List<String>	existingNames	= new ArrayList<String>(1);

	public final static void clearExistingNames()
	{
		existingNames.clear();
	}
	
	public DatabaseBackedCollection(String uniqueName)
	{
		synchronized(existingNames)
		{
			this.collectionName = uniqueName;
			if (existingNames.contains(uniqueName))
			{
				throw new IllegalArgumentException(uniqueName + " already exists as a database-backed collection");
			}
		}
	}

}