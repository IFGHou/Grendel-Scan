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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import flex.messaging.FlexComponent;
import flex.messaging.config.ConfigMap;

/**
 * This class watches for changes on files and forces a re-deploy by touching the specified files.
 * 
 * @exclude
 */
public class RedeployManager implements FlexComponent
{
    private boolean enabled;
    private long watchInterval;
    private List watches;
    private List touches;

    private ScheduledExecutorService redeployService;
    private boolean started;
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a new <code>RedeployManager</code> with default settings.
     */
    public RedeployManager()
    {
        this(null);
    }

    /**
     * Constructs a new <code>RedeployManager</code> with the supplied thread
     * factory.
     * 
     * @param tf Thread factory to use for the scheduled service used.
     */
    public RedeployManager(ThreadFactory tf)
    {
        if (tf == null)
            tf = new MonitorThreadFactory();

        enabled = false;        
        touches = new ArrayList();
        watchInterval = 20;
        watches = new ArrayList();
        
        redeployService = Executors.newSingleThreadScheduledExecutor(tf);
    }

    //--------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods. 
    //
    //--------------------------------------------------------------------------
   
    /**
     * Implements FlexComponents.initialize.
     * This is no-op for RedeployManager as it does not have an id and all 
     * its properties are directly settable. 
     */
    @Override public void initialize(String id, ConfigMap properties)
    {       
        // No-op
    }
    
    /**
     * Implements FlexComponent.start.
     * Starts the <code>RedeployManager</code>. 
     */
    @Override public void start()
    {      
        if (!started && enabled)
        {
            redeployService.schedule(new RedeployTask(), 20 * 1000, TimeUnit.MILLISECONDS);
            started = true;
        }
    }

    /**
     * Stops the <code>RedeployManager</code>. 
     */
    @Override public void stop()
    {
        if (started && enabled)
        {
            redeployService.shutdown();
            started = false;
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Public Methods
    //         
    //--------------------------------------------------------------------------
    
    /**
     * Returns whether redeploy is enabled or not.
     * 
     * @return <code>true</code> if redeploy is enabled; otherwise <code>false</code>. 
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * Sets whether redeploy is enabled or not.
     * 
     * @param enabled Whether redeploy is enabled or not.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;        
    }

    /**
     * Implements FlexComponent.isStarted.
     * Returns whether the RedeployManager is started or not.
     * 
     * @return <code>true</code> if the component is started; otherwise <code>false</code>.
     */
    @Override public boolean isStarted()
    {
        return started;
    }
    
    /**
     * Returns the watch interval.
     * 
     * @return The watch interval.
     */
    public long getWatchInterval()
    {
        return watchInterval;
    }

    /**
     * Sets the watch interval.
     * 
     * @param watchInterval The watch interval to set.
     */
    public void setWatchInterval(long watchInterval)
    {
        this.watchInterval = watchInterval;
    }

    /**
     * Returns the list of watch files.
     * 
     * @return The list of watch files.
     */
    public List getWatchFiles()
    {
        return watches;
    }

    /**
     * Adds a watch file. Note that the watch file set with this method should 
     * not contain the context.root token.
     * 
     * @param watch The watch file to add.
     */
    public void addWatchFile(String watch)
    {       
        watches.add(watch);
    }
    
    /**
     * Sets the list of watch files. Note that watch files set with this method 
     * should not contain contain the context.root token.
     * 
     * @param watches The list of watch files to set.
     */
    public void setWatchFiles(List watches)
    {
        this.watches = watches;
    }
    
    /**
     * Returns the list of touch files.
     * 
     * @return The list of touch files.
     */
    public List getTouchFiles()
    {
        return touches;
    }

    /**
     * Adds a touch file. Note that the touch file set with this method should
     * not contain the context.root token.
     * 
     * @param touch The touch file to add.
     */
    public void addTouchFile(String touch)
    {
        touches.add(touch);
    }
    
    /**
     * Sets the list of touch files. Note that touch files set with this method
     * should not contain the context.root token.
     * 
     * @param touches The list of touch files to set.
     */
    public void setTouchFiles(List touches)
    {
        this.touches = touches;
    }
    
    /**
     * Forces the redeployment.
     */
    public void forceRedeploy()
    {
        Iterator iter = touches.iterator();
        while (iter.hasNext())
        {
            String filename = (String)iter.next();
            File file = new File(filename);
            if (file.exists() && (file.isFile() || file.isDirectory()))
            {
                file.setLastModified(System.currentTimeMillis());
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Nested Classes
    //
    //--------------------------------------------------------------------------
    
    class MonitorThreadFactory implements ThreadFactory
    {
        @Override public Thread newThread(Runnable r)
        {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("RedeployManager");
            return t;
        }
    }

    class RedeployTask implements Runnable
    {
        @Override public void run()
        {
            boolean redeploy = false;

            // check if any of the redeploy watches have changed
            Iterator iter = watches.iterator();
            while (iter.hasNext() && !redeploy)
            {
                WatchedObject watched = (WatchedObject)iter.next();
                if (!watched.isUptodate())
                    redeploy = true;
            }

            if (redeploy)
                forceRedeploy();
            else
                redeployService.schedule(new RedeployTask(), watchInterval * 1000, TimeUnit.MILLISECONDS);
        }
    }
}
