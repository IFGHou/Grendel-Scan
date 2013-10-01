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

import java.io.IOException;
import java.io.OutputStream;

import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.MessageSerializer;
import flex.messaging.io.SerializationContext;

/**
 * This class can serialize messages to an output stream.
 * 
 * <p>
 * Multiple messages can be written to the same stream.
 * </p>
 * 
 * @author Simeon Simeonov (simeons@macromedia.com)
 * @see ActionMessage
 * @exclude
 */
public class AmfMessageSerializer implements MessageSerializer
{
    /**
     * Special content length value that indicates "unknown" content length.
     */
    public static final int UNKNOWN_CONTENT_LENGTH = -1;

    /**
     * Legacy AMF data serializer.
     */
    protected Amf0Output amfOut;

    /*
     * DEBUG LOGGING
     */
    protected boolean isDebug;
    protected AmfTrace debugTrace;
    protected int version;

    /**
     * Creates an AMF message serializer (without hooking it up to an output stream).
     */
    public AmfMessageSerializer()
    {
    }

    /**
     * @param value
     *            - The default version of AMF encoding to be used.
     */
    @Override
    public void setVersion(int value)
    {
        version = value;
    }

    /**
     * Establishes the context for writing out data to the given OutputStream. A null value can be passed for the trace parameter if a record of the AMF data should not be made.
     * 
     * @param context
     *            The SerializationContext specifying the custom options.
     * @param out
     *            The OutputStream to write out the AMF data.
     * @param trace
     *            If not null, turns on "trace" debugging for AMF responses.
     */
    @Override
    public void initialize(SerializationContext context, OutputStream out, AmfTrace trace)
    {
        // We start with the legacy encoding format for any version.
        // On encountering a complex type, DataOutput will delegate to
        // the new ObjectOutput class for AMF version 3.
        amfOut = new Amf0Output(context);
        amfOut.setOutputStream(out);
        amfOut.setAvmPlus(version >= MessageIOConstants.AMF3);

        debugTrace = trace;
        isDebug = trace != null;
        amfOut.setDebugTrace(debugTrace);
    }

    /**
     * Serializes a message to the output stream.
     * 
     * @param m
     *            message to serialize
     * @throws IOException
     */
    @Override
    public void writeMessage(ActionMessage m) throws IOException
    {
        if (isDebug)
            debugTrace.startResponse("Serializing AMF/HTTP response");

        int version = m.getVersion();

        amfOut.setAvmPlus(version >= MessageIOConstants.AMF3);

        // Write packet header
        amfOut.writeShort(version);

        if (isDebug)
            debugTrace.version(version);

        // Write out headers
        int headerCount = m.getHeaderCount();
        amfOut.writeShort(headerCount);
        for (int i = 0; i < headerCount; ++i)
        {
            MessageHeader header = m.getHeader(i);

            if (isDebug)
                debugTrace.startHeader(header.getName(), header.getMustUnderstand(), i);

            writeHeader(header);

            if (isDebug)
                debugTrace.endHeader();
        }

        // Write out the bodies
        int bodyCount = m.getBodyCount();
        amfOut.writeShort(bodyCount);
        for (int i = 0; i < bodyCount; ++i)
        {
            MessageBody body = m.getBody(i);

            if (isDebug)
                debugTrace.startMessage(body.getTargetURI(), body.getResponseURI(), i);

            writeBody(body);

            if (isDebug)
                debugTrace.endMessage();
        }
    }

    /**
     * Serializes a message header to the output stream.
     * 
     * @param h
     *            header to serialize
     * @throws IOException
     *             if write fails.
     */
    public void writeHeader(MessageHeader h) throws IOException
    {
        amfOut.writeUTF(h.getName());
        amfOut.writeBoolean(h.getMustUnderstand());
        amfOut.writeInt(UNKNOWN_CONTENT_LENGTH);
        amfOut.reset();
        writeObject(h.getData());
    }

    /**
     * Serializes a message body to the output stream.
     * 
     * @param b
     *            body to serialize
     * @throws IOException
     *             if write fails.
     */
    public void writeBody(MessageBody b) throws IOException
    {
        if (b.getTargetURI() == null)
            amfOut.writeUTF("null");
        else
            amfOut.writeUTF(b.getTargetURI());

        if (b.getResponseURI() == null)
            amfOut.writeUTF("null");
        else
            amfOut.writeUTF(b.getResponseURI());

        amfOut.writeInt(UNKNOWN_CONTENT_LENGTH);
        amfOut.reset();

        Object data = b.getData();
        writeObject(data);
    }

    /**
     * Serializes an Object directly to the output stream.
     * 
     * @param value
     *            - the Object to write to the AMF stream.
     */
    @Override
    public void writeObject(Object value) throws IOException
    {
        amfOut.writeObject(value);
    }
}
