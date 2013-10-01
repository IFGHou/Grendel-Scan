/**
 * 
 */
package com.grendelscan.ui.UpdateService;

import java.util.HashMap;
import java.util.Map;


/**
 * @author david
 *
 */
public class UpdateService implements Runnable
{
	private Map<UpdateServiceDataProvider, UpdateTarget> targets;
	private Map<UpdateServiceDataProvider, Long> times;
	
	private Object targetLock = new Object();

	private final static long UPDATE_DELAY = 250;

	private Thread thread;
	
	
	public UpdateService()
	{
		targets = new HashMap<UpdateServiceDataProvider, UpdateTarget>(1);
		times = new HashMap<UpdateServiceDataProvider, Long>(1);
		thread = new Thread(this);
		thread.setName("GUI Update Service");
		thread.setPriority(Thread.MAX_PRIORITY - 2);
		thread.setDaemon(true);
		thread.start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		while(true)
		{
			synchronized(targetLock)
			{
				for(UpdateServiceDataProvider provider: targets.keySet())
				{
					if (provider.getLastModified() > times.get(provider))
					{
						times.put(provider, provider.getLastModified());
						targets.get(provider).updateView();
					}
				}
			}
//			synchronized(this)
			{
				try
				{
					Thread.sleep(UPDATE_DELAY);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
		}
	}
	

	public void add(UpdateServiceDataProvider key, UpdateTarget value)
	{
		synchronized(targetLock)
		{
			times.put(key, 0L);
			targets.put(key, value);
		}
	}

	public void remove(UpdateServiceDataProvider key)
	{
		synchronized(targetLock)
		{
			times.remove(key);
			targets.remove(key);
		}
	}

}
