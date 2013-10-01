package com.grendelscan.commons.http;

/*
 * ====================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpec;

import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;

/**
 * Based on the BasicCookieStore from Apache
 * 
 */
public class CookieJar implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -6464787688205012752L;

    private transient static final Comparator<Cookie> cookieComparator = new CookieIdentityComparator();

    public static CookieSpec getCookieSpec()
    {
        return cookieSpec;
    }

    private final ArrayList<SerializableBasicCookie> cookies;

    private transient final static CookieSpec cookieSpec = new BrowserCompatSpec();

    // public int getCookieOriginTransaction(Cookie cookie)
    // {
    // return cookies.get(cookie);
    // }

    public CookieJar()
    {
        cookies = new ArrayList<SerializableBasicCookie>(1);
    }

    public synchronized void addCookie(final Cookie cookie)
    {
        addCookie(new SerializableBasicCookie(cookie));
    }

    public synchronized void addCookie(final SerializableBasicCookie cookie)
    {
        if (cookie != null)
        {
            Set<Cookie> tmpCookies = new HashSet<Cookie>(cookies);
            // first remove any old cookie that is equivalent
            for (Cookie comp : tmpCookies)
            {
                boolean match;
                synchronized (cookieComparator)
                {
                    match = cookieComparator.compare(cookie, comp) == 0;
                }
                if (match)
                {
                    cookies.remove(comp);
                    break;
                }
            }
            if (!cookie.isExpired(new Date()))
            {
                cookies.add(cookie);
            }
        }
    }

    public void addCookies(final CookieJar jar)
    {
        cookies.addAll(jar.getCookies());
    }

    /**
     * Clears all cookies.
     */
    public synchronized void clear()
    {
        cookies.clear();
    }

    /**
     * Removes all of {@link Cookie cookies} in this HTTP state that have expired by the specified {@link java.util.Date date}.
     * 
     * @return true if any cookies were purged.
     * 
     * @see Cookie#isExpired(Date)
     */
    public synchronized boolean clearExpired(final Date date)
    {
        if (date == null)
        {
            return false;
        }
        boolean removed = false;
        Set<Cookie> keys = new HashSet<Cookie>(cookies);
        for (Cookie cookie : keys)
        {
            if (cookie.isExpired(date))
            {
                cookies.remove(cookie);
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public CookieJar clone()
    {
        CookieJar clone = new CookieJar();
        for (SerializableBasicCookie cookie : cookies)
        {

            clone.addCookie(cookie);
            cookie.getClass();
        }
        return clone;
    }

    /**
     * Returns an immutable array of {@link Cookie cookies} that this HTTP state currently contains.
     * 
     * @return an array of {@link Cookie cookies}.
     */
    public List<SerializableBasicCookie> getCookies()
    {
        return Collections.unmodifiableList(new ArrayList<SerializableBasicCookie>(cookies));
    }

    public List<Cookie> getMatchingCookies(final CookieOrigin origin)
    {
        List<Cookie> c = new ArrayList<Cookie>();

        for (Cookie cookie : getCookies())
        {
            if (cookieSpec.match(cookie, origin))
            {
                c.add(cookie);
            }
        }
        return c;
    }

    public List<Cookie> getMatchingCookies(final URL url)
    {
        int port = url.getPort();
        if (port < 0)
        {
            port = url.getDefaultPort();
        }
        CookieOrigin origin = new CookieOrigin(url.getHost(), port, url.getPath(), url.getProtocol().equalsIgnoreCase("https"));
        return getMatchingCookies(origin);
    }

    public synchronized void removeCookie(final Cookie cookie)
    {
        if (cookie != null)
        {
            cookies.remove(cookie);
        }
    }

    public synchronized void removeCookie(final String cookieName, final String cookieDomain)
    {
        if (cookieName != null & cookieDomain != null)
        {
            for (SerializableBasicCookie cookie : cookies)
            {
                if (cookie.getName().equals(cookieName) && cookie.getDomain().equalsIgnoreCase(cookieDomain))
                {
                    cookies.remove(cookie);
                    break;
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return cookies.toString();
    }

    // @Override
    // public void addCookie(Cookie cookie)
    // {
    // addCookie(cookie, 0);
    // }

}
