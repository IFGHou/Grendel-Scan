/**
 * 
 */
package com.grendelscan.data.database;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * @author david
 *
 */
public class BulkInsertJob extends SQLiteJob<Object>
{

	private final List<Insert> inserts;
	
	private class Insert
	{
		public Insert(String query, Object[] values)
		{
			this.query = query;
			this.values = values;
		}
		String query;
		Object[] values;
	}
	
	public BulkInsertJob()
	{
		inserts = new ArrayList<Insert>();
	}
	
	
	public void addInsert(String query, Object[] values)
	{
		inserts.add(new Insert(query, values));
	}

	/* (non-Javadoc)
	 * @see com.almworks.sqlite4java.SQLiteJob#job(com.almworks.sqlite4java.SQLiteConnection)
	 */
	@Override
	protected Object job(SQLiteConnection connection) throws Throwable
	{
		connection.exec("BEGIN");
		for (Insert insert: inserts)
		{
			handleInsert(connection, insert.query, insert.values);
		}
		connection.exec("COMMIT");
		return null;
	}

	

	protected void handleInsert(SQLiteConnection connection, String query, Object[] values) throws SQLiteException, IOException
	{
		SQLiteStatement st = connection.prepare(query);
		for (int index = 1; index <= values.length; index++)
		{
			Object o = values[index-1];
			if (o == null)
			{
				st.bindNull(index);
			}
			else if (o instanceof Double)
			{
				st.bind(index, (Double) o);
			}
			else if (o instanceof Integer)
			{
				st.bind(index, (Integer) o);
			}
			else if (o instanceof Long)
			{
				st.bind(index, (Long) o);
			}
			else if (o instanceof byte[])
			{
				st.bind(index, (byte[]) o);
			}
			else if (o instanceof String)
			{
				st.bind(index, (String) o);
			}
			else 
			{
				ObjectOutputStream objstream = new ObjectOutputStream(st.bindStream(index));
				objstream.writeObject(o);
				objstream.close();
			}
		}
		st.step();
		st.dispose();
	}
}
