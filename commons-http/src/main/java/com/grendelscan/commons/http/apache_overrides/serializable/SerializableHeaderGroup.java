package com.grendelscan.commons.http.apache_overrides.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicListHeaderIterator;
import org.apache.http.util.CharArrayBuffer;

/**
 * Based on HeaderGroup from Apache
 * 
 * @author David Byrne
 * 
 */
public class SerializableHeaderGroup implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    /**
     * The list of headers for this group, in the order in which they were added
     */
    private final List<SerializableHttpHeader> headers;

    /**
     * Constructor for HeaderGroup.
     */
    public SerializableHeaderGroup()
    {
        headers = new ArrayList<SerializableHttpHeader>();
    }

    /**
     * Adds the given header to the group. The order in which this header was added is preserved.
     * 
     * @param header
     *            the header to add
     */
    public void addHeader(final Header header)
    {
        if (header == null)
        {
            return;
        }
        headers.add(SerializableHttpHeader.convertToSerializableHeader(header));
    }

    /**
     * Removes any contained headers.
     */
    public void clear()
    {
        headers.clear();
    }

    @Override
    public SerializableHeaderGroup clone()
    {
        SerializableHeaderGroup clone = new SerializableHeaderGroup();
        for (SerializableHttpHeader header : headers)
        {
            clone.addHeader(header.clone());
        }

        return clone;
    }

    /**
     * Tests if headers with the given name are contained within this group.
     * 
     * <p>
     * Header name comparison is case insensitive.
     * 
     * @param name
     *            the header name to test for
     * @return <code>true</code> if at least one header with the name is contained, <code>false</code> otherwise
     */
    public boolean containsHeader(final String name)
    {
        for (int i = 0; i < headers.size(); i++)
        {
            Header header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets all of the headers contained within this group.
     * 
     * @return an array of length >= 0
     */
    public Header[] getAllHeaders()
    {
        return headers.toArray(new Header[headers.size()]);
    }

    /**
     * Gets a header representing all of the header values with the given name. If more that one header with the given name exists the values will be combined with a "," as per RFC 2616.
     * 
     * <p>
     * Header name comparison is case insensitive.
     * 
     * @param name
     *            the name of the header(s) to get
     * @return a header with a condensed value or <code>null</code> if no headers by the given name are present
     */
    public Header getCondensedHeader(final String name)
    {
        Header[] headers = getHeaders(name);

        if (headers.length == 0)
        {
            return null;
        }
        else if (headers.length == 1)
        {
            return headers[0];
        }
        else
        {
            CharArrayBuffer valueBuffer = new CharArrayBuffer(128);
            valueBuffer.append(headers[0].getValue());
            for (int i = 1; i < headers.length; i++)
            {
                valueBuffer.append(", ");
                valueBuffer.append(headers[i].getValue());
            }

            return new BasicHeader(name.toLowerCase(), valueBuffer.toString());
        }
    }

    /**
     * Gets the first header with the given name.
     * 
     * <p>
     * Header name comparison is case insensitive.
     * 
     * @param name
     *            the name of the header to get
     * @return the first header or <code>null</code>
     */
    public Header getFirstHeader(final String name)
    {
        for (int i = 0; i < headers.size(); i++)
        {
            Header header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name))
            {
                return header;
            }
        }
        return null;
    }

    /**
     * Gets all of the headers with the given name. The returned array maintains the relative order in which the headers were added.
     * 
     * <p>
     * Header name comparison is case insensitive.
     * 
     * @param name
     *            the name of the header(s) to get
     * 
     * @return an array of length >= 0
     */
    public Header[] getHeaders(final String name)
    {
        ArrayList<SerializableHttpHeader> headersFound = new ArrayList<SerializableHttpHeader>();

        for (int i = 0; i < headers.size(); i++)
        {
            Header header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name))
            {
                headersFound.add(SerializableHttpHeader.convertToSerializableHeader(header));
            }
        }

        return headersFound.toArray(new Header[headersFound.size()]);
    }

    /**
     * Gets the last header with the given name.
     * 
     * <p>
     * Header name comparison is case insensitive.
     * 
     * @param name
     *            the name of the header to get
     * @return the last header or <code>null</code>
     */
    public Header getLastHeader(final String name)
    {
        // start at the end of the list and work backwards
        for (int i = headers.size() - 1; i >= 0; i--)
        {
            Header header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name))
            {
                return header;
            }
        }

        return null;
    }

    /**
     * Returns an iterator over this group of headers.
     * 
     * @return iterator over this group of headers.
     * 
     * @since 4.0
     */
    public HeaderIterator iterator()
    {
        return new BasicListHeaderIterator(headers, null);
    }

    /**
     * Returns an iterator over the headers with a given name in this group.
     * 
     * @param name
     *            the name of the headers over which to iterate, or <code>null</code> for all headers
     * 
     * @return iterator over some headers in this group.
     * 
     * @since 4.0
     */
    public HeaderIterator iterator(final String name)
    {
        return new BasicListHeaderIterator(headers, name);
    }

    /**
     * Removes the given header.
     * 
     * @param header
     *            the header to remove
     */
    public void removeHeader(final Header header)
    {
        if (header == null)
        {
            return;
        }
        headers.remove(header);
    }

    /**
     * Sets all of the headers contained within this group overriding any existing headers. The headers are added in the order in which they appear in the array.
     * 
     * @param headers
     *            the headers to set
     */
    public void setHeaders(final Header[] headers)
    {
        clear();
        if (headers == null)
        {
            return;
        }
        for (Header header : headers)
        {
            this.headers.add(SerializableHttpHeader.convertToSerializableHeader(header));
        }
    }

    /**
     * Replaces the first occurence of the header with the same name. If no header with the same name is found the given header is added to the end of the list.
     * 
     * @param header
     *            the new header that should replace the first header with the same name if present in the list.
     */
    public void updateHeader(final Header header)
    {
        if (header == null)
        {
            return;
        }
        for (int i = 0; i < headers.size(); i++)
        {
            Header current = headers.get(i);
            if (current.getName().equalsIgnoreCase(header.getName()))
            {
                headers.set(i, SerializableHttpHeader.convertToSerializableHeader(header));
                return;
            }
        }
        headers.add(SerializableHttpHeader.convertToSerializableHeader(header));
    }
}
