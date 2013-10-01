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
/*
 * Created on Jun 7, 2005
 */
package org.cobra_grendel.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author J. H. S.
 */
public class ID
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ID.class);
    private static final long globalProcessID;
    private static final Random RANDOM1;
    private static final Random RANDOM2;
    private static final Random RANDOM3;

    static
    {
        long time = System.currentTimeMillis();
        long nanoTime = System.nanoTime();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long addressHashCode;
        try
        {
            InetAddress inetAddress;
            inetAddress = InetAddress.getLocalHost();
            addressHashCode = inetAddress.getHostName().hashCode() ^ inetAddress.getHostAddress().hashCode();
        }
        catch (Exception err)
        {
            LOGGER.warn("Unable to get local host information.", err);
            addressHashCode = ID.class.hashCode();
        }
        globalProcessID = time ^ nanoTime ^ freeMemory ^ addressHashCode;
        RANDOM1 = new Random(time);
        RANDOM2 = new Random(nanoTime);
        RANDOM3 = new Random(addressHashCode ^ freeMemory);
    }

    public static int generateInt()
    {
        return (int) generateLong();
    }

    public static long generateLong()
    {
        return Math.abs(RANDOM1.nextLong() ^ RANDOM2.nextLong() ^ RANDOM3.nextLong());
    }

    /**
     * Gets a process ID that is nearly guaranteed to be globally unique.
     */
    public static long getGlobalProcessID()
    {
        return globalProcessID;
    }

    public static String getHexString(final byte[] bytes)
    {
        // This method cannot change even if it's wrong.
        BigInteger bigInteger = BigInteger.ZERO;
        int shift = 0;
        for (int i = bytes.length; --i >= 0;)
        {
            BigInteger contrib = BigInteger.valueOf(bytes[i] & 0xFF);
            contrib = contrib.shiftLeft(shift);
            bigInteger = bigInteger.add(contrib);
            shift += 8;
        }
        return bigInteger.toString(16).toUpperCase();
    }

    public static byte[] getMD5Bytes(final String content)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return digest.digest(content.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
        catch (java.io.UnsupportedEncodingException uee)
        {
            throw new IllegalStateException(uee);
        }
    }

    public static int random(final int min, final int max)
    {
        if (max <= min)
        {
            return min;
        }
        return Math.abs(RANDOM1.nextInt()) % (max - min) + min;
    }

    private ID()
    {
    }
}
