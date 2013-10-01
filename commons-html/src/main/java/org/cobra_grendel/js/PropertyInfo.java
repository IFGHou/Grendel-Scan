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
import java.lang.reflect.Method;

public class PropertyInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Method getter, setter;
    private final String name;
    private final Class propertyType;

    public PropertyInfo(final String name, final Class propType)
    {
        super();
        this.name = name;
        propertyType = propType;
    }

    public Method getGetter()
    {
        return getter;
    }

    public String getName()
    {
        return name;
    }

    public Class getPropertyType()
    {
        return propertyType;
    }

    public Method getSetter()
    {
        return setter;
    }

    public void setGetter(final Method getter)
    {
        this.getter = getter;
    }

    public void setSetter(final Method setter)
    {
        this.setter = setter;
    }
}
