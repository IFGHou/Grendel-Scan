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
package flex.messaging.config;

import java.util.List;

/**
 * Holds any child element of the properties section of the services configuration.
 * <p>
 * If a property is a simple element with a text value then it is stored as a String using the element name as the property name. If the same element appears again then the element is converted to a
 * List of values and further occurences are simply added to the List.
 * </p>
 * <p>
 * If a property element has child elements the children are recursively processed and added as a Map.
 * </p>
 * 
 * @author Peter Farland
 * @exclude
 */
public abstract class PropertiesSettings
{
    protected final ConfigMap properties;

    public PropertiesSettings()
    {
        properties = new ConfigMap();
    }

    /*
     * TODO UCdetector: Remove unused code: public final void addProperties(ConfigMap p) { properties.addProperties(p); }
     */

    public ConfigMap getProperties()
    {
        return properties;
    }

    public final String getProperty(String name)
    {
        return getPropertyAsString(name, null);
    }

    /*
     * TODO UCdetector: Remove unused code: public final void addProperty(String name, String value) { properties.addProperty(name, value); }
     */

    /*
     * TODO UCdetector: Remove unused code: public final void addProperty(String name, ConfigMap value) { properties.addProperty(name, value); }
     */

    /*
     * TODO UCdetector: Remove unused code: public final ConfigMap getPropertyAsMap(String name, ConfigMap defaultValue) { return properties.getPropertyAsMap(name, defaultValue); }
     */

    public final String getPropertyAsString(String name, String defaultValue)
    {
        return properties.getPropertyAsString(name, defaultValue);
    }

    public final List getPropertyAsList(String name, List defaultValue)
    {
        return properties.getPropertyAsList(name, defaultValue);
    }

    public final int getPropertyAsInt(String name, int defaultValue)
    {
        return properties.getPropertyAsInt(name, defaultValue);
    }

    public final boolean getPropertyAsBoolean(String name, boolean defaultValue)
    {
        return properties.getPropertyAsBoolean(name, defaultValue);
    }

    public final long getPropertyAsLong(String name, long defaultValue)
    {
        return properties.getPropertyAsLong(name, defaultValue);
    }
}
