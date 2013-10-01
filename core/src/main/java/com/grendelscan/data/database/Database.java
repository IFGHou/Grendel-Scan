package com.grendelscan.data.database;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.scan.Scan;

public class Database
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
	private final SQLiteQueue queue;
	private final String file;

	public Database(String file) 
    {
		this.file = file;
		LOGGER.debug("Initializing " + file + " database");
        try
		{
			Class.forName("com.almworks.sqlite4java.SQLite");
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.error("Failed to load DB driver: " + e.toString());
			System.exit(1);
		}
        queue = new SQLiteQueue(new File(Scan.getInstance().getOutputDirectory() + file));
        queue.start();
        try
		{
			execute("PRAGMA synchronous=OFF");
	        execute("PRAGMA count_changes=OFF");
		}
		catch (Throwable e)
		{
			LOGGER.error("Very weird problem configuring database: " + e.toString(), e);
		}
	}

	
	public void execute(final String query) throws Throwable
	{
		SQLiteJob<Object> job = new SQLiteJob<Object>() 
		{
		   	@Override
			protected Object job(SQLiteConnection connection) throws SQLiteException 
		   	{
		   		connection.exec(query);
		   		return null;
		   	}
		};
		queue.execute(job).get();
		if (job.getError() != null)
		{
			throw job.getError();
		}
	}
	

	public boolean tableExists(String tableName) throws SQLException, Throwable
	{
		String query = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name = ?";
		try
		{
			return selectSimpleInt(query, new Object[] {tableName}) > 0;
		}
		catch (DataNotFoundException e)
		{
			LOGGER.error("This should never happen: " + e.toString());
			System.exit(1);
		}
		return false; //stupid compiler
	}

	public int selectSimpleInt(String query, Object[] values) throws DataNotFoundException, Throwable
	{
		return (Integer) selectSimpleObject(query, values);
	}
	

	public Object[][] selectAll(String query, Object[] values) throws DataNotFoundException, Throwable
	{
		SelectJob job = new SelectJob(query, values);
		queue.execute(job);
		
		Object[][] results;
		try
		{
			results = job.get();
		}
		catch (InterruptedException e)
		{
			throw new DataNotFoundException(e);
		}
		catch (ExecutionException e)
		{
			throw new DataNotFoundException(e);
		}
		if (job.getError() != null)
		{
			throw job.getError();
		}

		if (results == null || results.length == 0)
		{
			throw new DataNotFoundException("No data returned");
		}

		return results;
	}
	

	public Object selectSimpleObject(String query, Object[] values) throws DataNotFoundException, Throwable
	{
		Object[][] results = selectAll(query, values);
		
		if (results.length == 0 || results[0][0] == null)
		{
			throw new DataNotFoundException("No data returned");
		}

		return results[0][0];
	}

	
	public <T, J extends SQLiteJob<T>> J execute(J job) throws Throwable
	{
		J result = queue.execute(job);
		job.get();
		if (job.getError() != null)
		{
			throw job.getError();
		}
		return result;
	}


	public SQLiteQueue stop(boolean gracefully) throws InterruptedException
	{
		LOGGER.debug("Shutting down " + file + " database");
		return queue.stop(gracefully).join();
	}



	
}
