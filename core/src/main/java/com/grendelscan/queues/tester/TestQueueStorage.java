//package com.grendelscan.queues.tester;
//
//import java.io.IOException;
//import java.sql.Blob;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.grendelscan.data.Database;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.tests.testJobs.TestJob;
//import com.grendelscan.commons.Log;
//
//public class TestQueueStorage
//{
//
//	private TesterQueue queue;
//	private int storedGroups = 0;
//	private int storedJobs = 0;
//	private Map<Integer, Integer> pendingCount;
//
//	public void initializeDatabase()
//	{
//		LOGGER.debug("Initializing database for test job storage");
//		try
//		{
//	        String tableQuery = 
//				"CREATE TABLE test_queue (\n" + 
//				"test_id INT,\n" +
//				"group_id INT,\n" +
//				"serialized_test_job BLOB,\n" +
//				"PRIMARY KEY (test_id))";
//			String indexQuery = "CREATE INDEX IDX_TEST_JOB_GROUP_ID ON test_queue (group_id)";
//        	Scan.getInstance().getDatabase().execute(tableQuery);
//        	Scan.getInstance().getDatabase().execute(indexQuery);
//		}
//		catch (SQLException e) 
//		{
//			LOGGER.error("Problem with creating database: " + e.toString(), e);
//			System.exit(1);
//		}
//	}
//	
//	public void empty()
//	{
//		try
//		{
//        	Scan.getInstance().getDatabase().execute("DELETE FROM test_queue");
//            storedJobs = 0;
//            storedGroups = 0;
//        }
//		catch (SQLException e) 
//		{
//			LOGGER.error("Problem with emptying database: " + e.toString(), e);
//			System.exit(1);
//		}
//	}
//
//	public void storeTestGroup(Set<TestJob> tests, int jobGroupNumber)
//	{
//		
//		Blob blobs[] = new Blob[tests.size()];
//		int ids[] = new int[tests.size()];
//		try
//        {
//			int index = 0;
//			for (TestJob test: tests)
//            {
//	            blobs[index] = Database.serializableToBlob(test);
//	            ids[index] = test.getId();
//	            index++;
//            }
//			
//			synchronized(Scan.getInstance().getDatabase())
//	        {
//                Connection connection = Scan.getInstance().getDatabase().getConnection();
//                PreparedStatement testInsertStatement = connection.prepareStatement("INSERT INTO test_queue (test_id, group_id, serialized_test_job) VALUES (?, ?, ?)");
//                connection.setAutoCommit(false);
//	        	for (int i = 0; i < blobs.length; i++)
//                {
//	        		testInsertStatement.setInt(1, ids[i]);
//	        		testInsertStatement.setInt(2, jobGroupNumber);
//	        		testInsertStatement.setBlob(3, blobs[i]);
//	        		testInsertStatement.addBatch();
//	        	}
//        		testInsertStatement.executeBatch();
//        		connection.commit();
//        		connection.setAutoCommit(true);
//        		testInsertStatement.close();
//	        }
//        	storedGroups++;
//        	storedJobs += tests.size();
//    		for (TestJob job: tests)
//    		{
//    			int moduleNumber = job.getModule().getModuleNumber();
//    			Integer count = pendingCount.get(moduleNumber);
//    			if (count == null)
//    			{
//    				count = 0;
//    			}
//    			pendingCount.put(moduleNumber, count + 1);
//    		}
//        }
//        catch (SQLException e)
//        {
//			LOGGER.error("Problem storing test job group in database: " + e.toString(), e);
//        }
//        catch (IOException e)
//        {
//			LOGGER.error("Problem storing test job group in database: " + e.toString(), e);
//        }
//	}
//	
//	
//	public List<TestJob> getNextTestGroup()
//	{
//		List<TestJob> tests = new ArrayList<TestJob>();
//		
//		try
//        {
//			int groupID;
//	        synchronized(Scan.getInstance().getDatabase())
//	        {
//	        	if (storedJobs > 0)
//	        	{
//	                PreparedStatement minGroupSelectStatement = Scan.getInstance().getDatabase().
//	                	prepareStatement("SELECT MIN(group_id) FROM test_queue");
//	                minGroupSelectStatement.execute();
//	        		ResultSet minGroupResults = minGroupSelectStatement.getResultSet();
//	        		minGroupResults.next();
//	        		groupID = minGroupResults.getInt(1);
//	        		minGroupResults.close();
//	        		minGroupSelectStatement.close();
//	        		
//        			PreparedStatement testSelectStatement = Scan.getInstance().getDatabase().prepareStatement("SELECT serialized_test_job FROM test_queue WHERE group_id = ?");
//					testSelectStatement.setInt(1, groupID);
//					testSelectStatement.setMaxRows(0);
//					testSelectStatement.execute();
//	        		ResultSet results = testSelectStatement.getResultSet();
//
//	        		while (results.next())
//	        		{
//	        			tests.add((TestJob) Database.blobToObject(results.getBlob(1)));
//	        		}
//	        		results.close();
//	        		testSelectStatement.close();
//
//	        		PreparedStatement groupDeleteStatement = Scan.getInstance().getDatabase().prepareStatement("DELETE FROM test_queue WHERE group_id = ?");
//					groupDeleteStatement.setInt(1, groupID);
//	        		groupDeleteStatement.execute();
//	        		groupDeleteStatement.close();
//	        		
//	        		storedJobs -= tests.size();
//	        		storedGroups--;
//	        	}
//	        }
//        }
//        catch (SQLException e)
//        {
//			LOGGER.error("Problem loading test group from database: " + e.toString(), e);
//        }
//        catch (IOException e)
//        {
//			LOGGER.error("Problem loading test group from database: " + e.toString(), e);
//        }
//        catch (ClassNotFoundException e)
//        {
//			LOGGER.error("Problem loading test group from database: " + e.toString(), e);
//        }
//        
//		for (TestJob job: tests)
//		{
//			Integer count;
//			int moduleNumber = job.getModule().getModuleNumber();
//			count = pendingCount.get(moduleNumber) - 1;
//			pendingCount.put(moduleNumber, count);
//		}
//
//		return tests;
//	}
//
//	public TestQueueStorage(TesterQueue queue)
//    {
//		this.queue = queue;
//	    pendingCount = new HashMap<Integer, Integer>();
//	    initializeDatabase();
//
//    }
//
//	public int getStoredJobs()
//    {
//    	return storedJobs;
//    }
//
//	public Map<Integer, Integer> getPendingCount()
//    {
//    	return pendingCount;
//    }
//
//	public int getStoredGroups()
//    {
//    	return storedGroups;
//    }
//	
//}
