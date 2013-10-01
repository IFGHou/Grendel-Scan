/**
 * 
 */
package com.grendelscan.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.http.util.ByteArrayBuffer;

/**
 * @author dbyrne
 *
 */
public class CharsetUtils
{

	private CharsetUtils()
	{
	}

	
	/*
	 * From org.apache.http.entity.mime, but it was private
	 */
	public static byte[] encode(final Charset charset, final String string) 
	{
        ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        byte[] bytes = new byte[encoded.remaining()];
        encoded.get(bytes);
        return bytes;
    }

}
