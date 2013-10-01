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
package flex.messaging.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import flex.messaging.LocalizedException;
import flex.messaging.util.PrettyPrinter;

/**
 * @exclude
 */
public class Log
{

    /** @exclude **/
    public static final String INVALID_CHARS = "[]~$^&\\/(){}<>+=`!#%?,:;\'\"@";

    // Errors
    private static final int INVALID_TARGET = 10013;
    private static final int INVALID_CATEGORY = 10014;
    private static final int INVALID_CATEGORY_CHARS = 10015;

    private static Log log;
    private static PrettyPrinter prettyPrinter;
    private static String prettyPrinterClass = "BasicPrettyPrinter";

    private static final HashSet excludedProperties = new HashSet();
    /** @exclude **/
    public static final String VALUE_SUPRESSED = "** [Value Suppressed] **";

    private volatile short targetLevel;
    private final Map loggers;
    private final List targets;
    private final Map targetMap;
    private static final Object staticLock = new Object();

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Private constructor.
     */
    private Log()
    {
        targetLevel = LogEvent.NONE;
        loggers = new HashMap();
        targets = new ArrayList();
        targetMap = new LinkedHashMap();
    }

    /**
     * Creates the log on first access, returns already created log on subsequent calls.
     * 
     * @return log.
     */
    public static Log createLog()
    {
        synchronized (staticLock)
        {
            if (log == null)
                log = new Log();

            return log;
        }
    }

    // --------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    // --------------------------------------------------------------------------

    // TODO UCdetector: Remove unused code:
    // /**
    // * Initializes Log with id and properties.
    // *
    // * @param id Id for the Log which is ignored, though is used by the ManageableComponent superclass
    // * @param properties ConfigMap of properties for the Log.
    // */
    // public static synchronized void initialize(String id, ConfigMap properties)
    // {
    // String value = properties.getPropertyAsString("pretty-printer", null);
    //
    // if (value != null)
    // {
    // prettyPrinterClass = value;
    // }
    //
    // // Create a HashSet with the properties that we want to exclude from the
    // // list of properties given by 'getPropertiesAsList'
    // ConfigMap excludeMap = properties.getPropertyAsMap("exclude-properties", null);
    //
    // if (excludeMap != null)
    // {
    // if (excludeMap.getPropertyAsList("property", null) != null)
    // excludedProperties.addAll(excludeMap.getPropertyAsList("property", null));
    // }
    // }

    // --------------------------------------------------------------------------
    //
    // Public Getters and Setters for Log properties
    //
    // --------------------------------------------------------------------------

    // TODO UCdetector: Remove unused code:
    // /**
    // * Indicates whether a fatal level log event will be processed by a log target.
    // */
    // public static boolean isFatal()
    // {
    // return log == null ? false : log.targetLevel <= LogEvent.FATAL;
    // }

    /**
     * Indicates whether an error level log event will be processed by a log target.
     */
    public static boolean isError()
    {
        return log == null ? false : log.targetLevel <= LogEvent.ERROR;
    }

    /**
     * Indicates whether a warn level log event will be processed by a log target.
     */
    public static boolean isWarn()
    {
        return log == null ? false : log.targetLevel <= LogEvent.WARN;
    }

    /**
     * Indicates whether an info level log event will be processed by a log target.
     */
    public static boolean isInfo()
    {
        return log == null ? false : log.targetLevel <= LogEvent.INFO;
    }

    /**
     * Indicates whether a debug level log event will be processed by a log target.
     */
    public static boolean isDebug()
    {
        return log == null ? false : log.targetLevel <= LogEvent.DEBUG;
    }

    /**
     * Indicates whether a log property should be excluded.
     */
    public static boolean isExcludedProperty(String property)
    {
        return !excludedProperties.isEmpty() && excludedProperties.contains(property);
    }

