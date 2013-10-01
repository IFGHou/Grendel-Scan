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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import flex.messaging.MessageException;

/**
 * Utility class to create instances of Complex Types and handle error conditions consistently across the RemoteObject code base.
 * 
 * @author Peter Farland
 * @exclude
 */
public class ClassUtil
{
    private static final int TYPE_NOT_FOUND = 10008;
    private static final int UNEXPECTED_TYPE = 10009;
    private static final int CANNOT_CREATE_TYPE = 10010;
    private static final int SECURITY_ERROR = 10011;
    private static final int UNKNOWN_ERROR = 10012;

    private ClassUtil()
    {
    }

    public static Class createClass(String type)
    {
        return createClass(type, null);
    }

    public static Class createClass(String type, ClassLoader loader)
    {
        try
        {
            if (type != null)
                type = type.trim();

            if (loader == null) // will use the loader for this class
                return Class.forName(type);
            else
                return Class.forName(type, true, loader);
        }
        catch (ClassNotFoundException cnf)
        {
            // Cannot invoke type '{type}'
            MessageException ex = new MessageException();
            ex.setMessage(TYPE_NOT_FOUND, new Object[] { type });
            ex.setDetails(TYPE_NOT_FOUND, "0", new Object[] { type });
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
    }

    public static Object createDefaultInstance(Class cls, Class expectedInstance)
    {
        String type = cls.getName();

        try
        {
            Object instance = cls.newInstance();

            if (expectedInstance != null && !expectedInstance.isInstance(instance))
            {
                // Given type '{name}' is not of expected type '{expectedName}'.
                MessageException ex = new MessageException();
                ex.setMessage(UNEXPECTED_TYPE, new Object[] { instance.getClass().getName(), expectedInstance.getName() });
                ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
                throw ex;
            }

            return instance;
        }
        catch (IllegalAccessException ia)
        {
            boolean details = false;
            StringBuffer message = new StringBuffer("Unable to create a new instance of type ");
            message.append(type);

            // Look for a possible cause...

            // Class might not have a suitable constructor?
            if (!hasValidDefaultConstructor(cls))
            {
                details = true;
            }

            // Unable to create a new instance of type '{type}'.
            MessageException ex = new MessageException();
            ex.setMessage(CANNOT_CREATE_TYPE, new Object[] { type });
            if (details)
            {
                // Types must have a public, no arguments constructor
                ex.setDetails(CANNOT_CREATE_TYPE, "0");
            }
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
        catch (InstantiationException ine)
        {
            String variant = null;

            // Look for a possible cause...
            if (cls != null)
            {
                // Class is really an interface?
                if (cls.isInterface())
                {
                    // Interfaces cannot be instantiated.
                    variant = "1";
                }
                else if (isAbstract(cls))
                {
                    // Abstract types cannot be instantiated.
                    variant = "2";
                }
                // Class might not have a suitable constructor?
                else if (!hasValidDefaultConstructor(cls))
                {
                    // Types cannot be instantiated without a public, no arguments constructor.
                    variant = "3";
                }
            }

            MessageException ex = new MessageException();
            ex.setMessage(CANNOT_CREATE_TYPE, new Object[] { type });
            if (variant != null)
                ex.setDetails(CANNOT_CREATE_TYPE, variant);
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
        catch (SecurityException se)
        {
            MessageException ex = new MessageException();
            ex.setMessage(SECURITY_ERROR, new Object[] { type });
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            ex.setRootCause(se);
            throw ex;
        }
        catch (Exception e)
        {
            MessageException ex = new MessageException();
            ex.setMessage(UNKNOWN_ERROR, new Object[] { type });
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            ex.setRootCause(e);
            throw ex;
        }
    }

    public static boolean isAbstract(Class cls)
    {
        boolean abs = false;

        try
        {
            if (cls != null)
            {
                int mod = cls.getModifiers();
                abs = Modifier.isAbstract(mod);
            }
        }
        catch (Throwable t)
        {
        }

        return abs;
    }

    public static boolean hasValidDefaultConstructor(Class cls)
    {
        boolean valid = false;

        try
        {
            if (cls != null)
            {
                Constructor c = cls.getConstructor(new Class[] {});
                int mod = c.getModifiers();
                valid = Modifier.isPublic(mod);
            }
        }
        catch (Throwable t)
        {
        }

        return valid;
    }

    public static String classLoaderToString(ClassLoader cl)
    {
        if (cl == null)
            return "null";

        if (cl == ClassLoader.getSystemClassLoader())
            return "system";

        StringBuffer sb = new StringBuffer();
        sb.append("hashCode: " + System.identityHashCode(cl) + " (parent " + ClassUtil.classLoaderToString(cl.getParent()) + ")");
        return sb.toString();
    }
}
