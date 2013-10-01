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

import java.util.IdentityHashMap;
import java.util.Map;

import flex.messaging.FlexContext;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.translator.ASTranslator;

/**
 * A simple context to hold type marshalling specific settings.
 */
public class TypeMarshallingContext
{
    private static ThreadLocal contexts = new ThreadLocal();
    private static ThreadLocal marshallers = new ThreadLocal();
    private IdentityHashMap knownObjects;
    private ClassLoader classLoader;

    /**
     * Constructs a default type marshalling context.
     */
    public TypeMarshallingContext()
    {
    }

    /**
     * Establishes a TypeMarshallingContext for the current thread. Users are not expected to call this function.
     * 
     * @param context
     *            The current TypeMarshallingContext.
     */
    public static void setTypeMarshallingContext(TypeMarshallingContext context)
    {
        if (context == null)
            contexts.remove();
        else
            contexts.set(context);
    }

    /**
     * @return The current thread's TypeMarshallingContext.
     */
    public static TypeMarshallingContext getTypeMarshallingContext()
    {
        TypeMarshallingContext context = (TypeMarshallingContext) contexts.get();
        if (context == null)
        {
            context = new TypeMarshallingContext();
            TypeMarshallingContext.setTypeMarshallingContext(context);
        }
        return context;
    }

    /**
     * Establishes a TypeMarshallingContext for the current thread. Users are not expected to call this function.
     * 
     * @param marshaller
     *            The current TypeMarshaller.
     */
    public static void setTypeMarshaller(TypeMarshaller marshaller)
    {
        if (marshaller == null)
            marshallers.remove();
        else
            marshallers.set(marshaller);
    }

    /**
     * @return The current thread's TypeMarshaller.
     */
    public static TypeMarshaller getTypeMarshaller()
    {
        TypeMarshaller marshaller = (TypeMarshaller) marshallers.get();
        if (marshaller == null)
        {
            marshaller = new ASTranslator();
            setTypeMarshaller(marshaller);
        }

        return marshaller;
    }

    /**
     * Returns the custom ClassLoader for this type marshalling session, or defaults to the current MessageBroker's ClassLoader if none has been set.
     */
    public ClassLoader getClassLoader()
    {
        if (classLoader != null)
            return classLoader;
        else
            return FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader();
    }

    /**
     * Sets a custom classloader for this type marshalling session that will be used to create new instances of strongly typed objects.
     */
    public void setClassLoader(ClassLoader loader)
    {
        classLoader = loader;
    }

    /**
     * A map of known objects already encountered in this type marshalling session.
     */
    public IdentityHashMap getKnownObjects()
    {
        if (knownObjects == null)
            knownObjects = new IdentityHashMap(64);

        return knownObjects;
    }

    /**
     * Sets the list of the objects already encountered for this type marshalling session.
     */
    public void setKnownObjects(IdentityHashMap knownObjects)
    {
        this.knownObjects = knownObjects;
    }

    /**
     * Resets the list of known objects.
     */
    public void reset()
    {
        if (knownObjects != null)
            knownObjects.clear();
    }

    /**
     * A utility method to determine whether an anonymous type specifies a strong type name, such as ASObject.getType() or the legacy Flash Remoting convention of using a _remoteClass property.
     * 
     * @return The name of the strong type, or null if none was specified.
     */
    public static String getType(Object obj)
    {
        String type = null;

        if (obj != null && obj instanceof Map)
        {
            Map map = (Map) obj;

            // Check for an Object.registerClass Typed ASObject
            if (map instanceof ASObject)
            {
                ASObject aso = (ASObject) map;
                type = aso.getType();
            }

            SerializationContext sc = SerializationContext.getSerializationContext();

            if (type == null && sc.supportRemoteClass)
            {
                Object registerClass = map.get(MessageIOConstants.REMOTE_CLASS_FIELD);
                if (registerClass != null && registerClass instanceof String)
                {
                    type = (String) registerClass;
                }
            }
        }

        return type;
    }

    /**
     * Clears out the thread local state after the request completes.
     */
    public static void clearThreadLocalObjects()
    {
        contexts.remove();
        marshallers.remove();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Destroy static thread local storage.
    // * Call ONLY on shutdown.
    // */
    // public static void releaseThreadLocalObjects()
    // {
    // contexts = null;
    // marshallers = null;
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Create static thread local storage.
    // */
    // public static void createThreadLocalObjects()
    // {
    // if (contexts == null)
    // contexts = new ThreadLocal();
    // if (marshallers == null)
    // marshallers = new ThreadLocal();
    // }
}
