/*
 * 
 * * ==================================================================== Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
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
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.util.CharArrayBuffer;

/**
 * Based on BasicClientCookie from Apache
 * 
 * @author David Byrne
 * 
 */
public class SerializableBasicCookie implements SetCookie2, ClientCookie, Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** Cookie attributes as specified by the origin server */
    private Map<String, String> attribs;

    private String commentURL;

    private int[] ports;
    private boolean discard = false;
    /** Comment attribute. */
    private String cookieComment;

    /** Domain attribute. */
    private String cookieDomain;

    /** Expiration {@link Date}. */
    private Date cookieExpiryDate;

    /** Path attribute. */
    private String cookiePath;

    /** The version of the cookie specification I was created from. */
    private int cookieVersion = 0;

    /** My secure flag. */
    private boolean isSecure;

    /** Cookie name */
    private final String name;

    /** Cookie value */
    private String value;

    public SerializableBasicCookie(final Cookie cookie)
    {
        cookieVersion = cookie.getVersion();
        cookieExpiryDate = cookie.getExpiryDate();
        cookiePath = cookie.getPath();
        name = cookie.getName();
        value = cookie.getValue();
        cookieComment = cookie.getComment();
        cookieDomain = cookie.getDomain();
        isSecure = cookie.isSecure();
        ports = cookie.getPorts();

        try
        {
            if (cookie instanceof BasicClientCookie2)
            {
                BasicClientCookie2 b2c = (BasicClientCookie2) cookie;
                discard = b2c.getClass().getField("discard").getBoolean(b2c);
            }
            if (cookie instanceof BasicClientCookie)
            {
                BasicClientCookie bCookie = (BasicClientCookie) cookie;
                Field field = bCookie.getClass().getDeclaredField("attribs");
                field.setAccessible(true);
                attribs = (HashMap) field.get(bCookie);
            }
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        finally
        {
            if (attribs == null)
            {
                attribs = new HashMap<String, String>();
            }
        }
    }

    /**
     * Default Constructor taking a name and a value. The value may be null.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    public SerializableBasicCookie(final String name, final String value)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        attribs = new HashMap<String, String>();
        this.value = value;
    }

    @Override
    public boolean containsAttribute(final String name)
    {
        return attribs.get(name) != null;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null || !(obj instanceof Cookie))
        {
            return false;
        }
        Cookie c = (Cookie) obj;
        boolean goodPorts = false;
        if (ports == null)
        {
            goodPorts = c.getPorts() == null;
        }
        else if (ports.length == c.getPorts().length)
        {
            Set<Integer> p1 = new HashSet<Integer>(ports.length);
            Set<Integer> p2 = new HashSet<Integer>(ports.length);
            for (int i = 0; i < ports.length; i++)
            {
                p1.add(ports[i]);
                p2.add(c.getPorts()[i]);
            }
            goodPorts = p1.equals(p2);
        }
        return goodPorts & cookieDomain.equalsIgnoreCase(c.getDomain()) && cookiePath.equalsIgnoreCase(c.getPath()) && value.equals(c.getValue()) && name.equalsIgnoreCase(c.getName());

    }

    @Override
    public String getAttribute(final String name)
    {
        return attribs.get(name);
    }

    /**
     * Returns the comment describing the purpose of this cookie, or <tt>null</tt> if no such comment has been defined.
     * 
     * @return comment
     * 
     * @see #setComment(String)
     */
    @Override
    public String getComment()
    {
        return cookieComment;
    }

    @Override
    public String getCommentURL()
    {
        return commentURL;
    }

    /**
     * Returns domain attribute of the cookie.
     * 
     * @return the value of the domain attribute
     * 
     * @see #setDomain(java.lang.String)
     */
    @Override
    public String getDomain()
    {
        return cookieDomain;
    }

    /**
     * Returns the expiration {@link Date} of the cookie, or <tt>null</tt> if none exists.
     * <p>
     * <strong>Note:</strong> the object returned by this method is considered immutable. Changing it (e.g. using setTime()) could result in undefined behaviour. Do so at your peril.
     * </p>
     * 
     * @return Expiration {@link Date}, or <tt>null</tt>.
     * 
     * @see #setExpiryDate(java.util.Date)
     * 
     */
    @Override
    public Date getExpiryDate()
    {
        return cookieExpiryDate;
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
     * Returns the path attribute of the cookie
     * 
     * @return The value of the path attribute.
     * 
     * @see #setPath(java.lang.String)
     */
    @Override
    public String getPath()
    {
        return cookiePath;
    }

    @Override
    public int[] getPorts()
    {
        return ports;
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

    /**
     * Returns the version of the cookie specification to which this cookie conforms.
     * 
     * @return the version of the cookie.
     * 
     * @see #setVersion(int)
     * 
     */
    @Override
    public int getVersion()
    {
        return cookieVersion;
    }

    /**
     * Returns true if this cookie has expired.
     * 
     * @param date
     *            Current time
     * 
     * @return <tt>true</tt> if the cookie has expired.
     */
    @Override
    public boolean isExpired(final Date date)
    {
        if (date == null)
        {
            throw new IllegalArgumentException("Date may not be null");
        }
        return discard || cookieExpiryDate != null && cookieExpiryDate.getTime() <= date.getTime();
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * Returns <tt>false</tt> if the cookie should be discarded at the end of the "session"; <tt>true</tt> otherwise.
     * 
     * @return <tt>false</tt> if the cookie should be discarded at the end of the "session"; <tt>true</tt> otherwise
     */
    @Override
    public boolean isPersistent()
    {
        return !discard && null != cookieExpiryDate;
    }

    /**
     * @return <code>true</code> if this cookie should only be sent over secure connections.
     * @see #setSecure(boolean)
     */
    @Override
    public boolean isSecure()
    {
        return isSecure;
    }

    public void setAttribute(final String name, final String value)
    {
        attribs.put(name, value);
    }

    /**
     * If a user agent (web browser) presents this cookie to a user, the cookie's purpose will be described using this comment.
     * 
     * @param comment
     * 
     * @see #getComment()
     */
    @Override
    public void setComment(final String comment)
    {
        cookieComment = comment;
    }

    @Override
    public void setCommentURL(final String commentURL)
    {
        this.commentURL = commentURL;
    }

    @Override
    public void setDiscard(final boolean discard)
    {
        this.discard = discard;
    }

    /**
     * Sets the domain attribute.
     * 
     * @param domain
     *            The value of the domain attribute
     * 
     * @see #getDomain
     */
    @Override
    public void setDomain(final String domain)
    {
        if (domain != null)
        {
            cookieDomain = domain.toLowerCase();
        }
        else
        {
            cookieDomain = null;
        }
    }

    /**
     * Sets expiration date.
     * <p>
     * <strong>Note:</strong> the object returned by this method is considered immutable. Changing it (e.g. using setTime()) could result in undefined behaviour. Do so at your peril.
     * </p>
     * 
     * @param expiryDate
     *            the {@link Date} after which this cookie is no longer valid.
     * 
     * @see #getExpiryDate
     * 
     */
    @Override
    public void setExpiryDate(final Date expiryDate)
    {
        cookieExpiryDate = expiryDate;
    }

    /**
     * Sets the path attribute.
     * 
     * @param path
     *            The value of the path attribute
     * 
     * @see #getPath
     * 
     */
    @Override
    public void setPath(final String path)
    {
        cookiePath = path;
    }

    @Override
    public void setPorts(final int[] ports)
    {
        this.ports = ports;
    }

    /**
     * Sets the secure attribute of the cookie.
     * <p>
     * When <tt>true</tt> the cookie should only be sent using a secure protocol (https). This should only be set when the cookie's originating server used a secure protocol to set the cookie's value.
     * 
     * @param secure
     *            The value of the secure attribute
     * 
     * @see #isSecure()
     */
    @Override
    public void setSecure(final boolean secure)
    {
        isSecure = secure;
    }

    /**
     * Sets the value
     * 
     * @param value
     */
    @Override
    public void setValue(final String value)
    {
        this.value = value;
    }

    /**
     * Sets the version of the cookie specification to which this cookie conforms.
     * 
     * @param version
     *            the version of the cookie.
     * 
     * @see #getVersion
     */
    @Override
    public void setVersion(final int version)
    {
        cookieVersion = version;
    }

    @Override
    public String toString()
    {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append("[version: ");
        buffer.append(Integer.toString(cookieVersion));
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(name);
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(value);
        buffer.append("]");
        buffer.append("[domain: ");
        buffer.append(cookieDomain);
        buffer.append("]");
        buffer.append("[path: ");
        buffer.append(cookiePath);
        buffer.append("]");
        buffer.append("[expiry: ");
        buffer.append(cookieExpiryDate);
        buffer.append("]");
        return buffer.toString();
    }

}
