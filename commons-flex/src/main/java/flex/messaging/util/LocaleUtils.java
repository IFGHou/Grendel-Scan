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

/**
 * @exclude
 */
public class LocaleUtils
{
    /**
     * Builds a <code>Locale</code> instance from the passed string. If the string is <code>null</code> this method will return the default locale for the JVM.
     * 
     * @param locale
     *            The locale as a string.
     * @return The Locale instance built from the passed string.
     */
    public static Locale buildLocale(String locale)
    {
        if (locale == null)
        {
            return Locale.getDefault();
        }
        else
        {
            int index = locale.indexOf('_');
            if (index == -1)
            {
                return new Locale(locale);
            }
            String language = locale.substring(0, index);
            String rest = locale.substring(index + 1);
            index = rest.indexOf('_');
            if (index == -1)
            {
                return new Locale(language, rest);
            }
            String country = rest.substring(0, index);
            rest = rest.substring(index + 1);
            return new Locale(language, country, rest);
        }
    }
}
