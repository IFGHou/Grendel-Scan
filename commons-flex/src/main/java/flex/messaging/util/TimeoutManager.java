/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;

/**
 * This class provides a means of managing TimeoutCapable objects. It leverages facilities in the the Java concurrency package to provide a common utility for scheduling timeout Futures and managing
 * the underlying worker thread pools.
 * 
 * @author neville
 * @exclude
 */
public class TimeoutManager
{
    private static final String LOG_CATEGORY = LogCategories.TIMEOUT;

    private ScheduledThreadPoolExecutor timeoutService;

    /**
     * Default constructor calls parameterized constructor will a null factory argument.
     */
    public TimeoutManager()
    {
        this(null);
    }

    /**
     * Constructs a new TimeoutManager using the passed in factory for thread creation.
     * 
     * @param tf
     *            ThreadFactory
     */
    public TimeoutManager(ThreadFactory tf)
    {
        if (tf == null)
        {
            tf = new MonitorThreadFactory();
        }
        timeoutService = new ScheduledThreadPoolExecutor(1, tf);
    }

    /**
     * Schedule a task to be executed in the future.
     * 
     * @param t
     *            task to be executed at some future time
     * @return a Future object that enables access to the value(s) returned by the task
     */
    public Future scheduleTimeout(TimeoutCapable t)
    {
        Future future = null;
        if (t.getTimeoutPeriod() > 0)
        {
            Runnable timeoutTask = new TimeoutTask(t);
            future = timeoutService.schedule(timeoutTask, t.getTimeoutPeriod(), TimeUnit.MILLISECONDS);
            t.setTimeoutFuture(future);
            if (t instanceof TimeoutAbstractObject)
            {
                TimeoutAbstractObject timeoutAbstract = (TimeoutAbstractObject) t;
                timeoutAbstract.setTimeoutManager(this);
                timeoutAbstract.setTimeoutTask(timeoutTask);
            }
            if (Log.isDebug())
                Log.getLogger(LOG_CATEGORY).debug(
                                "TimeoutManager '" + System.identityHashCode(this) + "' has scheduled instance '" + System.identityHashCode(t) + "' of type '" + t.getClass().getName() + "' to be timed out in " + t.getTimeoutPeriod()
                                                + " milliseconds. Task queue size: " + timeoutService.getQueue().size());
        }
        return future;
    }

    /**
     * Cancel the execution of a future task and remove all references to it.
     * 
     * @param timeoutAbstract
     *            the task to be canceled
     * @return true if cancellation were successful
     */
    public boolean unscheduleTimeout(TimeoutAbstractObject timeoutAbstract)
    {
        Object toRemove = timeoutAbstract.getTimeoutFuture();
        /*
         * In more recent versions of the backport, they are requiring that we pass in the Future returned by the schedule method. This should always implement Runnable even in 2.2 but I'm a little
         * paranoid here so just to be sure, if we get a future which is not a runnable we go back to the old code which calls the remove on the instance we passed into the schedule method.
         */
        if (!(toRemove instanceof Runnable))
            toRemove = timeoutAbstract.getTimeoutTask();
        if (timeoutService.remove((Runnable) toRemove))
        {
            if (Log.isDebug())
                Log.getLogger(LOG_CATEGORY).debug(
                                "TimeoutManager '" + System.identityHashCode(this) + "' has removed the timeout task for instance '" + System.identityHashCode(timeoutAbstract) + "' of type '" + timeoutAbstract.getClass().getName()
                                                + "' that has requested its timeout be cancelled. Task queue size: " + timeoutService.getQueue().size());
        }
        else
        {
            Future timeoutFuture = timeoutAbstract.getTimeoutFuture();
            timeoutFuture.cancel(false); // Don't interrupt it if it's running.
            if (Log.isDebug())
                Log.getLogger(LOG_CATEGORY).debug(
                                "TimeoutManager '" + System.identityHashCode(this) + "' cancelling timeout task for instance '" + System.identityHashCode(timeoutAbstract) + "' of type '" + timeoutAbstract.getClass().getName()
                                                + "' that has requested its timeout be cancelled. Task queue size: " + timeoutService.getQueue().size());
            if (timeoutFuture.isDone())
            {
                timeoutService.purge(); // Force the service to give up refs to task immediately rather than hanging on to them.
                if (Log.isDebug())
                    Log.getLogger(LOG_CATEGORY).debug("TimeoutManager '" + System.identityHashCode(this) + "' purged queue of any cancelled or completed tasks. Task queue size: " + timeoutService.getQueue().size());
            }
        }

        // to aggressively clean up memory remove the reference from the unscheduled timeout to its
        // time out object
        Object unscheduledTimeoutTask = timeoutAbstract.getTimeoutTask();
        if (unscheduledTimeoutTask != null && unscheduledTimeoutTask instanceof TimeoutTask)
            ((TimeoutTask) timeoutAbstract.getTimeoutTask()).clearTimeoutCapable();

        return true;
    }

    /**
     * Cancel any future tasks.
     */
    public void shutdown()
    {
        timeoutService.shutdown();
    }

    class MonitorThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(Runnable r)
        {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("TimeoutManager");
            return t;
        }
    }

    class TimeoutTask implements Runnable
    {
        private TimeoutCapable timeoutObject;

        /**
         * Removes the reference from this timeout task to the object that would have been timed out. This is useful for memory clean up when timeouts are unscheduled.
         */
        public void clearTimeoutCapable()
        {
            timeoutObject = null;
        }

        public TimeoutTask(TimeoutCapable timeoutObject)
        {
            this.timeoutObject = timeoutObject;
        }

        @Override
        public void run()
        {
            long inactiveMillis = System.currentTimeMillis() - timeoutObject.getLastUse();
            if (inactiveMillis >= timeoutObject.getTimeoutPeriod())
            {
                timeoutObject.timeout();
                if (Log.isDebug())
                    Log.getLogger(LOG_CATEGORY).debug(
                                    "TimeoutManager '" + System.identityHashCode(TimeoutManager.this) + "' has run the timeout task for instance '" + System.identityHashCode(timeoutObject) + "' of type '" + timeoutObject.getClass().getName()
                                                    + "'. Task queue size: " + timeoutService.getQueue().size());
            }
            else
            {
                // Reschedule timeout and store new Future for cancellation.
                timeoutObject.setTimeoutFuture(timeoutService.schedule(this, (timeoutObject.getTimeoutPeriod() - inactiveMillis), TimeUnit.MILLISECONDS));
                if (Log.isDebug())
                    Log.getLogger(LOG_CATEGORY).debug(
                                    "TimeoutManager '" + System.identityHashCode(TimeoutManager.this) + "' has rescheduled a timeout for the active instance '" + System.identityHashCode(timeoutObject) + "' of type '" + timeoutObject.getClass().getName()
                                                    + "'. Task queue size: " + timeoutService.getQueue().size());
            }
        }
    }
}
