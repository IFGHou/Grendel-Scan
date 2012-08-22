/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
package org.cobra_grendel.js;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Function;

public class JavaClassWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final Map functions = new HashMap();
	private PropertyInfo integerIndexer;
	private final Class javaClass;
	private PropertyInfo nameIndexer;
	private final Map properties = new HashMap();
	
	public JavaClassWrapper(Class class1)
	{
		super();
		javaClass = class1;
		scanMethods();
	}
	
	public String getClassName()
	{
		String className = javaClass.getName();
		int lastDotIdx = className.lastIndexOf('.');
		return lastDotIdx == -1 ? className : className.substring(lastDotIdx + 1);
	}
	
	public Function getFunction(String name)
	{
		return (Function) functions.get(name);
	}
	
	public PropertyInfo getIntegerIndexer()
	{
		return integerIndexer;
	}
	
	public PropertyInfo getNameIndexer()
	{
		return nameIndexer;
	}
	
	public PropertyInfo getProperty(String name)
	{
		return (PropertyInfo) properties.get(name);
	}
	
	public Object newInstance() throws InstantiationException, IllegalAccessException
	{
		return javaClass.newInstance();
	}
	
	@Override
	public String toString()
	{
		return javaClass.getName();
	}
	
	private void ensurePropertyKnown(String methodName, Method method)
	{
		String capPropertyName;
		String propertyName;
		boolean getter = false;
		if (methodName.startsWith("get"))
		{
			capPropertyName = methodName.substring(3);
			propertyName = uncapitalize(capPropertyName);
			getter = true;
		}
		else if (methodName.startsWith("set"))
		{
			capPropertyName = methodName.substring(3);
			propertyName = uncapitalize(capPropertyName);
		}
		else if (methodName.startsWith("is"))
		{
			capPropertyName = methodName.substring(2);
			propertyName = uncapitalize(capPropertyName);
			getter = true;
		}
		else
		{
			throw new IllegalArgumentException("methodName=" + methodName);
		}
		PropertyInfo pinfo = (PropertyInfo) properties.get(propertyName);
		if (pinfo == null)
		{
			Class pt = getter ? method.getReturnType() : method.getParameterTypes()[0];
			pinfo = new PropertyInfo(propertyName, pt);
			properties.put(propertyName, pinfo);
		}
		if (getter)
		{
			pinfo.setGetter(method);
		}
		else
		{
			pinfo.setSetter(method);
		}
	}
	
	private boolean isIntegerIndexer(String name, Method method)
	{
		return ("item".equals(name) && (method.getParameterTypes().length == 1))
		        || ("setItem".equals(name) && (method.getParameterTypes().length == 2));
	}
	
	private boolean isNameIndexer(String name, Method method)
	{
		return ("namedItem".equals(name) && (method.getParameterTypes().length == 1))
		        || ("setNamedItem".equals(name) && (method.getParameterTypes().length == 2));
	}
	
	private boolean isPropertyMethod(String name, Method method)
	{
		if (name.startsWith("get") || name.startsWith("is"))
		{
			return method.getParameterTypes().length == 0;
		}
		else if (name.startsWith("set"))
		{
			return method.getParameterTypes().length == 1;
		}
		else
		{
			return false;
		}
	}
	
	private void scanMethods()
	{
		Method[] methods = javaClass.getMethods();
		int len = methods.length;
		for (int i = 0; i < len; i++)
		{
			Method method = methods[i];
			String name = method.getName();
			if (isPropertyMethod(name, method))
			{
				ensurePropertyKnown(name, method);
			}
			if (isNameIndexer(name, method))
			{
				updateNameIndexer(name, method);
			}
			else if (isIntegerIndexer(name, method))
			{
				updateIntegerIndexer(name, method);
			}
			else
			{
				JavaFunctionObject f = (JavaFunctionObject) functions.get(name);
				if (f == null)
				{
					f = new JavaFunctionObject(name);
					functions.put(name, f);
				}
				f.addMethod(method);
			}
		}
	}
	
	private String uncapitalize(String text)
	{
		try
		{
			return Character.toLowerCase(text.charAt(0)) + text.substring(1);
		}
		catch (IndexOutOfBoundsException iob)
		{
			return text;
		}
	}
	
	private void updateIntegerIndexer(String methodName, Method method)
	{
		boolean getter = true;
		if (methodName.startsWith("set"))
		{
			getter = false;
		}
		PropertyInfo indexer = integerIndexer;
		if (indexer == null)
		{
			Class pt = getter ? method.getReturnType() : method.getParameterTypes()[1];
			indexer = new PropertyInfo("$item", pt);
			integerIndexer = indexer;
		}
		if (getter)
		{
			indexer.setGetter(method);
		}
		else
		{
			indexer.setSetter(method);
		}
	}
	
	private void updateNameIndexer(String methodName, Method method)
	{
		boolean getter = true;
		if (methodName.startsWith("set"))
		{
			getter = false;
		}
		PropertyInfo indexer = nameIndexer;
		if (indexer == null)
		{
			indexer = new PropertyInfo("$item", Object.class);
			nameIndexer = indexer;
		}
		if (getter)
		{
			indexer.setGetter(method);
		}
		else
		{
			indexer.setSetter(method);
		}
	}
}
