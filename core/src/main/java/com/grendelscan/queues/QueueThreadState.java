package com.grendelscan.queues;

public enum QueueThreadState
{
	POLLING("Polling"), SLEEPING("Sleeping"), PROCESSING("Processing"), TERMINATING("Terminating"), PAUSED("Paused"), CREATING("Creating");

	private String text;

	private QueueThreadState(String text)
	{
		this.text = text;
	}

	public String getText()
    {
    	return text;
    }
}
