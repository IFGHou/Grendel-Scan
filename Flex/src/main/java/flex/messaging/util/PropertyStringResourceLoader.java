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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.log.Logger;

/**
 * Implementation of <code>ResourceLoader</code> that loads string resources
 * from property files.
 * <p>
 * This class uses <code>MessageFormat</code> to perform substitutions
 * within parameterized strings.
 * </p>
 *
 * @author Seth Hodgson
 * @see MessageFormat
 * @exclude
 */
public class PropertyStringResourceLoader implements ResourceLoader
{
    // The property file bundle that contains localized error strings for BlazeDS.
    public static final String PROPERTY_BUNDLE = "flex/messaging/errors";

    // The property file bundle that contains localized error strings for BlazeDS 
    // code specific to vendors (eg. LoginCommands for specific application serves)
    public static final String VENDORS_BUNDLE = "flex/messaging/vendors";
    
    // The property file bundle that contains localized error strings for LCDS.
    public static final String LCDS_PROPERTY_BUNDLE = "flex/data/errors";

    // The category to write log entries under.
    private static final String LOG_CATEGORY = LogCategories.RESOURCE;

    // The property bundle names to use in string lookups.
    private String[] propertyBundles;

    // The default FDS locale.
    private Locale defaultLocale;

    // The set of locales that have strings loaded.
    private Set loadedLocales = new TreeSet();

    // A map of all loaded strings.
    private Map strings = new HashMap();

    // The logger for this instance.
    private Logger logger;

    /**
     * Constructs a <code>PropertyStringResourceLoader</code> using the default
     * property bundles specified by the <code>PROPERTY_BUNDLE</code> and
     * <code>LCDS_PROPERTY_BUNDLE</code> fields.
     */
    public PropertyStringResourceLoader()
    {
        this(new String[] {PROPERTY_BUNDLE, LCDS_PROPERTY_BUNDLE});
    }

    /**
     * Constructs a <code>PropertyStringResourceLoader</code> that will use the
     * specified property bundle to use for string lookups.
     *
     * @param propertyBundle The property bundles to use for lookups.
     */
    public PropertyStringResourceLoader(String propertyBundle)
    {
        this(new String[] {propertyBundle});
    }

    /**
     * Constructs a <code>PropertyStringResourceLoader</code> that will use the
     * specified property bundles to use for string lookups.
     *
     * @param propertyBundles The list of the property bundles to use for lookups.
     */
    public PropertyStringResourceLoader(String[] propertyBundles)
    {
        this.propertyBundles = propertyBundles;
        logger = Log.getLogger(LOG_CATEGORY);
    }

    // Implements flex.messaging.util.ResourceLoader.init; inherits javadoc specification.
    @Override public void init(Map properties)
    {}

    // Implements flex.messaging.util.ResourceLoader.getString; inherits javadoc specification.
    @Override public String getString(String key)
    {
        return getString(key, null, null);
    }

    // Implements flex.messaging.util.ResourceLoader.getString; inherits javadoc specification.
    @Override public String getString(String key, Object[] arguments)
    {
        return getString(key, null, arguments);
    }

    // Implements flex.messaging.util.ResourceLoader.getString; inherits javadoc specification.
    @Override public String getString(String key, Locale locale)
    {
        return getString(key, locale, null);
    }

    // Implements flex.messaging.util.ResourceLoader.getString; inherits javadoc specification.
    @Override public String getString(String key, Locale locale, Object[] arguments)
    {
        synchronized(strings)
        {
            if (defaultLocale == null)
            {
                defaultLocale = getDefaultLocale();
            }
        }
        String value = null;
        String stringKey = null;
        String localeKey = (locale != null) ?
                           generateLocaleKey(locale) :
                           generateLocaleKey(defaultLocale);
        String originalStringKey = generateStringKey(key, localeKey);
        int trimIndex = 0;

        /*
         * Attempt to get a string for the target locale - fail back to less specific
         * versions of the locale.
         */
        while (true)
        {
            loadStrings(localeKey);
            stringKey = generateStringKey(key, localeKey);
            synchronized(strings)
            {
                value = (String) strings.get(stringKey);
                if (value != null)
                {
                    if (!stringKey.equals(originalStringKey))
                    {
                        strings.put(originalStringKey, value);
                    }
                    return substituteArguments(value, arguments);
                }
            }
            trimIndex = localeKey.lastIndexOf("_");
            if (trimIndex != -1)
            {
                localeKey = localeKey.substring(0, trimIndex);
            }
            else
            {
                break;
            }
        }

        /*
         * Attempt to get the string in our default locale if it is
         * different than the requested locale.
         */
        if ((locale != null) && (!locale.equals(defaultLocale)))
        {
            localeKey = generateLocaleKey(defaultLocale);
            stringKey = generateStringKey(key, localeKey);
            synchronized(strings)
            {
                value = (String) strings.get(stringKey);
                if (value != null)
                {
                    strings.put(originalStringKey, value);
                    return substituteArguments(value, arguments);
                }
            }
        }

        // As a last resort, try to get a non-locale-specific string.
        loadStrings("");
        stringKey = generateStringKey(key, "");
        synchronized(strings)
        {
            value = (String) strings.get(stringKey);
            if (value != null)
            {
                strings.put(originalStringKey, value);
                return substituteArguments(value, arguments);
            }
        }

        // No string is available. Return a formatted missing string value.
        return ("???" + key + "???");
    }

