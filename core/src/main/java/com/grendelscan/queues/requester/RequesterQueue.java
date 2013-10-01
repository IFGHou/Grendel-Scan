package com.grendelscan.queues.requester;


import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.queues.AbstractQueueThread;
import com.grendelscan.queues.AbstractTransactionBasedQueue;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.Scan;
/**
 * 
 * @author David Byrne
 */
public class RequesterQueue extends AbstractTransactionBasedQueue
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequesterQueue.class);
    private static final String REQUESTER_QUEUE_TABLE = "requester_queue";
    

	/** Creates a new instance of requesterQueue */
	public RequesterQueue()
	{
		super("Requester queue", REQUESTER_QUEUE_TABLE);
	}

	
	
	public void isRequestable(StandardHttpTransaction transaction) throws UnrequestableTransaction
	{
		if (transaction.isResponsePresent())
		{
			transaction.setUnrequestable(true);
			throw new UnrequestableTransaction("Transaction already executed: "  + transaction.getRequestWrapper().getURI());
		}
		
		if (!transaction.getRequestOptions().ignoreRestrictions)
		{
			if (transaction.getRequestDepth() > Scan.getScanSettings().getMaxRequestDepth())
			{
				transaction.setUnrequestable(true);
				throw new UnrequestableTransaction("Request depth too high: " + transaction.getRequestWrapper().getURI());
			}
			
			if (!Scan.getScanSettings().getUrlFilters().isUriAllowed(transaction.getRequestWrapper().getAbsoluteUriString()))
			{
				transaction.setUnrequestable(true);
				throw new UnrequestableTransaction("Out of scope URI: " + transaction.getRequestWrapper().getAbsoluteUriString());
			}
		}
		
//		if (transaction.getRequestOptions().onlyUriIfNew)
//		{
//			if (!Scan.getInstance().getTransactionRecord().hasUriBeenRequested(transaction, transaction.getRequestOptions().ignoreUser))
//			{
//				throw new UnrequestableTransaction("URI has been already requested: " + transaction.getRequestWrapper().getURI());
//			}
//		}
	}


	@Override
	protected boolean checkSubmittedTransaction(StandardHttpTransaction transaction)
	{
		try
		{
			isRequestable(transaction);
		}
		catch (UnrequestableTransaction e) 
		{
			LOGGER.warn("Illegal transaction set to requester queue: " + e.toString());
			return false;
		}

		return true;
	}

	@Override
	protected int getMaxThreadCount()
	{
		return Scan.getScanSettings().getMaxRequesterThreads();
	}

	@Override
	protected AbstractQueueThread getNewThread()
	{
		return new RequesterThread(getThreadGroup());
	}

	@Override
	public void addTransaction(StandardHttpTransaction transaction)
	{
		super.addTransaction(transaction);
	}

	@Override
	public void addTransactions(Collection<StandardHttpTransaction> transactions)
	{
		super.addTransactions(transactions);
	}

	@Override
	protected String getDBPath()
	{
		return "request-queue.db";
	}

	

}
