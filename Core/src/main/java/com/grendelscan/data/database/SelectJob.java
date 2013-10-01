package com.grendelscan.data.database;

import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteConstants;
import com.almworks.sqlite4java.SQLiteException;
import com.grendelscan.logging.Log;

public class SelectJob extends DbJob<Object[][]>
{

	public SelectJob(String query, Object[] values)
	{
		super(query, values);
	}
	
	
	@Override
	protected Object[][] job(SQLiteConnection connection) throws Throwable
	{
		handleBindings(connection);
		ArrayList<Object[]> results = null;
		try
		{
			int columnCount = st.columnCount();
			results = new ArrayList<Object[]>();
			while (st.step())
			{
				Object[] row = new Object[columnCount];
				results.add(row);
				for (int i = 0; i < row.length; i++)
				{
					switch(st.columnType(i))
					{
						case SQLiteConstants.SQLITE_BLOB:
							ObjectInputStream objstream = new ObjectInputStream(st.columnStream(i));
							row[i] = objstream.readObject();
							break;
							
						case SQLiteConstants.SQLITE_NULL:
							row[i] = null;
							break;
							
						case SQLiteConstants.SQLITE_TEXT:
							row[i] = st.columnValue(i);
							break;
							
						case SQLiteConstants.SQLITE_INTEGER:
						case SQLiteConstants.SQLITE_FLOAT:
						default:
							row[i] = st.columnValue(i);
					}
				}
			}
			st.dispose();
		}
		catch (SQLiteException e)
		{
			Log.error("SQL problem: " + e.toString(), e);
			throw e;
		}
		
		return results.toArray(new Object[0][0]);
	}
	

}
