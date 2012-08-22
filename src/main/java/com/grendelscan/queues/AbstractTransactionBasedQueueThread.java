package com.grendelscan.queues;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

public abstract class AbstractTransactionBasedQueueThread extends AbstractQueueThread
{

	public AbstractTransactionBasedQueueThread(QueueThreadGroup threadGroup)
	{
		super(threadGroup);
	}

	protected abstract void processNextTransaction(StandardHttpTransaction transaction) throws InterruptedScanException;
	
	
	@Override
	protected void processNextItem(QueueItem nextItem) throws InterruptedScanException
	{
		int transactionID = ((TransactionQueueItem) nextItem).transactionID;
		processNextTransaction(Scan.getInstance().getTransactionRecord().getTransaction(transactionID));
	}

}
