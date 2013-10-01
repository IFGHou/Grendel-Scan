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

import java.util.Locale;
import java.util.Map;

/**
 * The root interface for classes that provide access to localized resources.
 * 
 * @author Seth Hodgson
 * @exclude
 */
public interface ResourceLoader
{
    /**
     * Initializes the <code>ResourceLoader</code> using the specified properties.
     * 
     * @param properties
     *            The initialization properties.
     */
    void init(Map properties);

    /**
     * Sets the default locale to be used when locating resources. The string will be converted into a Locale.
     * 
     * @param locale
     *            The default locale to be used.
     */
    void setDefaultLocale(String locale);

    /**
     * Sets the default locale to be used when locating resources.
     * 
     * @param locale
     *            The default locale to be used.
     */
    void setDefaultLocale(Locale locale);

    /**
     * The default locale to be used when locating resources.
     * 
     * @return The default locale.
     */
    Locale getDefaultLocale();

    /**
     * Gets a string for the given key.
     * 
     * @param key
     *            The key for the target string.
     * @return The string for the given key.
     */
    String getString(String key);

    /**
     * Gets a parameterized string for the given key and substitutes the parameters using the passed array of arguments.
     * 
     * @param key
     *            The key for the target string.
     * @param arguments
     *            The arguments to substitute into the parameterized string.
     * @return The substituted string for the given key.
     * @exception IllegalArgumentException
     *                If the parameterized string is invalid, or if an argument in the <code>arguments</code> array is not of the type expected by the format element(s) that use it.
     */
    String getString(String key, Object[] arguments);

    /**
     * Gets a string for the given key and locale.
     * 
     * @param key
     *            The key for the target string.
     * @param locale
     *            The target locale for the string.
     * @return The localized string for the given key.
     */
    String getString(String key, Locale locale);

    /**
     * Gets a parameterized string for the given key and locale and substitutes the parameters using the passed array of arguments.
     * 
     * @param key
     *            The key for the target string.
     * @param locale
     *            The target locale for the string.
     * @param arguments
     *            The arguments to substitute into the parameterized string.
     * @return The substituted localized string for the given key.
     * @exception IllegalArgumentException
     *                If the parameterized string is invalid, or if an argument in the <code>arguments</code> array is not of the type expected by the format element(s) that use it.
     */
    String getString(String key, Locale locale, Object[] arguments);

}
