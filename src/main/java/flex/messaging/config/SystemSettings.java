/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import flex.messaging.log.Log;
import flex.messaging.log.Logger;
import flex.messaging.util.PropertyStringResourceLoader;
import flex.messaging.util.ResourceLoader;
import flex.messaging.util.WatchedObject;

/**
 * @exclude
 */
public class SystemSettings
{
    private ResourceLoader resourceLoader;
    private Locale defaultLocale;
    private boolean manageable;
    private boolean redeployEnabled;
    private int watchInterval;
    private List watches;
    private List touches;

    public SystemSettings()
    {
        manageable = true;
        redeployEnabled = false;
        resourceLoader = new PropertyStringResourceLoader();
        touches = new ArrayList();
        watches = new ArrayList();
        watchInterval = 20;
    }

    public void setDefaultLocale(Locale locale)
    {
        defaultLocale = locale;
        resourceLoader.setDefaultLocale(defaultLocale);
    }

    public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    public boolean isManageable()
    {
        return manageable;
    }

    public void setManageable(String manageable)
    {
        manageable = manageable.toLowerCase();
        if (manageable.startsWith("f"))
            this.manageable = false;
    }

    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public void setRedeployEnabled(String enabled)
    {
        enabled = enabled.toLowerCase();
        if (enabled.startsWith("t"))
            this.redeployEnabled = true;
    }

    public boolean getRedeployEnabled()
    {
        return redeployEnabled;
    }

    public void setWatchInterval(String interval)
    {
        this.watchInterval = Integer.parseInt(interval);
    }

    public int getWatchInterval()
    {
        return watchInterval;
    }

/* TODO UCdetector: Remove unused code: 
    public void addWatchFile(String watch)
    {
        this.watches.add(watch);
    }
*/

    public List getWatchFiles()
    {
        return watches;
    }

/* TODO UCdetector: Remove unused code: 
    public void addTouchFile(String touch)
    {
        this.touches.add(touch);
    }
*/

    public List getTouchFiles()
    {
        return touches;
    }

    public void setPaths(ServletContext context)
    {
        if (redeployEnabled)
        {
            List resolvedWatches = new ArrayList();
            for (int i = 0; i < watches.size(); i++)
            {
                String path = (String)watches.get(i);
                String resolvedPath = null;
                if (path.startsWith("{context.root}") || path.startsWith("{context-root}"))
                {
                    path = path.substring(14);
                    resolvedPath = context.getRealPath(path);

                    if (resolvedPath != null)
                    {
                        try
                        {
                            resolvedWatches.add(new WatchedObject(resolvedPath));
                        }
                        catch (FileNotFoundException fnfe)
                        {
                            Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                            if (logger != null)
                            {
                                logger.warn("The watch-file, " + path + ", could not be found and will be ignored.");
                            }
                        }
                    }
                    else
                    {
                        Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                        logger.warn("The watch-file, " + path + ", could not be resolved to a path and will be ignored.");
                    }
                }
                else
                {
                    try
                    {
                        resolvedWatches.add(new WatchedObject(path));
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                        if (logger != null)
                        {
                            logger.warn("The watch-file, " + path + ", could not be found and will be ignored.");
                        }
                    }
                }
            }
            watches = resolvedWatches;

            List resolvedTouches = new ArrayList();
            for (int i = 0; i < touches.size(); i++)
            {
                String path = (String)touches.get(i);
                String resolvedPath = null;
                if (path.startsWith("{context.root}") || path.startsWith("{context-root}"))
                {
                    path = path.substring(14);
                    resolvedPath = context.getRealPath(path);

                    if (resolvedPath != null)
                    {
                        File file = new File(resolvedPath);
                        if (!file.exists() || (!file.isFile() && !file.isDirectory()) || (!file.isAbsolute()))
                        {
                            Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                            logger.warn("The touch-file, " + path + ", could not be found and will be ignored.");
                        }
                        else
                        {
                            resolvedTouches.add(resolvedPath);
                        }
                    }
                    else
                    {
                        Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                        logger.warn("The touch-file, " + path + ", could not be resolved to a path and will be ignored.");
                    }
                }
                else
                {
                    try
                    {
                        resolvedTouches.add(new WatchedObject(path));
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Logger logger = Log.getLogger(ConfigurationManager.LOG_CATEGORY);
                        if (logger != null)
                        {
                            logger.warn("The touch-file, " + path + ", could not be found and will be ignored.");
                        }
                    }
                }
            }
            touches = resolvedTouches;
        }
    }

    /**
     * Clean up static member variables.
     */
    public void clear()
    {
        resourceLoader = null;
        defaultLocale = null;
        watches = null;
        touches = null;
    }

}
