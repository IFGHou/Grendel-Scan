package com.grendelscan.queues;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.logging.Log;

public class QueueThreadGroup extends ThreadGroup
{
	private List<AbstractQueueThread> threads;
	
	public QueueThreadGroup(String name)
	{
		super(name);
		threads = new ArrayList<AbstractQueueThread>();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable)
	{
		Log.error("Thread " + thread.getName() + " died, exception was: ", throwable);
		thread.start();
	}
}
