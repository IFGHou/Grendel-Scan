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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import flex.messaging.MessageException;
import flex.messaging.log.Log;
import flex.messaging.log.Logger;

/**
 * Proxies serialization of a Map and considers all keys as String based property names. Additionally, bean properties from the instance are also included and override any Map entries with the same
 * key name.
 * 
 * @author Peter Farland
 */
public class MapProxy extends BeanProxy
{
    static final long serialVersionUID = 7857999941099335210L;

    private static final int NULL_KEY_ERROR = 10026;

    /**
     * Constructor
     */
    public MapProxy()
    {
        super();
        // dynamic = true;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Construct with a default instance type.
    // * @param defaultInstance defines the alias if provided
    // */
    // public MapProxy(Object defaultInstance)
    // {
    // super(defaultInstance);
    // //dynamic = true;
    // }

    /** {@inheritDoc} */
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
            if (excludes == null) // For compatibility with older implementations
                excludes = descriptor.getExcludes();
        }

        // Add all Map keys as properties
        if (instance instanceof Map)
        {
            Map map = (Map) instance;

            if (map.size() > 0)
            {
                propertyNames = new ArrayList(map.size());
                SerializationContext context = getSerializationContext();

                Iterator it = map.keySet().iterator();
                while (it.hasNext())
                {
                    Object key = it.next();
                    if (key != null)
                    {
                        if (excludes != null && excludes.contains(key))
                            continue;

                        propertyNames.add(key.toString());
                    }
                    else
                    {
                        // Log null key errors
                        if (Log.isWarn() && context.logPropertyErrors)
                        {
                            Logger log = Log.getLogger(LOG_CATEGORY);
                            log.warn("Cannot send a null Map key for type {0}.", new Object[] { map.getClass().getName() });
                        }

                        if (!context.ignorePropertyErrors)
                        {
                            // Cannot send a null Map key for type {0}.
                            MessageException ex = new MessageException();
                            ex.setMessage(NULL_KEY_ERROR, new Object[] { map.getClass().getName() });
                            throw ex;
                        }
                    }
                }
            }
        }

        // Then, check for bean properties
        List beanProperties = super.getPropertyNames(instance);
        if (beanProperties != null)
        {
            if (propertyNames == null)
            {
                propertyNames = beanProperties;
            }
            else
            {
                propertyNames.addAll(beanProperties);
            }
        }

        return propertyNames;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValue(Object instance, String propertyName)
    {
        if (instance == null || propertyName == null)
            return null;

        Object value = null;

        // First, check for bean property
        BeanProperty bp = getBeanProperty(instance, propertyName);
        if (bp != null)
        {
            value = super.getBeanValue(instance, bp);
        }

        // Then check for Map entry
        if (value == null && instance instanceof Map)
        {
            Map map = (Map) instance;
            value = map.get(propertyName);
        }

        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(Object instance, String propertyName, Object value)
    {
        if (instance == null || propertyName == null)
            return;

        Map props = getBeanProperties(instance);
        if (props.containsKey(propertyName))
        {
            super.setValue(instance, propertyName, value);
        }
        else if (instance instanceof Map)
        {
            ((Map) instance).put(propertyName, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object clone()
    {
        return super.clone();
    }

    /** {@inheritDoc} */
    @Override
    protected boolean ignorePropertyErrors(SerializationContext context)
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean logPropertyErrors(SerializationContext context)
    {
        return false;
    }

    /**
     * Return the classname of the instance, including ASObject types. If the instance is a Map and is in the java.util package, we return null.
     * 
     * @param instance
     *            the object to find the class name of
     * @return the class name of the object.
     */
    @Override
    protected String getClassName(Object instance)
    {
        if (instance != null && instance instanceof Map && instance.getClass().getName().startsWith("java.util."))
        {
            return null;
        }
        return super.getClassName(instance);
    }
}
