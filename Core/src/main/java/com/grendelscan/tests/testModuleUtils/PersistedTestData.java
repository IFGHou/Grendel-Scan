/**
 * 
 */
package com.grendelscan.tests.testModuleUtils;

import java.sql.SQLException;

import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.logging.Log;

/**
 * @author david
 * 
 */
public class PersistedTestData implements DatabaseUser
{
	private static final String	dbFile			= "test-data.db";
	private static final String	INT_TABLE		= "int_data";
	private static final String	OBJECT_TABLE	= "object_data";
	private static final String	STRING_TABLE	= "string_data";
	private Database			database;

	public PersistedTestData()
	{
		database = new Database(dbFile);
		initializeDatabase();
	}

	public boolean containsItem(String name)
	{
		try
		{
			getObject(name);
		}
		catch (DataNotFoundException e)
		{
			return false;
		}
		return true;
	}
	
	public void deleteInt(String name)
	{
		deleteItem(name, INT_TABLE);
	}

	public void deleteObject(String name)
	{
		deleteItem(name, OBJECT_TABLE);
	}

	public void deleteString(String name)
	{
		deleteItem(name, STRING_TABLE);
	}

	public final Database getDatabase()
	{
		return database;
	}

	public boolean getBoolean(String name) throws DataNotFoundException
	{
		return getInt(name) == 1;
	}

	public void setBoolean(String name, boolean value)
	{
		setInt(name, value ? 1 : 0);
	}

	
	public int getInt(String name) throws DataNotFoundException
	{
		try
		{
			return database.selectSimpleInt("SELECT value FROM " + INT_TABLE +
					" WHERE name = ?", new Object[] { name });
		}
		catch (DataNotFoundException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			Log.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public Object getObject(String name) throws DataNotFoundException
	{
		try
		{
			return database.selectSimpleObject("SELECT value FROM " + OBJECT_TABLE +
					" WHERE name = ?", new Object[] { name });
		}
		catch (DataNotFoundException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			Log.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public String getString(String name) throws DataNotFoundException
	{
		try
		{
			return (String) database.selectSimpleObject("SELECT value FROM " + STRING_TABLE +
					" WHERE name = ?", new Object[] { name });
		}
		catch (DataNotFoundException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			Log.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public void initializeDatabase()
	{
		Log.debug("Initializing database for test module persisted data storage");
		try
		{
			initializeTable(STRING_TABLE, "varchar(200)");
			initializeTable(INT_TABLE, "int");
			initializeTable(OBJECT_TABLE, "blob");
		}
		catch (Throwable e)
		{
			Log.fatal("Problem with creating database for persisted test data: " + e.toString(), e);
			System.exit(1);
		}
	}

	public void setInt(String name, Integer i)
	{
		deleteInt(name);
		CommandJob job = new CommandJob("INSERT INTO " + INT_TABLE +
				" (name, value) VALUES (?, ?)", new Object[] { name, i });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			Log.error("Problem saving integer: " + e.toString(), e);
		}
	}

	public void setObject(String name, Object object)
	{
		deleteObject(name);
		CommandJob job = new CommandJob("INSERT INTO " + OBJECT_TABLE +
				" (name, value) VALUES (?, ?)", new Object[] { name, object });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			Log.error("Problem saving object: " + e.toString(), e);
		}
	}

	public void setString(String name, String string)
	{
		deleteString(name);
		CommandJob job = new CommandJob("INSERT INTO " + STRING_TABLE +
				" (name, value) VALUES (?, ?)", new Object[] { name, string });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			Log.error("Problem saving string: " + e.toString(), e);
		}
	}

	@Override
	public void shutdown(boolean gracefully) throws InterruptedException
	{
		Log.debug("Shutting down PersistedTestData");
		database.stop(gracefully).join();
	}

	private void deleteItem(String name, String table)
	{
		CommandJob job = new CommandJob("DELETE FROM " + table +
				" WHERE name = ?", new Object[] { name });
		try
		{
			database.execute(job);
		}
		catch (Throwable e)
		{
			Log.error("Problem deleting from " + table + ": " + e.toString(), e);
		}
	}

	private void initializeTable(String tableName, String dataType) throws SQLException, Throwable
	{
		if (!database.tableExists(tableName))
		{
			String tableQuery =
					"CREATE TABLE " + tableName + " (name varchar(100), value " + dataType + ", PRIMARY KEY (name))";
			String indexQuery = "CREATE INDEX IDX_DEFAULT_" + tableName + " ON " + tableName + " (name)";
			database.execute(tableQuery);
			database.execute(indexQuery);
		}
	}
}