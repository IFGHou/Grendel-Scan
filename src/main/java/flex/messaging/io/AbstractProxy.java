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
package flex.messaging.io;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.List;

import flex.messaging.MessageException;
import flex.messaging.io.amf.ASObject;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.log.Logger;
import flex.messaging.util.ClassUtil;

/**
 * Simple abstract implementation of PropertyProxy's common properties. Specific
 * sub-classes need to provide the full implementation focusing on the retrieval 
 * of the instance traits or "list of properties" and a specific value for
 * a given property name.
 * 
 * @see flex.messaging.io.PropertyProxy
 * @exclude
 */
public abstract class AbstractProxy implements PropertyProxy, Serializable
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected Object defaultInstance;
    protected String alias;
    protected boolean dynamic;
    protected boolean externalizable;
    protected boolean includeReadOnly;
    protected SerializationDescriptor descriptor;
    protected SerializationContext context;
    
    protected static final String LOG_CATEGORY = LogCategories.ENDPOINT_TYPE;
    private static final int CONVERSION_ERROR = 10006;

    protected AbstractProxy(Object defaultInstance)
    {
        this.defaultInstance = defaultInstance;
        if (defaultInstance != null)
        {
            alias = defaultInstance.getClass().getName();
        }
    }

    /** {@inheritDoc} */
    @Override public Object getDefaultInstance()
    {
        return defaultInstance;
    }

    /** {@inheritDoc} */
    @Override public void setDefaultInstance(Object instance)
    {
        defaultInstance = instance;
    }

    /** 
     * A utility method which returns the Class from the given Class name
     * using the current type context's class loader with 
     * createASObjectForMissingType=false.  
     * 
     * @param className the class name.
     * @return a Class object for the named class.
     */
    public static Class getClassFromClassName(String className)
    {
        return getClassFromClassName(className, false);
    }

    /** 
     * A utility method which returns the Class from the given Class name
     * using the current type context's class loader. 
     * 
     * @param className the class name.
     * @param createASObjectForMissingType determines whether an ASObject is created
     * for a type that is not found on the server, instead of throwing an error.
     * 
     * @return a Class object for the named class.
     */
    public static Class getClassFromClassName(String className, boolean createASObjectForMissingType)
    {
        TypeMarshallingContext typeContext = TypeMarshallingContext.getTypeMarshallingContext();
        try
        {
            return ClassUtil.createClass(className, typeContext.getClassLoader());
        }
        catch (MessageException me)
        {
            // Type was not found.
            if (me.getCode().startsWith(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE))
            {
                if (createASObjectForMissingType) // Return ASObject instead.
                    return ASObject.class;
            }
            throw me; // Rethrow.
        }
    }

    /**
     * A utility method which creates an instance from a given class name.  It assumes
     * the class has a zero arg constructor.
     * @param className the class name
     * @return the instance of the named class.
     */
    public static Object createInstanceFromClassName(String className)
    {
        Class desiredClass = getClassFromClassName(className);
        return ClassUtil.createDefaultInstance(desiredClass, null);
    }

    /** {@inheritDoc} */
    @Override public Object createInstance(String className)
    {
        Object instance;

        if (className == null || className.length() == 0)
        {
            instance = new ASObject();
        }
        else if (className.startsWith(">")) // Handle [RemoteClass] (no server alias)
        {
            instance = new ASObject();
            ((ASObject)instance).setType(className);
        }
        else
        {
            SerializationContext context = getSerializationContext();
            if (context.instantiateTypes || className.startsWith("flex."))
            {
                return createInstanceFromClassName(className);
            }
            else
            {
                // Just return type info with an ASObject...
                instance = new ASObject();
                ((ASObject)instance).setType(className);
            }
        }
        return instance;
    }

    /** {@inheritDoc} */
    @Override public List getPropertyNames()
    {
        return getPropertyNames(getDefaultInstance());
    }

    /** {@inheritDoc} */
    @Override public Class getType(String propertyName)
    {
        return getType(getDefaultInstance(), propertyName);
    }
    
    /** {@inheritDoc} */
    @Override public Object getValue(String propertyName)
    {
        return getValue(getDefaultInstance(), propertyName);
    }

    /** {@inheritDoc} */
    @Override public void setValue(String propertyName, Object value)
    {
        setValue(getDefaultInstance(), propertyName, value);
    }

    /** {@inheritDoc} */
    @Override public void setAlias(String value)
    {
        alias = value;
    }

    /** {@inheritDoc} */
    @Override public String getAlias()
    {
        return alias;
    }

    /** {@inheritDoc} */
    @Override public void setDynamic(boolean value)
    {
        dynamic = value;
    }

    /** {@inheritDoc} */
    @Override public boolean isDynamic()
    {
        return dynamic;
    }

    /** {@inheritDoc} */
    @Override public boolean isExternalizable()
    {
        return externalizable;
    }

    /** {@inheritDoc} */
    @Override public void setExternalizable(boolean value)
    {
        externalizable = value;
    }

    /** {@inheritDoc} */
    @Override public boolean isExternalizable(Object instance)
    {
        return instance instanceof Externalizable;
    }

    /** {@inheritDoc} */
    @Override public SerializationContext getSerializationContext()
    {
        if (context == null)
        {
            return SerializationContext.getSerializationContext();
        }
        return context;
    }

    /** {@inheritDoc} */
    @Override public void setSerializationContext(SerializationContext value)
    {
        context = value;
    }

    /** {@inheritDoc} */
    @Override public void setIncludeReadOnly(boolean value)
    {
        includeReadOnly = value;
    }

    /** {@inheritDoc} */
    @Override public boolean getIncludeReadOnly()
    {
        return includeReadOnly;
    }

    /** {@inheritDoc} */
    @Override public SerializationDescriptor getDescriptor()
    {
        return descriptor;
    }

    /** {@inheritDoc} */
    @Override public void setDescriptor(SerializationDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    /**
     * This is called after the serialization finishes.  We return the same object
     * here... this is an opportunity to replace the instance we use once we have
     * gathered all of the state into a temporary object. 
     */
    @Override public Object instanceComplete(Object instance)
    {
        return instance;
    }

    /**
     * Returns the instance to serialize in place of the supplied instance.
     */
    @Override public Object getInstanceToSerialize(Object instance)
    {
        return instance;
    }
    
    /** {@inheritDoc} */
    @Override public Object clone()
    {
        try
        {
            AbstractProxy clonedProxy= (AbstractProxy) super.clone();
            clonedProxy.setCloneFieldsFrom(this);
            return clonedProxy;
        }
        catch (CloneNotSupportedException e)
        {
            if (Log.isError())
            {
                Logger log = Log.getLogger(LOG_CATEGORY);
                log.error("Failed to clone a property proxy: " + toString());
            }
            MessageException ex = new MessageException();
            ex.setMessage(CONVERSION_ERROR);
            throw ex;            
        }
    }

    /**
     * A string including the default instance, class and descriptor info
     * @return debug string.
     */
    @Override public String toString()
    {
        if (defaultInstance != null)
            return "[Proxy(inst=" + defaultInstance + ") proxyClass=" + getClass() + " descriptor=" + descriptor + "]";
        else
            return "[Proxy(proxyClass=" + getClass() + " descriptor=" + descriptor + "]";
    }

    protected void setCloneFieldsFrom(AbstractProxy source)
    {
        setDescriptor(source.getDescriptor());
        setDefaultInstance(source.getDefaultInstance());
        context = source.context;
        includeReadOnly = source.includeReadOnly;
    }
}
