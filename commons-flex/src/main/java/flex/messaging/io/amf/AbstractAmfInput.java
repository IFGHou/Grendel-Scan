/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.io.amf;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import flex.messaging.io.BeanProxy;
import flex.messaging.io.SerializationContext;
import flex.messaging.util.XMLUtil;

/**
 * A deserializer of AMF protocol data.
 * 
 * @author Peter Farland (pfarland@macromedia.com)
 * 
 * @see ActionMessageOutput
 * @exclude
 */
public abstract class AbstractAmfInput extends AmfIO implements ActionMessageInput
{
    protected BeanProxy beanProxy = new BeanProxy();

    protected DataInputStream in = null;

    /**
     * Construct a deserializer without connecting it to an input stream.
     * 
     * @param context
     *            serialization parameters.
     */
    public AbstractAmfInput(SerializationContext context)
    {
        super(context);
    }

    /**
     * Internal use
     * 
     * @exclude
     */
    @Override
    public void setInputStream(InputStream in)
    {
        this.in = new DataInputStream(in);
    }

    protected Object stringToDocument(String xml)
    {
        // FIXME: Temporary workaround for bug 194815
        if (xml != null && xml.indexOf('<') == -1)
            return xml;

        return XMLUtil.stringToDocument(xml, !(context.legacyXMLNamespaces));
    }

    //
    // java.io.ObjectInput IMPLEMENTATIONS
    //

    /** {@inheritDoc} */
    @Override
    public int available() throws IOException
    {
        return in.available();
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException
    {
        in.close();
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException
    {
        return in.read();
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] bytes) throws IOException
    {
        return in.read(bytes);
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException
    {
        return in.read(bytes, offset, length);
    }

    /** {@inheritDoc} */
    @Override
    public long skip(long n) throws IOException
    {
        return in.skip(n);
    }

    /** {@inheritDoc} */
    @Override
    public int skipBytes(int n) throws IOException
    {
        return in.skipBytes(n);
    }

    //
    // java.io.DataInput IMPLEMENTATIONS
    //

    /** {@inheritDoc} */
    @Override
    public boolean readBoolean() throws IOException
    {
        return in.readBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public byte readByte() throws IOException
    {
        return in.readByte();
    }

    /** {@inheritDoc} */
    @Override
    public char readChar() throws IOException
    {
        return in.readChar();
    }

    /** {@inheritDoc} */
    @Override
    public double readDouble() throws IOException
    {
        return in.readDouble();
    }

    /** {@inheritDoc} */
    @Override
    public float readFloat() throws IOException
    {
        return in.readFloat();
    }

    /** {@inheritDoc} */
    @Override
    public void readFully(byte[] bytes) throws IOException
    {
        in.readFully(bytes);
    }

    /** {@inheritDoc} */
    @Override
    public void readFully(byte[] bytes, int offset, int length) throws IOException
    {
        in.readFully(bytes, offset, length);
    }

    /** {@inheritDoc} */
    @Override
    public int readInt() throws IOException
    {
        return in.readInt();
    }

    /**
     * Reads the next line of text from the input stream.
     * 
     * @deprecated
     */
    @Override
    public String readLine() throws IOException
    {
        return in.readLine();
    }

    /** {@inheritDoc} */
    @Override
    public long readLong() throws IOException
    {
        return in.readLong();
    }

    /** {@inheritDoc} */
    @Override
    public short readShort() throws IOException
    {
        return in.readShort();
    }

    /** {@inheritDoc} */
    @Override
    public int readUnsignedByte() throws IOException
    {
        return in.readUnsignedByte();
    }

    /** {@inheritDoc} */
    @Override
    public int readUnsignedShort() throws IOException
    {
        return in.readUnsignedShort();
    }

    /** {@inheritDoc} */
    @Override
    public String readUTF() throws IOException
    {
        return in.readUTF();
    }
}
