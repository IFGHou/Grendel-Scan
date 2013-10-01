package com.grendelscan.queues;

import java.util.Collection;

import com.grendelscan.data.database.DataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

public abstract class AbstractTransactionBasedQueue extends AbstractScanQueue
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionBasedQueue.class);

	protected AbstractTransactionBasedQueue(String queueName, String queueTableName)
	{
		super(queueName, queueTableName);
	}
	
	protected abstract boolean checkSubmittedTransaction(StandardHttpTransaction transaction);
	
	private String getInsert(String transactionID)
	{
		return "INSERT INTO " + getQueueTableName() + " (transaction_id, locked) VALUES (" + transactionID + ", 0)";
	}
	
	protected void addTransaction(StandardHttpTransaction transaction) 
	{
		transaction.save();
		if (checkSubmittedTransaction(transaction))
		{
        	try
			{
				database.execute(getInsert(String.valueOf(transaction.getId())));
			}
			catch (Throwable e)
			{
				LOGGER.error("Huge problem with adding a queue item: " + e.toString(), e);
			}
		}
	}
	
	protected void addTransactions(Collection<StandardHttpTransaction> transactions) 
	{
		for (StandardHttpTransaction transaction: transactions)
		{
			addTransaction(transaction);
		}
	}

	@Override
	public synchronized QueueItem getNextQueueItem()
	{
        try
		{
        	int transactionID = database.selectSimpleInt(
    								"SELECT transaction_id FROM " + getQueueTableName() + " " +
									"WHERE locked = 0 LIMIT 1", new Object[]{});
        	
	        database.execute("UPDATE " + getQueueTableName() + " " +
	        						"SET locked = 1 " +
									"WHERE transaction_id = " + transactionID);
			return new TransactionQueueItem(transactionID);
		}
		catch (DataNotFoundException e)
		{
			// No problem, just an empty queue
		}
		catch (Throwable e)
		{
			LOGGER.error("Big problem with transaction db: " + e.toString(), e);
		}
		return null;
	}

	
	@Override
	protected void initializeNewDatabase()
	{
		LOGGER.debug("Initializing new database for " + getName() + " job storage");
		try
		{
	        String tableQuery = 
				"CREATE TABLE " + getQueueTableName() + " (\n" + 
				"transaction_id INT,\n" +
				"locked BOOLEAN default 0,\n" +
				"PRIMARY KEY (transaction_id))";
			String indexQuery = "CREATE INDEX IDX_" + getQueueTableName().toUpperCase() + "_LOCKED " +
					"ON " + getQueueTableName() + " (locked, transaction_id)";
        	database.execute(tableQuery);
        	database.execute(indexQuery);
		}
		catch (Throwable e) 
		{
			LOGGER.error("Problem with creating database for " + getName() + " queue: " + e.toString(), e);
			System.exit(1);
		}
	}

	@Override
	protected void removeQueueItem(QueueItem finishedItem)
	{
		int transactionID = ((TransactionQueueItem) finishedItem).transactionID;
		try
		{
			database.execute(
				"DELETE FROM " + getQueueTableName() + " " +
				"WHERE transaction_ID = " + transactionID);
		}
		catch (Throwable e)
		{
			LOGGER.error("Huge problem with removing a queue item: " + e.toString(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.queues.AbstractScanQueue#initializeOldDatabase()
	 */
	@Override
	protected void initializeOldDatabase()
	{
	}

}
