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
package flex.messaging.io;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * Proxies serialization of a Dictionary and considers all keys as String based property names. Additionally, bean properties from the instance are also included and override any Dictionary entries
 * with the same name.
 * 
 * @author Peter Farland
 */
public class DictionaryProxy extends BeanProxy
{
    static final long serialVersionUID = 1501461889185692712L;

    public DictionaryProxy()
    {
        super();
        // dynamic = true;
    }

    /*
     * TODO UCdetector: Remove unused code: public DictionaryProxy(Dictionary defaultInstance) { super(defaultInstance); }
     */

    @Override
    public List getPropertyNames(Object instance)
    {
        if (instance == null)
            return null;

        List propertyNames = null;
        List excludes = null;

        if (descriptor != null)
        {
            excludes = descriptor.getExcludesForInstance(instance);
            if (excludes == null)
                excludes = descriptor.getExcludes();
        }

        // Add all Dictionary keys as properties
        if (instance instanceof Dictionary)
        {
            Dictionary dictionary = (Dictionary) instance;

            propertyNames = new ArrayList(dictionary.size());

            Enumeration keys = dictionary.keys();
            while (keys.hasMoreElements())
            {
                Object key = keys.nextElement();
                if (key != null)
                {
                    if (excludes != null && excludes.contains(key))
                        continue;

                    propertyNames.add(key.toString());
                }
            }
        }

        // Then, check for bean properties
        List beanProperties = super.getPropertyNames();
        if (propertyNames == null)
        {
            propertyNames = beanProperties;
        }
        else
        {
            propertyNames.addAll(beanProperties);
        }

        return propertyNames;
    }

    @Override
    public Object getValue(Object instance, String propertyName)
    {
        if (instance == null || propertyName == null)
            return null;

        // First, check for bean property
        Object value = super.getValue(instance, propertyName);

        // Then check for Dictionary entry
        if (value == null && instance instanceof Dictionary)
        {
            Dictionary dictionary = (Dictionary) instance;
            value = dictionary.get(propertyName);
        }

        return value;
    }

    /** {@inheritDoc} */
    @Override
    public Object clone()
    {
        return super.clone();
    }
}
