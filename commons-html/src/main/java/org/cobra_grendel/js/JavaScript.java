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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.cobra_grendel.util.Objects;
import org.mozilla.javascript.Scriptable;

public class JavaScript implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static JavaScript instance = new JavaScript();

    public static JavaScript getInstance()
    {
        return instance;
    }

    // objectMap must be a map that uses weak keys
    // and refers to values using weak references.
    // Keys are java objects other than ScriptableDelegate instances.
    private final WeakHashMap javaObjectToWrapper = new WeakHashMap();

    public Object getJavaObject(final Object javascriptObject, final Class type)
    {
        if (javascriptObject instanceof JavaObjectWrapper)
        {
            return ((JavaObjectWrapper) javascriptObject).getJavaObject();
        }
        else if (javascriptObject == null)
        {
            return null;
        }
        else if (type == String.class)
        {
            if (javascriptObject instanceof String)
            {
                return javascriptObject;
            }
            else if (javascriptObject instanceof Double)
            {
                String text = String.valueOf(javascriptObject);
                if (text.endsWith(".0"))
                {
                    return text.substring(0, text.length() - 2);
                }
                else
                {
                    return text;
                }
            }
            // else if(javascriptObject instanceof Function) {
            // return ((Function)
            // javascriptObject).getDefaultValue(String.class);
            // }
            else
            {
                return String.valueOf(javascriptObject);
            }
        }
        else if (type == int.class || type == Integer.class)
        {
            if (javascriptObject instanceof Double)
            {
                return new Integer(((Double) javascriptObject).intValue());
            }
            else if (javascriptObject instanceof Integer)
            {
                return javascriptObject;
            }
            else if (javascriptObject instanceof String)
            {
                return Integer.valueOf((String) javascriptObject);
            }
            else if (javascriptObject instanceof Short)
            {
                return new Integer(((Short) javascriptObject).shortValue());
            }
            else if (javascriptObject instanceof Long)
            {
                return new Integer(((Long) javascriptObject).intValue());
            }
            else if (javascriptObject instanceof Float)
            {
                return new Integer(((Float) javascriptObject).intValue());
            }
            else
            {
                return javascriptObject;
            }
        }
        else
        {
            return javascriptObject;
        }
    }

    /**
     * Returns an object that may be used by the Javascript engine.
     * 
     * @param raw
     * @return
     */
    public Object getJavascriptObject(final Object raw, final Scriptable scope)
    {
        if (raw instanceof String || raw instanceof Scriptable)
        {
            return raw;
        }
        else if (raw == null)
        {
            return null;
        }
        else if (raw.getClass().isPrimitive())
        {
            return raw;
        }
        else if (raw instanceof ScriptableDelegate)
        {
            // Classes that implement ScriptableDelegate retain
            // the JavaScript object. Reciprocal linking cannot
            // be done with weak hash maps and without leaking.
            synchronized (this)
            {
                Object javascriptObject = ((ScriptableDelegate) raw).getScriptable();
                if (javascriptObject == null)
                {
                    JavaObjectWrapper jow = new JavaObjectWrapper(JavaClassWrapperFactory.getInstance().getClassWrapper(raw.getClass()), raw);
                    javascriptObject = jow;
                    jow.setParentScope(scope);
                    ((ScriptableDelegate) raw).setScriptable(jow);
                }
                return javascriptObject;
            }
        }
        else if (Objects.isBoxClass(raw.getClass()))
        {
            return raw;
        }
        else
        {
            synchronized (javaObjectToWrapper)
            {
                // WeakHashMaps will retain keys if the value refers to the key.
                // That's why we need to refer to the value weakly too.
                WeakReference valueRef = (WeakReference) javaObjectToWrapper.get(raw);
                JavaObjectWrapper jow = null;
                if (valueRef != null)
                {
                    jow = (JavaObjectWrapper) valueRef.get();
                }
                if (jow == null)
                {
                    Class javaClass = raw.getClass();
                    JavaClassWrapper wrapper = JavaClassWrapperFactory.getInstance().getClassWrapper(javaClass);
                    jow = new JavaObjectWrapper(wrapper, raw);
                    jow.setParentScope(scope);
                    javaObjectToWrapper.put(raw, new WeakReference(jow));
                }
                return jow;
            }
        }
    }
}
