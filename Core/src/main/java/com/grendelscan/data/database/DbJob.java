package com.grendelscan.data.database;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

public abstract class DbJob<T> extends SQLiteJob<T>
{
	private final String query;
	private final Object[] values;
	protected SQLiteStatement st;
	


	protected DbJob(String query, Object[] values)
	{
		this.query = query;
		this.values = values;
	}

	protected void handleBindings(SQLiteConnection connection) throws SQLiteException, IOException
	{
		st = connection.prepare(query);
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
	}

}
