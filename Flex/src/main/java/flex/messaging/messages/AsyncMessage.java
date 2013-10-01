/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import flex.messaging.log.Log;
import flex.messaging.util.UUIDUtils;

/**
 * This type of message contains information necessary to perform
 * point-to-point or publish-subscribe messaging.
 *
 * @author neville
 */
public class AsyncMessage extends AbstractMessage implements SmallMessage
{
    /**
     * This number was generated using the 'serialver' command line tool.
     * This number should remain consistent with the version used by
     * ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = -3549535089417916783L;
    
    /**
     * The name for the subtopic header if the message targets a destination
     * subtopic.
     */
    public static final String SUBTOPIC_HEADER_NAME = "DSSubtopic";

    // Serialization constants
    private static byte CORRELATION_ID_FLAG = 1;
    private static byte CORRELATION_ID_BYTES_FLAG = 2;
    
    protected String correlationId;
    protected byte[] correlationIdBytes;
    
    /**
     * Default constructor for <code>AsyncMessage</code>. 
     */
    public AsyncMessage()
    {
    }

    /**
     * Gets the correlationId of the <code>AsyncMessage</code>.
     * 
     * @return correlationId
     */
    public String getCorrelationId()
    {
        return correlationId;
    }

    /**
     * Sets the correlationId of the <code>AsyncMessage</code>.
     * 
     * @param correlationId The correlationId for the message.
     */
    public void setCorrelationId(String correlationId)
    {
        this.correlationId = correlationId;
    }

    /**
     * @exclude
     */
    @Override public Message getSmallMessage()
    {
        if (getClass() == AsyncMessage.class)
            return new AsyncMessageExt(this);
        return null;
    }

    /**
     * @exclude
     */
    @Override public void readExternal(ObjectInput input)throws IOException, ClassNotFoundException
    {
        super.readExternal(input);

        short[] flagsArray = readFlags(input);
        for (int i = 0; i < flagsArray.length; i++)
        {
            short flags = flagsArray[i];
            short reservedPosition = 0;

            if (i == 0)
            {
                if ((flags & CORRELATION_ID_FLAG) != 0)
                    correlationId = (String)input.readObject();

                if ((flags & CORRELATION_ID_BYTES_FLAG) != 0)
                {
                    correlationIdBytes = (byte[])input.readObject();
                    correlationId = UUIDUtils.fromByteArray(correlationIdBytes);
                }

                reservedPosition = 2;
            }

            // For forwards compatibility, read in any other flagged objects
            // to preserve the integrity of the input stream...
            if ((flags >> reservedPosition) != 0)
            {
                for (short j = reservedPosition; j < 6; j++)
                {
                    if (((flags >> j) & 1) != 0)
                    {
                        input.readObject();
                    }
                }
            }
        }
    }

    /**
     * @exclude
     */
    @Override public void writeExternal(ObjectOutput output) throws IOException
    {
        super.writeExternal(output);

        if (correlationIdBytes == null)
            correlationIdBytes = UUIDUtils.toByteArray(correlationId);

        short flags = 0;

        if (correlationId != null && correlationIdBytes == null)
            flags |= CORRELATION_ID_FLAG;

        if (correlationIdBytes != null)
            flags |= CORRELATION_ID_BYTES_FLAG;

        output.writeByte(flags);

        if (correlationId != null && correlationIdBytes == null)
            output.writeObject(correlationId);

        if (correlationIdBytes != null)
            output.writeObject(correlationIdBytes);
    }

    @Override protected String toStringFields(int indentLevel) 
    {
        String sep = getFieldSeparator(indentLevel);
        String s = sep + "clientId = " + (Log.isExcludedProperty("clientId") ? Log.VALUE_SUPRESSED : clientId);
        s += sep + "correlationId = " + (Log.isExcludedProperty("correlationId") ? Log.VALUE_SUPRESSED : correlationId);
        s += sep + "destination = " + (Log.isExcludedProperty("destination") ? Log.VALUE_SUPRESSED : destination);
        s += sep + "messageId = " + (Log.isExcludedProperty("messageId") ? Log.VALUE_SUPRESSED : messageId);
        s += sep + "timestamp = " + (Log.isExcludedProperty("timestamp") ? Log.VALUE_SUPRESSED : String.valueOf(timestamp));
        s += sep + "timeToLive = " + (Log.isExcludedProperty("timeToLive") ? Log.VALUE_SUPRESSED : String.valueOf(timeToLive));
        s += sep + "body = " + (Log.isExcludedProperty("body") ? Log.VALUE_SUPRESSED : bodyToString(body, indentLevel));
        s += super.toStringFields(indentLevel);
        return s;
    }

}
