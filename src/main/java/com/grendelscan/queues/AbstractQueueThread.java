package com.grendelscan.queues;

import com.grendelscan.logging.Log;
import com.grendelscan.scan.InterruptedScanException;

public abstract class AbstractQueueThread implements Runnable
{
	private static final long	THREAD_SLEEP_TIME	= 250;
	private boolean				lastWorkItem	= false;
	private Thread				thread;
	protected QueueThreadState	threadState;

	protected AbstractQueueThread(QueueThreadGroup threadGroup)
	{
		threadState = QueueThreadState.CREATING;
		thread = new Thread(threadGroup, this, getQueue().getName() + " thread");
	}

	protected abstract AbstractScanQueue getQueue();
	protected abstract void processNextItem(QueueItem nextItem) throws InterruptedScanException;

	
	@Override
	public void run()
	{
		try
		{
			while (!lastWorkItem) 
			{
				handlePause_isRunning();
				threadState = QueueThreadState.POLLING;
				QueueItem nextItem = getQueue().getNextQueueItem();

				handlePause_isRunning();
				if ((nextItem != null))
				{
					threadState = QueueThreadState.PROCESSING;
					processNextItem(nextItem);

					getQueue().removeQueueItem(nextItem);
					handlePause_isRunning();
				}
				else
				{
					threadState = QueueThreadState.SLEEPING;
					aLittleSleep();
				}
			}
		}
		catch (InterruptedScanException e)
		{
			Log.debug(getName() + " interrupted" + e.toString(), e);
		}
		threadState = QueueThreadState.TERMINATING;
	}

	/**
	 * This will properly interrupt the thread and tell it to process no other tasks. 
	 * It does not currently have the ability to abort a task already started. 
	 */
	public void interupt()
	{
		thread.interrupt();
	}
	
	protected void handlePause_isRunning() throws InterruptedScanException
	{
		getQueue().handlePause_isRunning();
	}
	
	public void start()
	{
		thread.start();
	}

	public String getName()
    {
	    return thread.getName();
    }


	public QueueThreadState getThreadState()
	{
		return threadState;
	}


	private void aLittleSleep() throws InterruptedScanException
	{
		try
		{
			Thread.sleep(THREAD_SLEEP_TIME);
		}
		catch (InterruptedException e)
		{
			throw new InterruptedScanException("Scan interupted", e);
		}
	}

//	protected void checkPause()
//	{
//		if (getQueue().isPaused())
//		{
//			QueueThreadState oldState = threadState;
//			threadState = QueueThreadState.PAUSED;
//			while (getQueue().isPaused())
//			{
//				aLittleSleep();
//			}
//			threadState = oldState;
//		}
//	}


	final protected boolean isLastWorkItem()
	{
		return lastWorkItem;
	}

	public final boolean isAlive()
	{
		return thread.isAlive();
	}

	public final void setLastWorkItem(boolean lastWorkItem)
	{
		this.lastWorkItem = lastWorkItem;
	}

}
