package com.grendelscan.commons.http.apache_overrides.serializable;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicLineFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializableHttpHeader implements Header, Serializable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableHttpHeader.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5590741863734703260L;
	
	private final String name;
	
	/**
	 * Header value.
	 */
	private final String value;
	
	public SerializableHttpHeader()
	{
		this(null, null);
	}
	
	public SerializableHttpHeader(Header header)
	{
		this(header.getName(), header.getValue());
	}

	public SerializableHttpHeader(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
	
	@Override
	public SerializableHttpHeader clone()
	{
		return new SerializableHttpHeader(new String(name), new String(value));
	}
	
	/**
	 * Returns an array of {@link HeaderElement}s constructed from my value.
	 * 
	 * @see BasicHeaderValueParser#parseElements
	 * 
	 * @return an array of header elements
	 * 
	 * @throws ParseException
	 *             in case of a parse error
	 */
	@Override
	public HeaderElement[] getElements() throws ParseException
	{
		if (value != null)
		{
			// result intentionally not cached, it's probably not used again
			return BasicHeaderValueParser.parseElements(value, null);
		}
		else
		{
			return new HeaderElement[] {};
		}
	}
	
	/**
	 * Returns the header name.
	 * 
	 * @return String name The name
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the header value.
	 * 
	 * @return String value The current value.
	 */
	@Override
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Returns a {@link String} representation of the header.
	 * 
	 * @return a string
	 */
	@Override
	public String toString()
	{
		// no need for non-default formatting in toString()
		return BasicLineFormatter.DEFAULT.formatHeader(null, this).toString();
	}
	
	public byte[] getBytes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			out.write(name.getBytes());
			out.write(':');
			out.write(' ');
			out.write(value.getBytes());
		}
		catch (IOException e)
		{
			LOGGER.error("Weird problem getting bytes for header: " + e.toString(), e);
		}
		return out.toByteArray();
	}
	
	public static SerializableHttpHeader convertToSerializableHeader(Header header)
	{
		SerializableHttpHeader basic = null;
		if (header instanceof SerializableHttpHeader)
		{
			basic = (SerializableHttpHeader) header;
		}
		else if (header != null)
		{
			basic = new SerializableHttpHeader(header.getName(), header.getValue());
		}
		return basic;
	}

}
