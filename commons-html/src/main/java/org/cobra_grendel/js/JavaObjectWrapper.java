/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
package org.cobra_grendel.js;

import java.lang.reflect.Method;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaObjectWrapper extends ScriptableObject
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaObjectWrapper.class);
    /**
	 * 
	 */
    private static final long serialVersionUID = 3901811777319788638L;

    public static Function getConstructor(final String className, final JavaClassWrapper classWrapper, final Scriptable scope)
    {
        return new JavaConstructorObject(className, classWrapper);
    }

    public static Function getConstructor(final String className, final JavaClassWrapper classWrapper, final Scriptable scope, final JavaInstantiator instantiator)
    {
        return new JavaConstructorObject(className, classWrapper, instantiator);
    }

    private final JavaClassWrapper classWrapper;

    private final Object delegate;

    public JavaObjectWrapper(final JavaClassWrapper classWrapper) throws InstantiationException, IllegalAccessException
    {
        this.classWrapper = classWrapper;
        // Retaining a strong reference, but note
        // that the object wrapper map uses weak keys
        // and weak values.
        Object delegate = this.classWrapper.newInstance();
        this.delegate = delegate;
    }

    public JavaObjectWrapper(final JavaClassWrapper classWrapper, final Object delegate)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("Argument delegate cannot be null.");
        }
        this.classWrapper = classWrapper;
        // Retaining a strong reference, but note
        // that the object wrapper map uses weak keys
        // and weak values.
        this.delegate = delegate;
    }

    @Override
    public Object get(final int index, final Scriptable start)
    {
        PropertyInfo pinfo = classWrapper.getIntegerIndexer();
        if (pinfo == null)
        {
            return super.get(index, start);
        }
        else
        {
            try
            {
                Method getter = pinfo.getGetter();
                if (getter == null)
                {
                    throw new EvaluatorException("Indexer is write-only");
                }
                // Cannot retain delegate with a strong reference.
                Object javaObject = getJavaObject();
                if (javaObject == null)
                {
                    throw new IllegalStateException("Java object (class=" + classWrapper + ") is null.");
                }
                Object raw = getter.invoke(javaObject, new Object[] { new Integer(index) });
                return JavaScript.getInstance().getJavascriptObject(raw, getParentScope());
            }
            catch (Exception err)
            {
                throw new WrappedException(err);
            }
        }
    }

    @Override
    public Object get(final String name, final Scriptable start)
    {
        PropertyInfo pinfo = classWrapper.getProperty(name);
        if (pinfo != null)
        {
            Method getter = pinfo.getGetter();
            if (getter == null)
            {
                throw new EvaluatorException("Property '" + name + "' is not readable");
            }
            try
            {
                // Cannot retain delegate with a strong reference.
                Object javaObject = getJavaObject();
                if (javaObject == null)
                {
                    throw new IllegalStateException("Java object (class=" + classWrapper + ") is null.");
                }
                Object val = getter.invoke(javaObject, (Object[]) null);
                return JavaScript.getInstance().getJavascriptObject(val, start.getParentScope());
            }
            catch (Exception err)
            {
                throw new WrappedException(err);
            }
        }
        else
        {
            Function f = classWrapper.getFunction(name);
            if (f != null)
            {
                return f;
            }
            else
            {
                PropertyInfo ni = classWrapper.getNameIndexer();
                if (ni != null)
                {
                    Method getter = ni.getGetter();
                    if (getter != null)
                    {
                        // Cannot retain delegate with a strong reference.
                        Object javaObject = getJavaObject();
                        if (javaObject == null)
                        {
                            throw new IllegalStateException("Java object (class=" + classWrapper + ") is null.");
                        }
                        try
                        {
                            Object val = getter.invoke(javaObject, new Object[] { name });
                            if (val == null)
                            {
                                // There might not be an indexer setter.
                                return super.get(name, start);
                            }
                            else
                            {
                                return JavaScript.getInstance().getJavascriptObject(val, start.getParentScope());
                            }
                        }
                        catch (Exception err)
                        {
                            throw new WrappedException(err);
                        }
                    }
                    else
                    {
                        return super.get(name, start);
                    }
                }
                else
                {
                    return super.get(name, start);
                }
            }
        }
    }

    @Override
    public String getClassName()
    {
        return classWrapper.getClassName();
    }

    @Override
    public java.lang.Object getDefaultValue(final java.lang.Class hint)
    {
        LOGGER.info("getDefaultValue(): hint=" + hint + ",this=" + getJavaObject());
        if (hint == null || String.class.equals(hint))
        {
            Object javaObject = getJavaObject();
            if (javaObject == null)
            {
                throw new IllegalStateException("Java object (class=" + classWrapper + ") is null.");
            }
            return javaObject.toString();
        }
        else if (Number.class.isAssignableFrom(hint))
        {
            Object javaObject = getJavaObject();
            if (javaObject instanceof Number)
            {
                return javaObject;
            }
            else if (javaObject instanceof String)
            {
                return Double.valueOf((String) javaObject);
            }
            else
            {
                return super.getDefaultValue(hint);
            }
        }
        else
        {
            return super.getDefaultValue(hint);
        }
    }

    /**
     * Returns the Java object.
     * 
     * @return An object or <code>null</code> if garbage collected.
     */
    public Object getJavaObject()
    {
        // Cannot retain delegate with a strong reference.
        return delegate;
    }

    @Override
    public void put(final int index, final Scriptable start, final Object value)
    {
        PropertyInfo pinfo = classWrapper.getIntegerIndexer();
        if (pinfo == null)
        {
            super.put(index, start, value);
        }
        else
        {
            try
            {
                Method setter = pinfo.getSetter();
                if (setter == null)
                {
                    throw new EvaluatorException("Indexer is read-only");
                }
                Object actualValue;
                actualValue = JavaScript.getInstance().getJavaObject(value, pinfo.getPropertyType());
                setter.invoke(getJavaObject(), new Object[] { new Integer(index), actualValue });
            }
            catch (Exception err)
            {
                throw new WrappedException(err);
            }
        }
    }

    @Override
    public void put(final String name, final Scriptable start, final Object value)
    {
        PropertyInfo pinfo = classWrapper.getProperty(name);
        if (pinfo != null)
        {
            Method setter = pinfo.getSetter();
            if (setter == null)
            {
                throw new EvaluatorException("Property '" + name + "' is not settable in " + classWrapper.getClassName() + ".");
            }
            try
            {
                Object actualValue;
                actualValue = JavaScript.getInstance().getJavaObject(value, pinfo.getPropertyType());
                setter.invoke(getJavaObject(), new Object[] { actualValue });
            }
            catch (IllegalArgumentException iae)
            {
                Exception newException = new IllegalArgumentException("Property named '" + name + "' could not be set with value " + value + ".", iae);
                throw new WrappedException(newException);
            }
            catch (Exception err)
            {
                throw new WrappedException(err);
            }
        }
        else
        {
            PropertyInfo ni = classWrapper.getNameIndexer();
            if (ni != null)
            {
                Method setter = ni.getSetter();
                if (setter != null)
                {
                    try
                    {
                        Object actualValue;
                        actualValue = JavaScript.getInstance().getJavaObject(value, ni.getPropertyType());
                        setter.invoke(getJavaObject(), new Object[] { name, actualValue });
                    }
                    catch (Exception err)
                    {
                        throw new WrappedException(err);
                    }
                }
                else
                {
                    super.put(name, start, value);
                }
            }
            else
            {
                super.put(name, start, value);
            }
        }
    }

    @Override
    public String toString()
    {
        Object javaObject = getJavaObject();
        String type = javaObject == null ? "<null>" : javaObject.getClass().getName();
        return "JavaObjectWrapper[object=" + getJavaObject() + ",type=" + type + "]";
    }
}
