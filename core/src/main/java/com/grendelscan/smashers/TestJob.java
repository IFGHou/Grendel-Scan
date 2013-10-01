/*
 * TestJob.java
 * 
 * Created on September 15, 2007, 10:07 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.smashers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.grendelscan.queues.QueueItem;
import com.grendelscan.scan.InterruptedScanException;

/**
 * 
 * @author David Byrne
 */
public abstract class TestJob implements Serializable, QueueItem
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static Integer lastID = 0;
    private static final Object lastIDLock = new Object();

    public static final void setLastID(final Integer lastID)
    {
        TestJob.lastID = lastID;
    }

    private final int id;
    protected final Set<Integer> dependencies;
    protected Class<? extends AbstractSmasher> moduleClass;

    protected TestJobStatus status = TestJobStatus.NOT_STARTED;

    public TestJob(final Class<? extends AbstractSmasher> moduleClass)
    {
        this.moduleClass = moduleClass;
        synchronized (lastIDLock)
        {
            id = ++lastID;
        }
        dependencies = new HashSet<Integer>();
    }

    public void addDependency(final TestJob dependency)
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

    public AbstractSmasher getModule()
    {
        return MasterTestModuleCollection.getInstance().getTestModule(moduleClass);
    }

    public Class<? extends AbstractSmasher> getModuleClass()
    {
        return moduleClass;
    }

    public TestJobStatus getStatus()
    {
        return status;
    }

    protected abstract void internalRunTest() throws InterruptedScanException;

    public void runTest() throws InterruptedScanException
    {
        status = TestJobStatus.RUNNING;
        internalRunTest();
        status = TestJobStatus.COMPLETE;
    }

    public void setStatus(final TestJobStatus status)
    {
        this.status = status;
    }
}
