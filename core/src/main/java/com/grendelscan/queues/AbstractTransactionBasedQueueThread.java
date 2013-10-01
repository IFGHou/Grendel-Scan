package com.grendelscan.queues;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

public abstract class AbstractTransactionBasedQueueThread extends AbstractQueueThread
{

    public AbstractTransactionBasedQueueThread(final QueueThreadGroup threadGroup)
    {
        super(threadGroup);
    }

    @Override
    protected void processNextItem(final QueueItem nextItem) throws InterruptedScanException
    {
        int transactionID = ((TransactionQueueItem) nextItem).transactionID;
        processNextTransaction(Scan.getInstance().getTransactionRecord().getTransaction(transactionID));
    }

    protected abstract void processNextTransaction(StandardHttpTransaction transaction) throws InterruptedScanException;

}
