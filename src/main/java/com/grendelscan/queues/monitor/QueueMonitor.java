/**
 * 
 */
package com.grendelscan.queues.monitor;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.logging.Log;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.scan.InterruptedScanException;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author david
 *
 */
public class QueueMonitor implements Runnable
{

	private List<AbstractScanQueue> queues;
	private Thread thread;
	
	public QueueMonitor()
	{
		queues = Collections.synchronizedList(new ArrayList<AbstractScanQueue>(1));
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("Queue monitor");
		thread.start();
	}
	
	public void addQueue(AbstractScanQueue queue)
	{
		queues.add(queue);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				for(AbstractScanQueue queue: queues)
				{
					queue.checkThreadCount();
				}
				Thread.sleep(1000);
			}
			catch (InterruptedScanException e)
			{
				Log.debug("Shutting down I guess", e);
				return;
			}
			catch (InterruptedException e)
			{
				Log.debug("Shutting down I guess", e);
				return;
			}
		}
	}

}
