/*
 * TestJob.java
 * 
 * Created on September 15, 2007, 10:07 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.tests.testJobs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.grendelscan.queues.QueueItem;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModuleUtils.MasterTestModuleCollection;
import com.grendelscan.tests.testModules.TestModule;

/**
 * 
 * @author David Byrne
 */
public abstract class TestJob implements Serializable, QueueItem
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	private static Integer			lastID				= 0;
	private static final Object		lastIDLock			= new Object();
	private final int				id;

	protected final Set<Integer>	dependencies;
	protected Class<? extends TestModule>				moduleClass;
	protected TestJobStatus			status				= TestJobStatus.NOT_STARTED;

	public TestJob(Class<? extends TestModule> moduleClass)
	{
		this.moduleClass = moduleClass;
		synchronized (lastIDLock)
		{
			id = ++lastID;
		}
		dependencies = new HashSet<Integer>();
	}

	public void addDependency(TestJob dependency)
	{
		dependencies.add(dependency.getId());
	}

	public Set<Integer> getDependencies()
	{
		return dependencies;
	}

	public int getId()
	{
		return id;
	}

	public TestModule getModule()
	{
		return MasterTestModuleCollection.getInstance().getTestModule(moduleClass);
	}

	public Class<? extends TestModule> getModuleClass()
	{
		return moduleClass;
	}

	public TestJobStatus getStatus()
	{
		return status;
	}

	public void runTest() throws InterruptedScanException
	{
		status = TestJobStatus.RUNNING;
		internalRunTest();
		status = TestJobStatus.COMPLETE;
	}

	public void setStatus(TestJobStatus status)
	{
		this.status = status;
	}

	protected abstract void internalRunTest() throws InterruptedScanException;

	public static final void setLastID(Integer lastID)
	{
		TestJob.lastID = lastID;
	}
}
