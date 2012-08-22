package com.grendelscan.queues;


public class TransactionQueueItem implements QueueItem
{
	public int transactionID;

	public TransactionQueueItem(int transactionID)
	{
		this.transactionID = transactionID;
	}
}
