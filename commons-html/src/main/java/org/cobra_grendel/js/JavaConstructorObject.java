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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JavaConstructorObject extends ScriptableObject implements Function
{
    public static class SimpleInstantiator implements JavaInstantiator
    {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;
        private final JavaClassWrapper classWrapper;

        public SimpleInstantiator(final JavaClassWrapper classWrapper)
        {
            super();
            this.classWrapper = classWrapper;
        }

        @Override
        public Object newInstance() throws InstantiationException, IllegalAccessException
        {
            return classWrapper.newInstance();
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -836761555035355455L;
    private final JavaClassWrapper classWrapper;
    private final JavaInstantiator instantiator;

    private final String name;

    public JavaConstructorObject(final String name, final JavaClassWrapper classWrapper)
    {
        this.name = name;
        this.classWrapper = classWrapper;
        instantiator = new SimpleInstantiator(classWrapper);
    }

    public JavaConstructorObject(final String name, final JavaClassWrapper classWrapper, final JavaInstantiator instantiator)
    {
        this.name = name;
        this.classWrapper = classWrapper;
        this.instantiator = instantiator;
    }

    @Override
    public Object call(final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Scriptable construct(final Context cx, final Scriptable scope, final Object[] args)
    {
        try
        {
            Object javaObject = instantiator.newInstance();
            Scriptable newObject = new JavaObjectWrapper(classWrapper, javaObject);
            newObject.setParentScope(scope);
            return newObject;
        }
        catch (Exception err)
        {
            throw new IllegalStateException(err.getMessage());
        }
    }

    @Override
    public String getClassName()
    {
        return name;
    }

    @Override
    public java.lang.Object getDefaultValue(final java.lang.Class hint)
    {
        if (String.class.equals(hint))
        {
            return "function " + name;
        }
        else
        {
            return super.getDefaultValue(hint);
        }
    }
}