    /**
     * Sets the default locale to be used when locating resources. The
     * string will be converted into a Locale.
     *
     * @param locale The default locale to be used.
     */
    @Override public void setDefaultLocale(String locale)
    {
        defaultLocale = LocaleUtils.buildLocale(locale);
    }

    /**
     * Sets the default locale to be used when locating resources.
     *
     * @param locale The default locale to be used.
     */
    @Override public void setDefaultLocale(Locale locale)
    {
        defaultLocale = locale;
    }

    /**
     * The default locale to be used when locating resources.
     */
    @Override public Locale getDefaultLocale()
    {
        if (defaultLocale == null)
            defaultLocale = Locale.getDefault();

        return defaultLocale;
    }

    /**
     * Loads localized strings for the specified locale from a property file.
     *
     * @param localeKey The locale to load strings for.
     */
    private synchronized void loadStrings(String localeKey)
    {
        if (loadedLocales.contains(localeKey))
        {
            return;
        }

        if (propertyBundles != null)
        {
            for (int i = 0; i < propertyBundles.length; i++)
            {
                String propertyBundle = propertyBundles[i];
                loadProperties(localeKey, propertyBundle);
            }
        }
    }

    // Helper method for loadStrings.
    private void loadProperties(String localeKey, String propertyBundle)
    {
        // Build the path to the target property file.
        String filename = propertyBundle;
        if (localeKey.length() > 0)
        {
            filename += "_" + localeKey;
        }
        filename += ".properties";
        // Load the property file.
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream stream = loader.getResourceAsStream(filename);
        Properties props = new Properties();
        if (stream != null)
        {
            try
            {
                props.load(stream);
            }
            catch (IOException ioe)
            {
                logger.warn("There was a problem reading the string resource property file '" + filename + "' stream.", ioe);
            }
            catch (IllegalArgumentException iae)
            {
                logger.warn("The string resource property file '" + filename + "' contains a malformed Unicode escape sequence.", iae);
            }
            finally
            {
                try
                {
                    stream.close();
                }
                catch (IOException ioe)
                {
                    logger.warn("The string resource property file '" + filename + "' stream failed to close.", ioe);
                }
            }
        }
        else
        {
            logger.warn("The class loader could not locate the string resource property file '" + filename + "'. This may not be an issue if a property file is available for a less specific locale or the default locale.");
        }
        // Move strings into string cache.
        if (props.size() > 0)
        {
            synchronized(strings)
            {
                Iterator iter = props.keySet().iterator();
                while (iter.hasNext())
                {
                    String key = (String) iter.next();
                    strings.put(generateStringKey(key, localeKey), props.getProperty(key));
                }
            }
        }
    }

    /**
     * Generates a locale cache key.
     *
     * @param locale The locale to generate a cache key for.
     * @return The generated cache key.
     */
    private String generateLocaleKey(Locale locale)
    {
        return (locale == null) ? "" : locale.toString();
    }

    /**
     * Generates a cache key for a string resource.
     *
     * @param key The string to generate a cache key for.
     * @param locale The locale to retrieve the string for.
     * @return The generated cache key for the string resource.
     */
    private String generateStringKey(String key, String locale)
    {
        return (key + "-" + locale);
    }

    /**
     * Substitutes the specified arguments into a parameterized string.
     *
     * @param parameterized The string containing parameter tokens for substitution.
     * @param arguments The arguments to substitute into the parameterized string.
     * @return The resulting substituted string.
     */
    private String substituteArguments(String parameterized, Object[] arguments)
    {
        return MessageFormat.format(parameterized, arguments).trim();
    }

}