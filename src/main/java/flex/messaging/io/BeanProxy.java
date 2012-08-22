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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import flex.messaging.MessageException;
import flex.messaging.io.amf.ASObject;
import flex.messaging.log.Log;
import flex.messaging.log.Logger;
import flex.messaging.util.ExceptionUtil;

/**
 * Uses Bean introspection to collect the properties for a given instance.
 *
 * @author Peter Farland
 */
public class BeanProxy extends AbstractProxy
{
    static final long serialVersionUID = 7365078101695257715L;

    private static final int FAILED_PROPERTY_READ_ERROR = 10021;
    private static final int FAILED_PROPERTY_WRITE_ERROR = 10022;
    private static final int NON_READABLE_PROPERTY_ERROR = 10023;
    private static final int NON_WRITABLE_PROPERTY_ERROR = 10024;
    private static final int UNKNOWN_PROPERTY_ERROR = 10025;

    protected static final Map propertyNamesCache = new IdentityHashMap();
    protected static final Map beanPropertyCache = new IdentityHashMap();
    protected static final Map propertyDescriptorCache = new IdentityHashMap();

    protected boolean cacheProperties = true;
    protected boolean cachePropertiesDescriptors = true;
    protected Class stopClass = Object.class;

    protected static final Map ignoreProperties = new HashMap();
    static
    {
        addIgnoreProperty(AbstractMap.class, "empty");
        addIgnoreProperty(AbstractCollection.class, "empty");
        addIgnoreProperty(ASObject.class, "type");
        addIgnoreProperty(Throwable.class, "stackTrace");
        addIgnoreProperty(File.class, "parentFile");
        addIgnoreProperty(File.class, "canonicalFile");
        addIgnoreProperty(File.class, "absoluteFile");
    }

    /**
     * Constructor
     */
    public BeanProxy()
    {
        this(null);
    }

    /**
     * Construct a new BeanProxy with the provided default instance.
     * 
     * @param defaultInstance defines the alias if provided
     */
    public BeanProxy(Object defaultInstance)
    {
        super(defaultInstance);

        // Override default behavior here... standard Map implementations
        // are treated as anonymous Objects, i.e. without an alias.
        if (defaultInstance != null)
        {
            alias = getClassName(defaultInstance);
        }
    }

    /** {@inheritDoc} */
    @Override public String getAlias(Object instance)
    {
        return getClassName(instance);
    }

    /** {@inheritDoc} */
    @Override public List getPropertyNames(Object instance)
    {
        if (instance == null)
            return null;

        Class c = instance.getClass();
        List propertyNames = null;
        Map properties;

        if (descriptor == null)
        {
            propertyNames = (List)propertyNamesCache.get(c);
        }

        if (propertyNames != null)
        {
            return propertyNames;
        }
        else
        {
            properties = getBeanProperties(instance);

            // FIXME: Pete, This list could be generated at the time the bean properties
            // are collected.
            propertyNames = new ArrayList(properties.size());
            Iterator it = properties.keySet().iterator();
            while (it.hasNext())
            {
                propertyNames.add(it.next().toString());
            }

            if (cacheProperties && descriptor == null)
            {
                synchronized(propertyNamesCache)
                {
                    List propertyNames2 = (List)propertyNamesCache.get(c);
                    if (propertyNames2 == null)
                        propertyNamesCache.put(c, propertyNames);
                    else
                        propertyNames = propertyNames2;
                }
            }
        }

        return propertyNames;
    }


