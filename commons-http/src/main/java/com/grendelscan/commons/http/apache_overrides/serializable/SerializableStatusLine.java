package com.grendelscan.commons.http.apache_overrides.serializable;

import java.io.Serializable;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicLineFormatter;

public class SerializableStatusLine implements Serializable, StatusLine, Cloneable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -4052124223049114377L;

    /** The protocol version. */
    private final ProtocolVersion protoVersion;

    /** The reason phrase. */
    private final String reasonPhrase;

    /** The status code. */
    private final int statusCode;

    public SerializableStatusLine()
    {
        this(null, 0, null);
    }

    public SerializableStatusLine(final ProtocolVersion version, final int statusCode, final String reasonPhrase)
    {
        if (version == null)
        {
            throw new IllegalArgumentException("Protocol version may not be null.");
        }
        if (statusCode < 0)
        {
            throw new IllegalArgumentException("Status code may not be negative.");
        }
        protoVersion = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public SerializableStatusLine(final StatusLine statusLine)
    {
        this(statusLine.getProtocolVersion(), statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }

    // --------------------------------------------------------- Public Methods

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * @return the HTTP-Version
     */
    @Override
    public ProtocolVersion getProtocolVersion()
    {
        return protoVersion;
    }

    /**
     * @return the Reason-Phrase
     */
    @Override
    public String getReasonPhrase()
    {
        return reasonPhrase;
    }

    /**
     * @return the Status-Code
     */
    @Override
    public int getStatusCode()
    {
        return statusCode;
    }

    @Override
    public String toString()
    {
        // no need for non-default formatting in toString()
        return BasicLineFormatter.DEFAULT.formatStatusLine(null, this).toString();
    }

}
