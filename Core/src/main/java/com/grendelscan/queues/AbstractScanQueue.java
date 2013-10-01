package com.grendelscan.queues;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.logging.Log;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

public abstract class AbstractScanQueue implements DatabaseUser
{
	private static final int				MIN_QUEUE_AGE_BEFORE_THREAD_CREATION	= 30000;
	private final String					name;
	private boolean							paused									= false;
	private long							queueEmptyStart;
	private long							queueFullStart;
	boolean							shutdown = false;
	
	private boolean							running									= true;
	private final QueueThreadGroup			threadGroup;
	final List<AbstractQueueThread>	threads; // intentionally default visibility
	private final String queueTableName;
	protected final Database database;
	
	public abstract QueueItem getNextQueueItem();
	protected abstract int getMaxThreadCount();
	protected abstract void removeQueueItem(QueueItem finishedItem);
	protected abstract AbstractQueueThread getNewThread();
	protected abstract String getDBPath();
	
	protected AbstractScanQueue(String queueName, String queueTableName)
	{
		this.name = queueName;
		this.queueTableName = queueTableName;
		threadGroup = new QueueThreadGroup(name + " - thread group");
		threads = new ArrayList<AbstractQueueThread>();
		queueFullStart = 0;
		database = new Database(getDBPath());
		initializeDatabase();
	}
	
	
	public void start(int initialThreadCount)
	{
		startNewThreads(initialThreadCount);
	}

	private synchronized void startNewThreads(int count)
	{
		if (count + threads.size() > getMaxThreadCount())
		{
			count = getMaxThreadCount() - threads.size();
		}
		
		for (int i = 0; i < count; i++)
		{
			AbstractQueueThread newThread = getNewThread();
			threads.add(newThread);
			newThread.start();
		}
	}

	private synchronized void stopThreads(int count)
	{
		if (count >= threads.size())
		{
			count = threads.size() - 1;
		}
		
		for (int i = 0; i < count; i++)
		{
			getThreads().get(i).setLastWorkItem(true);
		}
	}

	public int getQueueLength()
	{
		int length = 0;
		try
		{
			length = database.selectSimpleInt(
					"SELECT COUNT(*) " +
					"FROM " + queueTableName + " " +
					"WHERE locked = 0", new Object[]{});
		}
		catch (Throwable e)
		{
			Log.error("Problem with getting " + name + " queue length: " + e.toString(), e);
		}
		
		return length;
	}

	
	public String getName()
	{
		return name;
	}


	public QueueThreadGroup getThreadGroup()
	{
		return threadGroup;
	}

	public List<AbstractQueueThread> getThreads()
	{
		return threads;
	}

	/**
	 * 
	 * @return True if the queue isn't running
	 */
	public void handlePause_isRunning() throws InterruptedScanException
	{
		while(isPaused() && running)
		{
			synchronized(this)
			{
					try
					{
						wait(250);
					}
					catch (InterruptedException e)
					{
						throw new InterruptedScanException(getName() + " queue was interrupted: " + e.toString(), e);
					}
			}
		}
		if (!running)
		{
			throw new InterruptedScanException(getName() + " queue is stopped");
		}
	}

//	final public void checkIsRunning() throws InterruptedException
//	{
//		if (!running)
//		{
//			throw new InterruptedException(getName() + " queue is stopped");
//		}
//		Scan.getInstance().isTerminated();
//	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	void pruneThreads()
	{
		List<AbstractQueueThread> tempThreads = new ArrayList<AbstractQueueThread>(threads);
		for(AbstractQueueThread thread: tempThreads)
		{
			if(!thread.isAlive())
			{
				threads.remove(thread);
			}
		}
	}
	
	@Override
	public void shutdown(final boolean gracefully)
	{
		Log.info("Shutting down " + getName() + " queue");
		running = false;
		for (int i = 0; i < threads.size(); i++)
		{
			threads.get(i).interupt();
		}
		
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int timeout = 5000;
				int delay = 250;
				while (timeout > 0 && threads.size() > 0)
				{
					pruneThreads();
					synchronized(this)
					{
						try
						{
							wait(delay);
						}
						catch (InterruptedException e)
						{
							break;
						}
					}
					timeout -= delay;
				}
				interuptThreads();
				try
				{
					database.stop(gracefully).join();
				}
				catch (InterruptedException e)
				{
					Log.debug(getName() + " shutdown interupted: " + e.toString(), e);
				}
				shutdown = true;
			}
		}, getName() + " queue shutdown");
		t.start();
	}

	public void checkThreadCount() throws InterruptedScanException
	{
		Date date = new Date();
		if (threads.size() > getMaxThreadCount())
		{
			stopThreads(threads.size() - getMaxThreadCount());
		}
		
		handlePause_isRunning();
		if (getQueueLength() > 0)
		{
			queueEmptyStart = 0;
			if (threads.size() < getMaxThreadCount())
			{
				if (queueFullStart > 0)
				{
					if (date.getTime() - queueFullStart > MIN_QUEUE_AGE_BEFORE_THREAD_CREATION)
					{
						startNewThreads(1);
					}
				}
				else
				{
					queueFullStart = date.getTime();
					startNewThreads(1);
				}
			}
		}
		else
		{
			queueFullStart = 0;
			if (queueEmptyStart > 0)
			{
				if (date.getTime() - queueFullStart > MIN_QUEUE_AGE_BEFORE_THREAD_CREATION)
				{
					stopThreads(1);
				}
			}
			else
			{
				queueEmptyStart = date.getTime();
			}
		}
		pruneThreads();
	}

	protected abstract void initializeOldDatabase();
	private void initializeDatabase()
	{
		try
		{
			if (database.tableExists(queueTableName))
			{
				Log.debug("Initializing old database for " + name + " job storage");
		        	database.execute("UPDATE " + queueTableName + " SET locked = 0");
		        	initializeOldDatabase();
			}
			else
			{
				initializeNewDatabase();
			}
		}
		catch (Throwable e) 
		{
			Log.fatal("Problem with updating database: " + e.toString(), e);
			System.exit(1);
		}
	}

	protected abstract void initializeNewDatabase();
	public String getQueueTableName()
	{
		return queueTableName;
	}
	
	/**
	 * Intentionally default visibility
	 */
	void interuptThreads()
	{
		pruneThreads();
		for(AbstractQueueThread thread: threads)
		{
			thread.interupt();
		}
	}
	
	public final boolean isShutdown()
	{
		return shutdown;
	}
	public final boolean isPaused()
	{
		return paused || Scan.getInstance().isPaused();
	}
}