    /** {@inheritDoc} */
    @Override public Class getType(Object instance, String propertyName)
    {
        if (instance == null || propertyName == null)
            return null;

        BeanProperty bp = getBeanProperty(instance, propertyName);

        if (bp != null)
        {
            return bp.getType();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override public Object getValue(Object instance, String propertyName)
    {
        if (instance == null || propertyName == null)
            return null;
        BeanProperty bp = getBeanProperty(instance, propertyName);

        if (bp != null)
        {
            return getBeanValue(instance, bp);
        }
        else
        {
            SerializationContext context = getSerializationContext();
            if (!ignorePropertyErrors(context))
            {
                // Property '{propertyName}' not found on class '{alias}'.
                MessageException ex = new MessageException();
                ex.setMessage(UNKNOWN_PROPERTY_ERROR, new Object[] {propertyName, getAlias(instance)});
                throw ex;
            }
        }
        return null;
    }

    /**
     * Gets the value specified by the BeanProperty
     * @param instance Object to get the value from
     * @param bp the property to get
     * @return the value of the property if it exists
     */
    protected final Object getBeanValue(Object instance, BeanProperty bp)
    {
        String propertyName = bp.getName();
        if (bp.isRead())
        {
            try
            {
                Object value = bp.get(instance);
                if (value != null && descriptor != null)
                {
                    SerializationDescriptor subDescriptor = (SerializationDescriptor)descriptor.get(propertyName);
                    if (subDescriptor != null)
                    {
                        PropertyProxy subProxy = PropertyProxyRegistry.getProxyAndRegister(value);
                        subProxy = (PropertyProxy)subProxy.clone();
                        subProxy.setDescriptor(subDescriptor);
                        subProxy.setDefaultInstance(value);
                        value = subProxy;
                    }
                }

                return value;
            }
            catch (Exception e)
            {
                SerializationContext context = getSerializationContext();

                // Log failed property set errors
                if (Log.isWarn() && logPropertyErrors(context))
                {
                    Logger log = Log.getLogger(LOG_CATEGORY);
                    log.warn("Failed to get property {0} on type {1}.",
                             new Object[] {propertyName, getAlias(instance)}, e);
                }

                if (!ignorePropertyErrors(context))
                {
                    // Failed to get property '{propertyName}' on type '{className}'.
                    MessageException ex = new MessageException();
                    ex.setMessage(FAILED_PROPERTY_READ_ERROR, new Object[] {propertyName, getAlias(instance)});
                    ex.setRootCause(e);
                    throw ex;
                }
            }
        }
        else
        {
            SerializationContext context = getSerializationContext();
            if (!ignorePropertyErrors(context))
            {
                //Property '{propertyName}' not readable from class '{alias}'.
                MessageException ex = new MessageException();
                ex.setMessage(NON_READABLE_PROPERTY_ERROR, new Object[] {propertyName, getAlias(instance)});
                throw ex;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override public void setValue(Object instance, String propertyName, Object value)
    {
        BeanProperty bp = getBeanProperty(instance, propertyName);

        if (bp != null)
        {
            if (bp.isWrite())
            {
                try
                {
                    Class desiredPropClass = bp.getType();
                    TypeMarshaller marshaller = TypeMarshallingContext.getTypeMarshaller();
                    value = marshaller.convert(value, desiredPropClass);
                    bp.set(instance, value);
                }
                catch (Exception e)
                {
                    SerializationContext context = getSerializationContext();

                    // Log ignore failed property set errors
                    if (Log.isWarn() && logPropertyErrors(context))
                    {
                        Logger log = Log.getLogger(LOG_CATEGORY);
                        log.warn("Failed to set property {0} on type {1}.",
                                new Object[] {propertyName, getAlias(instance)}, e);
                    }

                    if (!ignorePropertyErrors(context))
                    {
                        // Failed to get property '{propertyName}' on type '{className}'.
                        MessageException ex = new MessageException();
                        ex.setMessage(FAILED_PROPERTY_WRITE_ERROR, new Object[] {propertyName, getAlias(instance)});
                        ex.setRootCause(e);
                        throw ex;
                    }
                }
            }
            else
            {
                SerializationContext context = getSerializationContext();

                if (Log.isWarn() && logPropertyErrors(context))
                {
                    Logger log = Log.getLogger(LOG_CATEGORY);
                    log.warn("Property {0} not writable on class {1}",
                            new Object[] {propertyName, getAlias(instance)});
                }

                if (!ignorePropertyErrors(context))
                {
                    //Property '{propertyName}' not writable on class '{alias}'.
                    MessageException ex = new MessageException();
                    ex.setMessage(NON_WRITABLE_PROPERTY_ERROR, new Object[] {propertyName, getAlias(instance)});
                    throw ex;
                }
            }
        }
        else
        {
            SerializationContext context = getSerializationContext();

            if (Log.isWarn() && logPropertyErrors(context))
            {
                Logger log = Log.getLogger(LOG_CATEGORY);
                log.warn("Ignoring set property {0} for type {1} as a setter could not be found.",
                            new Object[] {propertyName, getAlias(instance)});
            }

            if (!ignorePropertyErrors(context))
            {
                // Property '{propertyName}' not found on class '{alias}'.
                MessageException ex = new MessageException();
                ex.setMessage(UNKNOWN_PROPERTY_ERROR, new Object[] {propertyName, getAlias(instance)});
                throw ex;
            }
        }
    }

    /**
     * Are we ignoring property errors?
     * @param context serialization paramters.
     * @return true if ignoring property errors.
     */
    protected boolean ignorePropertyErrors(SerializationContext context)
    {
        return context.ignorePropertyErrors;
    }

    /**
     * Should we log property errors?
     * @param context serialization parameters.
     * @return true if we should log property errors.
     */
    protected boolean logPropertyErrors(SerializationContext context)
    {
        return context.logPropertyErrors;
    }

    /**
     * Determins the classname for both normal types via Class.getName() and
     * virtual types via ASObject.getType(). Virtual types starting
     * with the special ">" token are also handled and the underlying
     * className is returned.
     *
     * @param instance the object to examine.
     * @return the classname to use for instances of this type
     */
    protected String getClassName(Object instance)
    {
        String className;

        if (instance instanceof ASObject)
        {
            className = ((ASObject)instance).getType();
        }
        else if (instance instanceof ClassAlias)
        {
            className = ((ClassAlias)instance).getAlias();
        }
        else
        {
            className = instance.getClass().getName();
            // If there's an alias, use that as the class name.
            ClassAliasRegistry registry = ClassAliasRegistry.getRegistry();
            String aliasedClass = registry.getClassName(className);
            className = (aliasedClass == null)? className : aliasedClass;
        }

        return className;
    }

    /**
     * Return a map of properties for a object.
     * @param instance object to examine.
     * @return a map of Strings to BeanProperty objects.
     */
    protected Map getBeanProperties(Object instance)
    {
        Class c = instance.getClass();
        Map props;
        if (descriptor == null)
        {
            props = (Map)beanPropertyCache.get(c);
            if (props != null)
            {
                return props;
            }
        }

        props = new HashMap();
        PropertyDescriptor[] pds = getPropertyDescriptors(c);
        if (pds == null)
            return null;

        List excludes = null;
        if (descriptor != null)
        {
            excludes = descriptor.getExcludesForInstance(instance);
            if (excludes == null) // For compatibility with older implementations
                excludes = descriptor.getExcludes();
        }

        // Add standard bean properties first
        for (int i = 0; i < pds.length; i++)
        {
            PropertyDescriptor pd = pds[i];
            String propertyName = pd.getName();
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();

            if (readMethod != null && isPublicAccessor(readMethod.getModifiers()))
            {
                if (!includeReadOnly && writeMethod == null)
                    continue;

                if (excludes != null && excludes.contains(propertyName))
                    continue;

                if (isPropertyIgnored(c, propertyName))
                    continue;

                props.put(propertyName, new BeanProperty(propertyName, pd.getPropertyType(),
                        readMethod, writeMethod, null));
            }
        }

        // Then add public fields to list if property does not alreay exist
        Field[] fields = instance.getClass().getFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            String propertyName = field.getName();
            int modifiers = field.getModifiers();
            if (isPublicField(modifiers) && !props.containsKey(propertyName))
            {
                if (excludes != null && excludes.contains(propertyName))
                    continue;

                if (isPropertyIgnored(c, propertyName))
                    continue;

                props.put(propertyName,
                        new BeanProperty(propertyName, field.getType(), null, null, field));
            }
        }

        if (descriptor == null && cacheProperties)
        {
            synchronized(beanPropertyCache)
            {
                Map props2 = (Map)beanPropertyCache.get(c);
                if (props2 == null)
                    beanPropertyCache.put(c, props);
                else
                    props = props2;
            }
        }

        return props;
    }

    /**
     * Return a specific property descriptor for a named property.
     * @param instance the object to use.
     * @param propertyName the property to get.
     * @return a descriptor for the property.
     */
    protected final BeanProperty getBeanProperty(Object instance, String propertyName)
    {
        Class c = instance.getClass();
        Map props;

        // It is faster to use the BeanProperty cache if we are going to cache it.
        if (descriptor == null && cacheProperties)
        {
            props = getBeanProperties(instance);
            if (props != null)
                return (BeanProperty) props.get(propertyName);

            return null;
        }

        // Otherwise, just build up the property we are asked for
        PropertyDescriptorCacheEntry pce =  getPropertyDescriptorCacheEntry(c);
        if (pce == null)
            return null;

        Object pType = pce.propertiesByName.get(propertyName);
        if (pType == null)
            return null;

        List excludes = null;
        if (descriptor != null)
        {
            excludes = descriptor.getExcludesForInstance(instance);
            if (excludes == null) // For compatibility with older implementations
                excludes = descriptor.getExcludes();
        }

        if (pType instanceof PropertyDescriptor)
        {
            PropertyDescriptor pd = (PropertyDescriptor) pType;

            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();

            if (readMethod != null && isPublicAccessor(readMethod.getModifiers()))
            {
                if (!includeReadOnly && writeMethod == null)
                    return null;

                if (excludes != null && excludes.contains(propertyName))
                    return null;

                if (isPropertyIgnored(c, propertyName))
                    return null;

                return new BeanProperty(propertyName, pd.getPropertyType(), readMethod, writeMethod, null);
            }
        }
        else if (pType instanceof Field)
        {
            Field field = (Field) pType;

            String pName = field.getName();
            int modifiers = field.getModifiers();
            if (isPublicField(modifiers) && pName.equals(propertyName))
            {
                if (excludes != null && excludes.contains(propertyName))
                    return null;

                if (isPropertyIgnored(c, propertyName))
                    return null;

                return new BeanProperty(propertyName, field.getType(), null, null, field);
            }
        }

        return null;
    }

    /**
     * Return an array of JavaBean property descriptors for a class
     * @param c the class to examine.
     * @return an array ot JavaBean PropertyDescriptors.
     */
    private PropertyDescriptor [] getPropertyDescriptors(Class c)
    {
        PropertyDescriptorCacheEntry pce = getPropertyDescriptorCacheEntry(c);
        if (pce == null)
            return null;
        return pce.propertyDescriptors;
    }

    /**
     * Return an entry from the property descrpitor cache for a class. 
     */
    private PropertyDescriptorCacheEntry getPropertyDescriptorCacheEntry(Class c)
    {
        PropertyDescriptorCacheEntry pce = (PropertyDescriptorCacheEntry) propertyDescriptorCache.get(c);

        try
        {
            if (pce == null)
            {
                BeanInfo beanInfo = Introspector.getBeanInfo(c, stopClass);
                pce = new PropertyDescriptorCacheEntry();
                pce.propertyDescriptors = beanInfo.getPropertyDescriptors();
                pce.propertiesByName = createPropertiesByNameMap(pce.propertyDescriptors, c.getFields());
                if (cachePropertiesDescriptors)
                {
                    synchronized(propertyDescriptorCache)
                    {
                        PropertyDescriptorCacheEntry pce2 = (PropertyDescriptorCacheEntry) propertyDescriptorCache.get(c);
                        if (pce2 == null)
                            propertyDescriptorCache.put(c, pce);
                        else
                            pce = pce2;
                    }
                }
            }
        }
        catch (IntrospectionException ex)
        {
            // Log failed property set errors
            if (Log.isError())
            {
                Logger log = Log.getLogger(LOG_CATEGORY);
                log.error("Failed to introspect object of type: " + c + " error: " + ExceptionUtil.toString(ex));
            }

            // Return an empty descriptor rather than crashing
            pce = new PropertyDescriptorCacheEntry();
            pce.propertyDescriptors = new PropertyDescriptor[0];
            pce.propertiesByName = new TreeMap();
        }
        return pce;
    }

    private Map createPropertiesByNameMap(PropertyDescriptor [] pds, Field [] fields)
    {
        Map m = new HashMap(pds.length);
        for (int i = 0; i < pds.length; i++)
        {
            PropertyDescriptor pd = pds[i];
            Method readMethod = pd.getReadMethod();
            if (readMethod != null && isPublicAccessor(readMethod.getModifiers()) &&
                (includeReadOnly || pd.getWriteMethod() != null))
                m.put(pd.getName(), pd);
        }
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];

            if (isPublicField(field.getModifiers()) && !m.containsKey(field.getName()))
                m.put(field.getName(), field);
        }
        return m;
    }

    /**
     * Is this property on the ignore list for this class?
     * @param c the class.
     * @param propertyName the property name.
     * @return true if we should ignore this property.
     */
    public static boolean isPropertyIgnored(Class c, String propertyName)
    {
        boolean result = false;
        Set propertyOwners = (Set)ignoreProperties.get(propertyName);
        if (propertyOwners != null)
        {
            while (c != null)
            {
                if (propertyOwners.contains(c))
                {
                    result = true;
                    break;
                }
                c = c.getSuperclass();
            }
        }
        return result;
    }

    /**
     * Add a property to the ignore list for this class.
     * @param c the class.
     * @param propertyName the property to ignore.
     */
    public static void addIgnoreProperty(Class c, String propertyName)
    {
        synchronized(ignoreProperties)
        {
            Set propertyOwners = (Set)ignoreProperties.get(propertyName);
            if (propertyOwners == null)
            {
                propertyOwners = new HashSet();
                ignoreProperties.put(propertyName, propertyOwners);
            }
            propertyOwners.add(c);
        }
    }

    /**
     * Do the provided modifiers indicate that this is public?
     * @param modifiers the flags to check
     * @return true if public but not final, static or transient.
     */
    public static boolean isPublicField(int modifiers)
    {
        if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers)
                && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Do the provided modifiers indicate that this is public?
     * @param modifiers the flags to check
     * @return true if public but not static.
     */
    public static boolean isPublicAccessor(int modifiers)
    {
        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * A class that holds information about a bean property.
     */
    protected static class BeanProperty
    {
        private String name;
        private Class type;
        private Method readMethod, writeMethod;
        private Field field;

        protected BeanProperty(String name, Class type, Method read, Method write, Field field)
        {
            this.name = name;
            this.type = type;
            this.writeMethod = write;
            this.readMethod = read;
            this.field = field;
        }

        /**
         * The name of the property..
         * @return the name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * The type of the property
         * @return the type
         */
        public Class getType()
        {
            return type;
        }

        /**
         * Is there a setter for this property?
         * @return true if there is a write method.
         */
        public boolean isWrite()
        {
            return writeMethod != null || field != null;
        }

        /**
         * Is there a getter for this property?
         * @return true if there is a read method.
         */
        public boolean isRead()
        {
            return readMethod != null || field != null;
        }

        /**
         * Returns the Class object that declared the public field or getter function.
         * @return an object of the declaring class for the read method or null if the read method is undefined.
         */
        public Class getReadDeclaringClass()
        {
            if (readMethod != null)
                return readMethod.getDeclaringClass();
            else if (field != null)
                return field.getDeclaringClass();
            else
                return null;
        }

        /**
         * Return a class that represents the type of the property.
         * @return the type of the property or null if there is no read method defined.
         */
        public Class getReadType()
        {
            if (readMethod != null)
                return readMethod.getReturnType();
            else if (field != null)
                return field.getType();
            else
                return null;
        }

        /**
         * 
         * Returns a string indicating the setter or field name of the property. 
         * The setter is prefixed by 'method ', or the field is prefixed by 'field '.
         * @return A string suitable for debugging.
         */
        public String getWriteName()
        {
            if (writeMethod != null)
                return "method " + writeMethod.getName();
            else if (field != null)
                return "field " + field.getName();
            else
                return null;
        }

        /**
         * Set the property of the object to the specified value.
         * @param bean the bean to set the property on.
         * @param value the value to set.
         * @throws IllegalAccessException if no access.
         * @throws InvocationTargetException if the setter throws an exception.
         */
        public void set(Object bean, Object value) throws IllegalAccessException,
                InvocationTargetException
        {
            if (writeMethod != null)
            {
                writeMethod.invoke(bean, new Object[] { value });
            }
            else if (field != null)
            {
                field.set(bean, value);
            }
            else
            {
                throw new MessageException("Setter not found for property " + name);
            }
        }

        /**
         * Get the value of this property from the specified object.
         * @param bean the object to retrieve the value from
         * @return the value of the property.
         * @throws IllegalAccessException if no access.
         * @throws InvocationTargetException if the getter throws an exception.
         */
        public Object get(Object bean) throws IllegalAccessException, InvocationTargetException
        {
            Object obj = null;
            if (readMethod != null)
            {
                obj = readMethod.invoke(bean, null);
            }
            else if (field != null)
            {
                obj = field.get(bean);
            }
            return obj;
        }
    }

    /** {@inheritDoc} */
    @Override public Object clone()
    {
        return super.clone();
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * Clears all static caches.
//      */
//     public static void clear()
//     {
//         synchronized(ignoreProperties)
//         {
//             ignoreProperties.clear();
//         }
//         synchronized(propertyNamesCache)
//         {
//             propertyNamesCache.clear();
//         }
//         synchronized(beanPropertyCache)
//         {
//             beanPropertyCache.clear();
//         }
//         synchronized(propertyDescriptorCache)
//         {
//             propertyDescriptorCache.clear();
//         }
//     }

    protected static class PropertyDescriptorCacheEntry
    {
        PropertyDescriptor [] propertyDescriptors;
        Map propertiesByName;
    }
}
