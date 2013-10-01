/*
 * TesterThread.java
 * 
 * Created on September 15, 2007, 9:52 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.queues.tester;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.queues.AbstractQueueThread;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.queues.QueueItem;
import com.grendelscan.queues.QueueThreadGroup;
import com.grendelscan.queues.QueueThreadState;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.TestJob;

/**
 * 
 * @author Administrator
 */
public class TesterThread extends AbstractQueueThread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TesterThread.class);
    private String currentModule;

    /** Creates a new instance of TesterThread */
    public TesterThread(final QueueThreadGroup threadGroup)
    {
        super(threadGroup);
    }

    public String getCurrentModule()
    {
        return currentModule;
    }

    @Override
    protected AbstractScanQueue getQueue()
    {
        return Scan.getInstance().getTesterQueue();
    }

    @Override
    public QueueThreadState getThreadState()
    {
        return threadState;
    }

    @Override
    protected void processNextItem(final QueueItem nextItem) throws InterruptedScanException
    {
        TestJob test = (TestJob) nextItem;
        currentModule = test.getModule().getName() + " (" + test.getModuleClass() + ")";
        Date startTime = new Date();
        try
        {
            test.runTest();
        }
        catch (InterruptedScanException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception caught when trying to run test module \"" + test.getModule().getName() + "\" (module number " + test.getModuleClass() + "): ", e);
        }
        Scan.getInstance().getTesterQueue().removeQueueItem(test);
        currentModule = "";
        Date stopTime = new Date();
        Scan.getInstance().getTesterQueue().addTime(test, stopTime.getTime() - startTime.getTime());
    }
}
