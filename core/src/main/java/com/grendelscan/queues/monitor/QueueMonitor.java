/**
 * 
 */
package com.grendelscan.queues.monitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.scan.InterruptedScanException;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author david
 *
 */
public class QueueMonitor implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMonitor.class);

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
				LOGGER.debug("Shutting down I guess", e);
				return;
			}
			catch (InterruptedException e)
			{
				LOGGER.debug("Shutting down I guess", e);
				return;
			}
		}
	}

}
