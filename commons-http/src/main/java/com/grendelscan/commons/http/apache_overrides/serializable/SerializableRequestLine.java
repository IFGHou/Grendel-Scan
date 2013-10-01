package com.grendelscan.commons.http.apache_overrides.serializable;

import java.io.Serializable;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicLineFormatter;

public class SerializableRequestLine implements RequestLine, Cloneable, Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String method;
    private ProtocolVersion protoversion;
    private String uri;

    private SerializableRequestLine()
    {
    }

    public SerializableRequestLine(final RequestLine requestline)
    {
        this(requestline.getMethod(), requestline.getUri(), requestline.getProtocolVersion());
    }

    public SerializableRequestLine(final String method, final String uri, final ProtocolVersion version)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("Method must not be null.");
        }
        if (uri == null)
        {
            throw new IllegalArgumentException("URI must not be null.");
        }
        if (version == null)
        {
            throw new IllegalArgumentException("Protocol version must not be null.");
        }
        this.method = method;
        this.uri = uri;
        protoversion = version;
    }

    @Override
    public SerializableRequestLine clone()
    {
        SerializableRequestLine clone = new SerializableRequestLine();
        clone.method = method;
        clone.uri = uri;
        clone.protoversion = protoversion; // ProtocolVersion is immutable
        return clone;
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public ProtocolVersion getProtocolVersion()
    {
        return protoversion;
    }

    @Override
    public String getUri()
    {
        return uri;
    }

    @Override
    public String toString()
    {
        // no need for non-default formatting in toString()
        return BasicLineFormatter.DEFAULT.formatRequestLine(null, this).toString();
    }

}