    /**
     * Given a category, returns the logger associated with the category.
     * 
     * @param category
     *            Categogry for the logger.
     * @return Logger associated with the category.
     */
    public static Logger getLogger(String category)
    {
        if (log != null)
        {
            return getLogger(log, category);
        }
        else
        {
            // Return a dummy logger?
            return new Logger(category);
        }
    }

    /**
     * @exclude
     */
    public static Logger getLogger(Log log, String category)
    {
        checkCategory(category);

        synchronized (staticLock)
        {
            Logger result = (Logger) log.loggers.get(category);
            if (result == null)
            {
                result = new Logger(category);

                // check to see if there are any targets for this logger.
                for (Iterator iter = log.targets.iterator(); iter.hasNext();)
                {
                    Target target = (Target) iter.next();
                    if (categoryMatchInFilterList(category, target.getFilters()))
                        target.addLogger(result);
                }

                log.loggers.put(category, result);
            }
            return result;
        }
    }

    /**
     * Returns an unmodifiable snapshot of the targets registered with this Log when the method is invoked.
     */
    public static List getTargets()
    {
        if (log != null)
        {
            List currentTargets;
            // Snapshot the current target list (shallow copy) and return it.
            synchronized (staticLock)
            {
                currentTargets = Collections.unmodifiableList(new ArrayList(log.targets));
            }
            return currentTargets;
        }
        return null;
    }

    /**
     * @return the Log's map of targets keyed on their human-readable ids (e.g. ConsoleTarget0, ConsoleTarget1, etc.)
     */
    public static Map getTargetMap()
    {
        if (log != null)
        {
            Map currentTargets;
            synchronized (staticLock)
            {
                currentTargets = new LinkedHashMap(log.targetMap);
            }
            return currentTargets;
        }
        return null;
    }

    /**
     * Returns the target associated with the unique ID searchId. Returns null if no such target exists.
     */
    public static Target getTarget(String searchId)
    {
        if (log != null)
        {
            synchronized (staticLock)
            {
                return (Target) log.targetMap.get(searchId);
            }
        }

        return null;
    }

    /**
     * @return the categories for all of the loggers
     */
    public String[] getLoggers()
    {
        String[] currentCategories;
        if (log != null)
        {

            synchronized (staticLock)
            {
                Object[] currentCategoryObjects = loggers.keySet().toArray();
                currentCategories = new String[currentCategoryObjects.length];
                for (int i = 0; i < currentCategoryObjects.length; i++)
                {
                    currentCategories[i] = (String) (currentCategoryObjects[i]);
                }
            }
        }
        else
        {
            currentCategories = new String[0];
        }

        return currentCategories;
    }

