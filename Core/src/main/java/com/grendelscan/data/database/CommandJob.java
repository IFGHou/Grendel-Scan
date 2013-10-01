package com.grendelscan.data.database;

import com.almworks.sqlite4java.SQLiteConnection;

public class CommandJob extends DbJob<Object>
{

	public CommandJob(String query, Object[] values)
	{
		super(query, values);
	}

	
	@Override
	protected Object job(SQLiteConnection connection) throws Throwable
	{
		handleBindings(connection);
		st.step();
		st.dispose();
		return null;
	}

}
