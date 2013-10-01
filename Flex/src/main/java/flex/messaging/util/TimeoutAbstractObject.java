/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

import edu.emory.mathcs.backport.java.util.concurrent.Future;

/**
 * This class defines the default implementation of TimeoutCapable,
 * providing the default behavior for an object that is capable of timing
 * out where that time out mechanism is managed by TimeoutManager.
 *
 * @exclude
 */
public abstract class TimeoutAbstractObject implements TimeoutCapable
{
    private long lastUse;
    private volatile boolean timeoutCanceled;
    private TimeoutManager timeoutManager;
    private Runnable timeoutTask;
    private Future timeoutFuture;
    private long timeoutPeriod;
    private final Object lock = new Object();

    /** {@inheritDoc} */
    @Override public void cancelTimeout()
    {
        if (timeoutCanceled)
            return;

        boolean purged = false;
        if ((timeoutManager != null) && (timeoutTask != null) && (timeoutFuture != null))
            purged = timeoutManager.unscheduleTimeout(this);

        if (!purged && (timeoutFuture != null))
        {
            timeoutFuture.cancel(false);
        }

        timeoutCanceled = true;
    }

    /** {@inheritDoc} */
    @Override public long getLastUse()
    {
        synchronized (lock)
        {
            return lastUse;
        }
    }

    /**
     * Updates the time this object was last used.
     * @param lastUse time this object was last used
     */
    public void setLastUse(long lastUse)
    {
        synchronized (lock)
        {
            this.lastUse = lastUse;
        }
    }

    /**
     * Updates the time this object was last used to be the current time.
     */
    public void updateLastUse()
    {
        synchronized (lock)
        {
            this.lastUse = System.currentTimeMillis();
        }
    }

    /**
     * Returns manager responsible for this object.
     * @return manager responsible for this object
     */
    public TimeoutManager getTimeoutManager()
    {
        synchronized (lock)
        {
            return timeoutManager;
        }
    }

    /**
     * Sets the manager responsible for this object.
     * @param timeoutManager manager responsible for this object
     */
    public void setTimeoutManager(TimeoutManager timeoutManager)
    {
        synchronized (lock)
        {
            this.timeoutManager = timeoutManager;
        }
    }

    /**
     * Returns the runnable task that will be executed once this object times out.
     * @return the runnable task that will be executed once this object times out
     */
    public Runnable getTimeoutTask()
    {
        synchronized (lock)
        {
            return timeoutTask;
        }
    }

    /**
     * Sets the runnable task that will be executed once this object times out.
     * @param timeoutTask the runnable task that will be executed once this object times out
     */
    public void setTimeoutTask(Runnable timeoutTask)
    {
        synchronized (lock)
        {
            this.timeoutTask = timeoutTask;
        }
    }

    /**
     * Return the object encapsulating result of the execution of this object once it has timed out.
     * @return the object encapsulating result of the execution of this object once it has timed out
     */
    public Future getTimeoutFuture()
    {
        synchronized (lock)
        {
            return timeoutFuture;
        }
    }

    /** {@inheritDoc} */
    @Override public void setTimeoutFuture(Future timeoutFuture)
    {
        synchronized (lock)
        {
            this.timeoutFuture = timeoutFuture;
        }
    }

    /** {@inheritDoc} */
    @Override public long getTimeoutPeriod()
    {
        synchronized (lock)
        {
            return timeoutPeriod;
        }
    }

    /**
     * Set the time to be elapsed before this object times out and its associated task gets executed.
     * @param timeoutPeriod the time to be elapsed before this object times out and its associated task gets executed
     */
    public void setTimeoutPeriod(long timeoutPeriod)
    {
        synchronized (lock)
        {
            this.timeoutPeriod = timeoutPeriod;
        }
    }
}