    /**
     * Adds a target to the log.
     * 
     * @param target
     *            Target to be added.
     */
    public static void addTarget(Target target)
    {
        if (log != null)
        {
            if (target != null)
            {
                synchronized (staticLock)
                {
                    List filters = target.getFilters();

                    // need to find what filters this target matches and set the specified
                    // target as a listener for that logger.
                    Iterator it = log.loggers.keySet().iterator();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();

                        if (categoryMatchInFilterList(key, filters))
                            target.addLogger((Logger) log.loggers.get(key));
                    }
                    // if we found a match all is good, otherwise we need to
                    // put the target in a waiting queue in the event that a logger
                    // is created that this target cares about.
                    if (!log.targets.contains(target))
                        log.targets.add(target);

                    if (!log.targetMap.containsValue(target))
                    {
                        String name = target.getClass().getName();

                        if (name.indexOf(".") > -1)
                        {
                            String[] classes = name.split("\\.");
                            name = classes[classes.length - 1];
                        }

                        log.targetMap.put(new String(name + log.targetMap.size()), target);
                    }

                    // update our global target log level if this target is more verbose.
                    short targetLevel = target.getLevel();
                    if (log.targetLevel == LogEvent.NONE)
                        log.targetLevel = targetLevel;
                    else if (targetLevel < log.targetLevel)
                    {
                        log.targetLevel = targetLevel;
                    }
                }
            }
            else
            {
                // Invalid target specified. Target must not be null.
                LocalizedException ex = new LocalizedException();
                ex.setMessage(INVALID_TARGET);
                throw ex;
            }
        }
    }

    /**
     * Removes a target from the log.
     * 
     * @param target
     *            The target to be removed.
     */
    public static void removeTarget(Target target)
    {
        if (log != null)
        {
            if (target != null)
            {
                synchronized (staticLock)
                {
                    // Remove the target from any associated loggers.
                    List filters = target.getFilters();
                    Iterator it = log.loggers.keySet().iterator();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();

                        if (categoryMatchInFilterList(key, filters))
                            target.removeLogger((Logger) log.loggers.get(key));
                    }
                    // Remove the target from the Log set.
                    log.targets.remove(target);
                    resetTargetLevel();
                }
            }
            else
            {
                // Invalid target specified. Target must not be null.
                LocalizedException ex = new LocalizedException();
                ex.setMessage(INVALID_TARGET);
                throw ex;
            }
        }
    }

    // --------------------------------------------------------------------------
    //
    // Other Public APIs
    //
    // --------------------------------------------------------------------------

    // TODO UCdetector: Remove unused code:
    // /**
    // * This method removes all of the current loggers and targets from the cache.
    // * and resets target level.
    // */
    // public static synchronized void reset()
    // {
    // flush();
    // }

    /**
     * @exclude
     */
    public static void flush()
    {
        if (log != null)
        {
            log.loggers.clear();
            log.targets.clear();
            log.targetLevel = LogEvent.NONE;
        }
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // */
    // public static short readLevel(String l)
    // {
    // short lvl = LogEvent.ERROR;
    // if ((l != null) && (l.length() > 0))
    // {
    // l = l.trim().toLowerCase();
    // char c = l.charAt(0);
    // switch (c)
    // {
    // case 'n':
    // lvl = LogEvent.NONE;
    // break;
    // case 'e':
    // lvl = LogEvent.ERROR;
    // break;
    // case 'w':
    // lvl = LogEvent.WARN;
    // break;
    // case 'i':
    // lvl = LogEvent.INFO;
    // break;
    // case 'd':
    // lvl = LogEvent.DEBUG;
    // break;
    // case 'a':
    // lvl = LogEvent.ALL;
    // break;
    // default:
    // lvl = LogEvent.ERROR;
    // }
    // }
    //
    // return lvl;
    // }

    /**
     * @exclude This method checks the specified string value for illegal characters.
     * 
     * @param value
     *            to check for illegal characters. The following characters are not valid: []~$^&\/(){}<>+=`!#%?,:;'"&#64;
     * @return <code>true</code> if there are any illegal characters found, <code>false</code> otherwise
     */
    public static boolean hasIllegalCharacters(String value)
    {
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            if (INVALID_CHARS.indexOf(c) != -1)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @exclude Returns the PrettyPrinter used by the Log.
     */
    public static PrettyPrinter getPrettyPrinter()
    {
        if (prettyPrinter == null || !prettyPrinter.getClass().getName().equals(prettyPrinterClass))
        {
            try
            {
                Class c = Class.forName(prettyPrinterClass);
                prettyPrinter = (PrettyPrinter) c.newInstance();
            }
            catch (Throwable t)
            {
            }
        }
        return (PrettyPrinter) prettyPrinter.copy();
    }

    /**
     * @exclude Returns the current target level for the Log.
     */
    public static short getTargetLevel()
    {
        return log == null ? LogEvent.NONE : log.targetLevel;
    }

    /**
     * @exclude Sets the pretty printer class name used by the log.
     * 
     * @param value
     *            Name of the pretty printer class.
     */
    public static void setPrettyPrinterClass(String value)
    {
        prettyPrinterClass = value;
    }

    // --------------------------------------------------------------------------
    //
    // Protected/private methods.
    //
    // --------------------------------------------------------------------------

    /* package */static void resetTargetLevel()
    {
        if (log != null)
        {
            synchronized (staticLock)
            {
                short maxTargetLevel = LogEvent.NONE;
                for (Iterator iter = log.targets.iterator(); iter.hasNext();)
                {
                    short targetLevel = ((Target) iter.next()).getLevel();
                    if (maxTargetLevel == LogEvent.NONE || targetLevel < maxTargetLevel)
                    {
                        maxTargetLevel = targetLevel;
                    }
                }
                log.targetLevel = maxTargetLevel;
            }
        }
    }

    /* package */static void processTargetFilterAdd(Target target, String filter)
    {
        if (log != null)
        {
            synchronized (staticLock)
            {
                List filters = new ArrayList();
                filters.add(filter);

                // Find the loggers this target matches and add the
                // target as a listener for log events from these loggers.
                Iterator it = log.loggers.keySet().iterator();
                while (it.hasNext())
                {
                    String key = (String) it.next();

                    if (categoryMatchInFilterList(key, filters))
                        target.addLogger((Logger) log.loggers.get(key));
                }
            }
        }
    }

    /* package */static void processTargetFilterRemove(Target target, String filter)
    {
        if (log != null)
        {
            synchronized (staticLock)
            {
                // Remove the target from any matching loggers.
                List filters = new ArrayList();
                filters.add(filter);
                Iterator it = log.loggers.keySet().iterator();
                while (it.hasNext())
                {
                    String key = (String) it.next();

                    if (categoryMatchInFilterList(key, filters))
                        target.removeLogger((Logger) log.loggers.get(key));
                }
            }
        }
    }

    /**
     * This method checks that the specified category matches any of the filter expressions provided in the filters array.
     * 
     * @param category
     *            to match against
     * @param filters
     *            - list of strings to check category against.
     * @return <code>true</code> if the specified category matches any of the filter expressions found in the filters list, <code>false</code> otherwise.
     */
    private static boolean categoryMatchInFilterList(String category, List filters)
    {
        if (filters == null)
            return false;

        for (int i = 0; i < filters.size(); i++)
        {
            String filter = (String) filters.get(i);

            // match category to filter based on the presense of a wildcard
            if (checkFilterToCategory(filter, category))
                return true;
        }
        return false;
    }

    /**
     * @param filter
     *            The filter string to check against a specific category
     * @param category
     *            The category which the filter could match
     * @return whether the filter matches a specific category
     */
    public static boolean checkFilterToCategory(String filter, String category)
    {
        int index = -1;
        index = filter.indexOf("*");

        if (index == 0) // match all
        {
            return true;
        }
        else if (index < 0) // match full category to filter
        {
            if (category.equals(filter))
            {
                return true;
            }
        }
        else
        // match partial category to filter
        {
            if ((category.length() >= index) && category.substring(0, index).equals(filter.substring(0, index)))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * This method will ensure that a valid category string has been specified. If the category is not valid an exception will be thrown.
     * 
     * Categories can not contain any blanks or any of the following characters: []`*~,!#$%^&()]{}+=\|'";?><./&#64; or be less than 1 character in length.
     */
    private static void checkCategory(String category)
    {
        if (category == null || category.length() == 0)
        {
            // Categories must be at least one character in length.
            LocalizedException ex = new LocalizedException();
            ex.setMessage(INVALID_CATEGORY);
            throw ex;
        }

        if (hasIllegalCharacters(category) || (category.indexOf("*") != -1))
        {
            // Categories can not contain any of the following characters: 'INVALID_CHARS'
            LocalizedException ex = new LocalizedException();
            ex.setMessage(INVALID_CATEGORY_CHARS, new Object[] { INVALID_CHARS });
            throw ex;
        }
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Clean up static member variables.
    // */
    // public static void clear()
    // {
    // log = null;
    // prettyPrinter = null;
    // }

}
