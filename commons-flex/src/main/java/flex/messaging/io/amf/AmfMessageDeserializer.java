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
import java.io.InputStream;

import flex.messaging.MessageException;
import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.RecoverableSerializationException;
import flex.messaging.io.SerializationContext;

/**
 * This class can deserialize messages from an input stream Multiple messages can be read from the same stream.
 * 
 * @author Simeon Simeonov (simeons@macromedia.com)
 * @author Peter Farland (pfarland@macromedia.com)
 * @see ActionMessage
 * @exclude
 */
public class AmfMessageDeserializer implements MessageDeserializer
{
    private static final int UNSUPPORTED_AMF_VERSION = 10310;

    /**
     * Stream used for AMF data types.
     */
    protected ActionMessageInput amfIn;

    /*
     * DEBUG LOGGING.
     */
    protected AmfTrace debugTrace;
    protected boolean isDebug;

    /**
     * Creates a new AMF message deserializer (without hooking it up to an input stream).
     */
    public AmfMessageDeserializer()
    {
    }

    /**
     * Establishes the context for reading in data from the given InputStream. A null value can be passed for the trace parameter if a record of the AMF data should not be made.
     */
    @Override
    public void initialize(SerializationContext context, InputStream in, AmfTrace trace)
    {
        amfIn = new Amf0Input(context);
        amfIn.setInputStream(in);

        debugTrace = trace;
        isDebug = debugTrace != null;
        amfIn.setDebugTrace(debugTrace);
    }

    /**
     * Deserializes a message from the input stream.
     * 
     * @param m
     *            - holds the deserialized message
     * @throws IOException
     *             thrown by the underlying stream
     */
    @Override
    public void readMessage(ActionMessage m, ActionContext context) throws ClassNotFoundException, IOException
    {
        if (isDebug)
            debugTrace.startRequest("Deserializing AMF/HTTP request");

        // Read packet header
        int version = amfIn.readUnsignedShort();

        // Treat FMS's AMF1 as AMF0.
        if (version == MessageIOConstants.AMF1)
            version = MessageIOConstants.AMF0;

        if (version != MessageIOConstants.AMF0 && version != MessageIOConstants.AMF3)
        {
            // Unsupported AMF version {version}.
            MessageException ex = new MessageException();
            ex.setMessage(UNSUPPORTED_AMF_VERSION, new Object[] { new Integer(version) });
            ex.setCode("VersionMismatch");
            throw ex;
        }

        m.setVersion(version);
        context.setVersion(version);

        if (isDebug)
            debugTrace.version(version);

        // Read headers
        int headerCount = amfIn.readUnsignedShort();
        for (int i = 0; i < headerCount; ++i)
        {
            MessageHeader header = new MessageHeader();
            m.addHeader(header);
            readHeader(header, i);
        }

        // Read bodies
        int bodyCount = amfIn.readUnsignedShort();
        for (int i = 0; i < bodyCount; ++i)
        {
            MessageBody body = new MessageBody();
            m.addBody(body);
            readBody(body, i);
        }
    }

    /**
     * Deserialize a message header from the input stream. A message header is structured as: NAME kString MUST UNDERSTAND kBoolean LENGTH kInt DATA kObject
     * 
     * @param header
     *            - will hold the deserialized message header
     * @param index
     *            header index for debugging
     * @throws IOException
     *             thrown by the underlying stream
     * @throws ClassNotFoundException
     *             if we don't find the class for the header data.
     */
    public void readHeader(MessageHeader header, int index) throws ClassNotFoundException, IOException
    {
        String name = amfIn.readUTF();
        header.setName(name);
        boolean mustUnderstand = amfIn.readBoolean();
        header.setMustUnderstand(mustUnderstand);

        amfIn.readInt(); // Length

        amfIn.reset();
        Object data;

        if (isDebug)
            debugTrace.startHeader(name, mustUnderstand, index);

        try
        {
            data = readObject();
        }
        catch (RecoverableSerializationException ex)
        {
            ex.setCode("Client.Header.Encoding");
            data = ex;
        }
        catch (MessageException ex)
        {
            ex.setCode("Client.Header.Encoding");
            throw ex;
        }

        header.setData(data);

        if (isDebug)
            debugTrace.endHeader();
    }

    /**
     * Deserialize a message body from the input stream.
     * 
     * @param body
     *            - will hold the deserialized message body
     * @param index
     *            message index for debugging
     * @throws IOException
     *             thrown by the underlying stream
     * @throws ClassNotFoundException
     *             if we don't find the class for the body data.
     */
    public void readBody(MessageBody body, int index) throws ClassNotFoundException, IOException
    {
        String targetURI = amfIn.readUTF();
        body.setTargetURI(targetURI);
        String responseURI = amfIn.readUTF();
        body.setResponseURI(responseURI);

        amfIn.readInt(); // Length

        amfIn.reset();
        Object data;

        if (isDebug)
            debugTrace.startMessage(targetURI, responseURI, index);

        try
        {
            data = readObject();
        }
        catch (RecoverableSerializationException ex)
        {
            ex.setCode("Client.Message.Encoding");
            data = ex;
        }
        catch (MessageException ex)
        {
            ex.setCode("Client.Message.Encoding");
            throw ex;
        }

        body.setData(data);

        if (isDebug)
            debugTrace.endMessage();
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException
    {
        return amfIn.readObject();
    }
}
