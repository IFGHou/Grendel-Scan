/**
 * 
 */
package com.grendelscan.scan.data;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.scan.Scan;

/**
 * @author david
 * 
 */
public class PersistedTestData implements DatabaseUser {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersistedTestData.class);
	private static final String dbFile = "test-data.db";
	private static final String INT_TABLE = "int_data";
	private static final String OBJECT_TABLE = "object_data";
	private static final String STRING_TABLE = "string_data";
	private final Database database;

	public PersistedTestData() {
		database = new Database(Scan.getInstance().getOutputDirectory()
				+ dbFile);
		initializeDatabase();
	}

	public boolean containsItem(final String name) {
		try {
			getObject(name);
		} catch (DataNotFoundException e) {
			return false;
		}
		return true;
	}

	public void deleteInt(final String name) {
		deleteItem(name, INT_TABLE);
	}

	private void deleteItem(final String name, final String table) {
		CommandJob job = new CommandJob("DELETE FROM " + table
				+ " WHERE name = ?", new Object[] { name });
		try {
			database.execute(job);
		} catch (Throwable e) {
			LOGGER.error(
					"Problem deleting from " + table + ": " + e.toString(), e);
		}
	}

	public void deleteObject(final String name) {
		deleteItem(name, OBJECT_TABLE);
	}

	public void deleteString(final String name) {
		deleteItem(name, STRING_TABLE);
	}

	public boolean getBoolean(final String name) throws DataNotFoundException {
		return getInt(name) == 1;
	}

	public final Database getDatabase() {
		return database;
	}

	public int getInt(final String name) throws DataNotFoundException {
		try {
			return database.selectSimpleInt("SELECT value FROM " + INT_TABLE
					+ " WHERE name = ?", new Object[] { name });
		} catch (DataNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOGGER.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public Object getObject(final String name) throws DataNotFoundException {
		try {
			return database.selectSimpleObject("SELECT value FROM "
					+ OBJECT_TABLE + " WHERE name = ?", new Object[] { name });
		} catch (DataNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOGGER.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public String getString(final String name) throws DataNotFoundException {
		try {
			return (String) database.selectSimpleObject("SELECT value FROM "
					+ STRING_TABLE + " WHERE name = ?", new Object[] { name });
		} catch (DataNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOGGER.error("Weird problem getting test data: " + e.toString(), e);
			throw new DataNotFoundException(e);
		}
	}

	public void initializeDatabase() {
		LOGGER.debug("Initializing database for test module persisted data storage");
		try {
			initializeTable(STRING_TABLE, "varchar(200)");
			initializeTable(INT_TABLE, "int");
			initializeTable(OBJECT_TABLE, "blob");
		} catch (Throwable e) {
			LOGGER.error(
					"Problem with creating database for persisted test data: "
							+ e.toString(), e);
			System.exit(1);
		}
	}

	private void initializeTable(final String tableName, final String dataType)
			throws SQLException, Throwable {
		if (!database.tableExists(tableName)) {
			String tableQuery = "CREATE TABLE " + tableName
					+ " (name varchar(100), value " + dataType
					+ ", PRIMARY KEY (name))";
			String indexQuery = "CREATE INDEX IDX_DEFAULT_" + tableName
					+ " ON " + tableName + " (name)";
			database.execute(tableQuery);
			database.execute(indexQuery);
		}
	}

	public void setBoolean(final String name, final boolean value) {
		setInt(name, value ? 1 : 0);
	}

	public void setInt(final String name, final Integer i) {
		deleteInt(name);
		CommandJob job = new CommandJob("INSERT INTO " + INT_TABLE
				+ " (name, value) VALUES (?, ?)", new Object[] { name, i });
		try {
			database.execute(job);
		} catch (Throwable e) {
			LOGGER.error("Problem saving integer: " + e.toString(), e);
		}
	}

	public void setObject(final String name, final Object object) {
		deleteObject(name);
		CommandJob job = new CommandJob("INSERT INTO " + OBJECT_TABLE
				+ " (name, value) VALUES (?, ?)", new Object[] { name, object });
		try {
			database.execute(job);
		} catch (Throwable e) {
			LOGGER.error("Problem saving object: " + e.toString(), e);
		}
	}

	public void setString(final String name, final String string) {
		deleteString(name);
		CommandJob job = new CommandJob("INSERT INTO " + STRING_TABLE
				+ " (name, value) VALUES (?, ?)", new Object[] { name, string });
		try {
			database.execute(job);
		} catch (Throwable e) {
			LOGGER.error("Problem saving string: " + e.toString(), e);
		}
	}

	@Override
	public void shutdown(final boolean gracefully) throws InterruptedException {
		LOGGER.debug("Shutting down PersistedTestData");
		database.stop(gracefully).join();
	}
}
