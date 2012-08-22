/*
 * requesterThread.java
 * 
 * Created on September 10, 2007, 6:29 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.queues.requester;

import com.grendelscan.logging.Log;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.queues.AbstractTransactionBasedQueueThread;
import com.grendelscan.queues.QueueThreadGroup;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
public class RequesterThread extends AbstractTransactionBasedQueueThread
{
    
    
	public RequesterThread(QueueThreadGroup threadGroup)
	{
		super(threadGroup);
	}

	@Override
	protected AbstractScanQueue getQueue()
	{
		return Scan.getInstance().getRequesterQueue();
	}

	@Override
	protected void processNextTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		try
		{
			transaction.execute();
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn("Somehow an unrequestable transaction is in the request queue (" +
					transaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
		}
	}



}
