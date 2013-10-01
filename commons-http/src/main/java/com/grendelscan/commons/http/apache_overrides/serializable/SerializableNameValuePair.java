/*
 * ==================================================================== Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.grendelscan.commons.http.apache_overrides.serializable;

import java.io.Serializable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

/**
 * Based on BasicNameValuePair in Apache
 * 
 * @author David Byrne
 * 
 */
public class SerializableNameValuePair implements NameValuePair, Cloneable, Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;

    public SerializableNameValuePair()
    {
    }

    /**
     * Default Constructor taking a name and a value. The value may be null, but will be converted to an empty string.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    public SerializableNameValuePair(final String name, final String value)
    {
        super();
        if (name == null)
        {
            throw new IllegalArgumentException("Name may not be null");
        }
        if (value == null)
        {
            this.value = "";
        }
        else
        {
            this.value = value;
        }
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object == null)
        {
            return false;
        }
        if (this == object)
        {
            return true;
        }
        if (object instanceof NameValuePair)
        {
            BasicNameValuePair that = (BasicNameValuePair) object;
            return name.equals(that.getName()) && LangUtils.equals(value, that.getValue());
        }
        return false;
    }

    /**
     * Returns the name.
     * 
     * @return String name The name
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Returns the value.
     * 
     * @return String value The current value.
     */
    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, name);
        hash = LangUtils.hashCode(hash, value);
        return hash;
    }

    public final void setName(final String name)
    {
        this.name = name;
    }

    public final void setValue(final String value)
    {
        this.value = value;
    }

    /**
     * Get a string representation of this pair.
     * 
     * @return A string representation.
     */
    @Override
    public String toString()
    {
        // don't call complex default formatting for a simple toString

        int len = name.length();
        if (value != null)
        {
            len += 1 + value.length();
        }
        CharArrayBuffer buffer = new CharArrayBuffer(len);

        buffer.append(name);
        if (value != null)
        {
            buffer.append("=");
            buffer.append(value);
        }
        return buffer.toString();
    }

}
