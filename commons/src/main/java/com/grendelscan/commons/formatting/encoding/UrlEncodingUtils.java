/**
 * 
 */
package com.grendelscan.commons.formatting.encoding;

import java.util.BitSet;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * @author david
 * 
 */
public class UrlEncodingUtils
{
    public static final BitSet ENCODE_ALL_CHARS = new BitSet(256);
    protected static final BitSet GOOD_URL_CHARS = new BitSet(256);
    protected static final BitSet GOOD_URL_PARAM_CHARS;

    static
    {
        GOOD_URL_CHARS.flip(0, 255); // default to good

        for (int i = 0; i <= 19; i++)
        {
            GOOD_URL_CHARS.set(i, false);
        }

        for (int i = 127; i <= 255; i++)
        {
            GOOD_URL_CHARS.set(i, false);
        }

        GOOD_URL_CHARS.set('{', false);
        GOOD_URL_CHARS.set('}', false);
        GOOD_URL_CHARS.set('\\', false);
        GOOD_URL_CHARS.set('"', false);
        GOOD_URL_CHARS.set('\'', false);
        GOOD_URL_CHARS.set('`', false);
        GOOD_URL_CHARS.set('^', false);
        GOOD_URL_CHARS.set('#', false);
        GOOD_URL_CHARS.set('|', false);
        GOOD_URL_CHARS.set('[', false);
        GOOD_URL_CHARS.set(']', false);
        GOOD_URL_CHARS.set('(', false);
        GOOD_URL_CHARS.set(')', false);
        GOOD_URL_CHARS.set('<', false);
        GOOD_URL_CHARS.set('>', false);

        GOOD_URL_PARAM_CHARS = (BitSet) GOOD_URL_CHARS.clone();
        GOOD_URL_PARAM_CHARS.set('=', false);
        GOOD_URL_PARAM_CHARS.set('&', false);
    }

    public static byte[] decodeUrl(final byte[] bytes)
    {
        if (bytes == null || bytes.length == 0)
        {
            return bytes;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++)
        {
            int b = bytes[i];
            if (b == '+')
            {
                buffer.write(' ');
            }
            else if (b == '%' && i + 2 < bytes.length)
            {
                int u = Character.digit(bytes[++i], 16);
                int l = Character.digit(bytes[++i], 16);
                if (u >= 0 && l >= 0)
                {
                    buffer.write((char) ((u << 4) + l));
                }
                else
                // This wasn't a valid hex code, just ignore it
                {
                    buffer.write('%');
                    buffer.write(bytes[i - 1]);
                    buffer.write(bytes[i]);
                }
            }
            else
            {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    public static String decodeUrl(final String string)
    {
        return new String(decodeUrl(string.getBytes()));
    }

    public static byte[] encodeAllChars(final byte[] bytes)
    {
        return URLCodec.encodeUrl(ENCODE_ALL_CHARS, bytes);
    }

    public static byte[] encodeDefaultChars(final byte[] bytes)
    {
        return URLCodec.encodeUrl(GOOD_URL_CHARS, bytes);
    }

    public static byte[] encodeForParam(final byte[] bytes)
    {
        return URLCodec.encodeUrl(GOOD_URL_PARAM_CHARS, bytes);
    }

    public static String encodeForParam(final String string)
    {
        return new String(URLCodec.encodeUrl(GOOD_URL_PARAM_CHARS, string.getBytes()));
    }

}
