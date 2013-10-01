package com.grendelscan.queues;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueThreadGroup extends ThreadGroup
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueThreadGroup.class);
	private List<AbstractQueueThread> threads;
	
	public QueueThreadGroup(String name)
	{
		super(name);
		threads = new ArrayList<AbstractQueueThread>();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable)
	{
		LOGGER.error("Thread " + thread.getName() + " died, exception was: ", throwable);
		thread.start();
	}
}
