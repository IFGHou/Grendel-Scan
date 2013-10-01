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
package flex.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;
import flex.messaging.util.UUIDUtils;

/**
 * This is the default implementation of Message, which provides a convenient base for behavior and associations common to all endpoints.
 * 
 * @author neville
 * @exclude
 */
public abstract class AbstractMessage implements Message, Cloneable
{
    /**
     * This number was generated using the 'serialver' command line tool. This number should remain consistent with the version used by ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = -834697863344344313L;

    // Serialization constants
    private static final short HAS_NEXT_FLAG = 128;
    private static final short BODY_FLAG = 1;
    private static final short CLIENT_ID_FLAG = 2;
    private static final short DESTINATION_FLAG = 4;
    private static final short HEADERS_FLAG = 8;
    private static final short MESSAGE_ID_FLAG = 16;
    private static final short TIMESTAMP_FLAG = 32;
    private static final short TIME_TO_LIVE_FLAG = 64;
    private static final short CLIENT_ID_BYTES_FLAG = 1;
    private static final short MESSAGE_ID_BYTES_FLAG = 2;

    protected Object clientId;
    protected String destination;
    protected String messageId;
    protected long timestamp;
    protected long timeToLive;

    protected Map headers;
    protected Object body;

    private byte[] clientIdBytes;
    private byte[] messageIdBytes;

    @Override
    public Object getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(Object clientId)
    {
        this.clientId = clientId;
        clientIdBytes = null;
    }

    @Override
    public String getMessageId()
    {
        return messageId;
    }

    @Override
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
        messageIdBytes = null;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public long getTimeToLive()
    {
        return timeToLive;
    }

    @Override
    public void setTimeToLive(long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    @Override
    public Object getBody()
    {
        return body;
    }

    @Override
    public void setBody(Object body)
    {
        this.body = body;
    }

    @Override
    public String getDestination()
    {
        return destination;
    }

    @Override
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    @Override
    public Map getHeaders()
    {
        if (headers == null)
        {
            headers = new HashMap();
        }
        return headers;
    }

    @Override
    public void setHeaders(Map newHeaders)
    {
        for (Iterator iter = newHeaders.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String propName = (String) entry.getKey();
            setHeader(propName, entry.getValue());
        }
    }

    @Override
    public Object getHeader(String headerName)
    {
        if (headers == null)
        {
            return null;
        }
        return headers.get(headerName);
    }

    @Override
    public void setHeader(String headerName, Object value)
    {
        if (headers == null)
        {
            headers = new HashMap();
        }

        if (value == null)
        {
            headers.remove(headerName);
        }
        else
        {
            headers.put(headerName, value);
        }
    }

    @Override
    public boolean headerExists(String headerName)
    {
        return (headers != null && headers.containsKey(headerName));
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Message)
        {
            if (messageId == null)
                return this == o;

            Message m = (Message) o;
            if (m.getMessageId().equals(this.getMessageId()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        if (messageId == null)
            return super.hashCode();
        return messageId.hashCode();
    }

    @Override
    public String toString()
    {
        return toString(1);
    }

    public String toString(int indent)
    {
        return toStringHeader(indent) + toStringFields(indent + 1);
    }

    /**
     * @exclude
     * 
     *          While this class itself does not implement java.io.Externalizable, SmallMessage implementations will typically use Externalizable to serialize themselves in a smaller form. This method
     *          supports this functionality by implementing Externalizable.readExternal(ObjectInput) to deserialize the properties for this abstract base class.
     */
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException
    {
        short[] flagsArray = readFlags(input);

        for (int i = 0; i < flagsArray.length; i++)
        {
            short flags = flagsArray[i];
            short reservedPosition = 0;

            if (i == 0)
            {
                if ((flags & BODY_FLAG) != 0)
                    body = input.readObject();

                if ((flags & CLIENT_ID_FLAG) != 0)
                    clientId = input.readObject();

                if ((flags & DESTINATION_FLAG) != 0)
                    destination = (String) input.readObject();

                if ((flags & HEADERS_FLAG) != 0)
                    headers = (Map) input.readObject();

                if ((flags & MESSAGE_ID_FLAG) != 0)
                    messageId = (String) input.readObject();

                if ((flags & TIMESTAMP_FLAG) != 0)
                    timestamp = ((Number) input.readObject()).longValue();

                if ((flags & TIME_TO_LIVE_FLAG) != 0)
                    timeToLive = ((Number) input.readObject()).longValue();

                reservedPosition = 7;
            }
            else if (i == 1)
            {
                if ((flags & CLIENT_ID_BYTES_FLAG) != 0)
                {
                    clientIdBytes = (byte[]) input.readObject();
                    clientId = UUIDUtils.fromByteArray(clientIdBytes);
                }

                if ((flags & MESSAGE_ID_BYTES_FLAG) != 0)
                {
                    messageIdBytes = (byte[]) input.readObject();
                    messageId = UUIDUtils.fromByteArray(messageIdBytes);
                }

                reservedPosition = 2;
            }

            // For forwards compatibility, read in any other flagged objects to
            // preserve the integrity of the input stream...
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
     * 
     *          While this class itself does not implement java.io.Externalizable, SmallMessage implementations will typically use Externalizable to serialize themselves in a smaller form. This method
     *          supports this functionality by implementing Externalizable.writeExternal(ObjectOutput) to efficiently serialize the properties for this abstract base class.
     */
    public void writeExternal(ObjectOutput output) throws IOException
    {
        short flags = 0;

        if (clientIdBytes == null && clientId instanceof String)
            clientIdBytes = UUIDUtils.toByteArray((String) clientId);

        if (messageIdBytes == null)
            messageIdBytes = UUIDUtils.toByteArray(messageId);

        if (body != null)
            flags |= BODY_FLAG;

        if (clientId != null && clientIdBytes == null)
            flags |= CLIENT_ID_FLAG;

        if (destination != null)
            flags |= DESTINATION_FLAG;

        if (headers != null)
            flags |= HEADERS_FLAG;

        if (messageId != null && messageIdBytes == null)
            flags |= MESSAGE_ID_FLAG;

        if (timestamp != 0)
            flags |= TIMESTAMP_FLAG;

        if (timeToLive != 0)
            flags |= TIME_TO_LIVE_FLAG;

        if (clientIdBytes != null || messageIdBytes != null)
            flags |= HAS_NEXT_FLAG;

        output.writeByte(flags);

        flags = 0;

        if (clientIdBytes != null)
            flags |= CLIENT_ID_BYTES_FLAG;

        if (messageIdBytes != null)
            flags |= MESSAGE_ID_BYTES_FLAG;

        if (flags != 0)
            output.writeByte(flags);

        if (body != null)
            output.writeObject(body);

        if (clientId != null && clientIdBytes == null)
            output.writeObject(clientId);

        if (destination != null)
            output.writeObject(destination);

        if (headers != null)
            output.writeObject(headers);

        if (messageId != null && messageIdBytes == null)
            output.writeObject(messageId);

        if (timestamp != 0)
            output.writeObject(new Long(timestamp));

        if (timeToLive != 0)
            output.writeObject(new Long(timeToLive));

        if (clientIdBytes != null)
            output.writeObject(clientIdBytes);

        if (messageIdBytes != null)
            output.writeObject(messageIdBytes);
    }

    @Override
    public Object clone()
    {
        AbstractMessage m = null;
        try
        {
            m = (AbstractMessage) super.clone();

            /* NOTE: this is not cloning the body - just the headers */
            if (headers != null)
                m.headers = (HashMap) ((HashMap) headers).clone();
        }
        catch (CloneNotSupportedException exc)
        {
            // can't happen..
        }
        return m;
    }

    static final String[] indentLevels = { "", "  ", "    ", "      ", "        ", "          " };

    protected String getIndent(int indentLevel)
    {
        if (indentLevel < indentLevels.length)
            return indentLevels[indentLevel];
        StringBuffer sb = new StringBuffer();
        sb.append(indentLevels[indentLevels.length - 1]);
        indentLevel -= indentLevels.length - 1;
        for (int i = 0; i < indentLevel; i++)
            sb.append("  ");
        return sb.toString();
    }

    protected String getFieldSeparator(int indentLevel)
    {
        String indStr = getIndent(indentLevel);
        if (indentLevel > 0)
            indStr = StringUtils.NEWLINE + indStr;
        else
            indStr = " ";
        return indStr;
    }

    protected String toStringHeader(int indentLevel)
    {
        String s = "Flex Message";
        s += " (" + getClass().getName() + ") ";
        return s;
    }

    protected String toStringFields(int indentLevel)
    {
        if (headers != null)
        {
            String sep = getFieldSeparator(indentLevel);
            String s = "";
            for (Iterator i = headers.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry e = (Map.Entry) i.next();
                String key = e.getKey().toString();
                s += sep + "hdr(" + key + ") = ";
                if (Log.isExcludedProperty(key))
                    s += Log.VALUE_SUPRESSED;
                else
                    s += bodyToString(e.getValue(), indentLevel + 1);
            }
            return s;
        }
        return "";
    }

    /**
     * This is usually an array so might as well format it nicely in this case.
     */
    protected final String bodyToString(Object body, int indentLevel)
    {
        return bodyToString(body, indentLevel, null);
    }

    /**
     * This is usually an array so might as well format it nicely in this case.
     */
    protected final String bodyToString(Object body, int indentLevel, Map visited)
    {
        try
        {
            indentLevel = indentLevel + 1;
            if (visited == null && indentLevel > 18)
                return StringUtils.NEWLINE + getFieldSeparator(indentLevel) + "<..max-depth-reached..>";
            return internalBodyToString(body, indentLevel, visited);
        }
        catch (RuntimeException exc)
        {
            return "Exception in body toString: " + ExceptionUtil.toString(exc);
        }
    }

    /*
     * TODO UCdetector: Remove unused code: protected String internalBodyToString(Object body, int indentLevel) { return internalBodyToString(body, indentLevel, null); }
     */

    protected String internalBodyToString(Object body, int indentLevel, Map visited)
    {
        if (body instanceof Object[])
        {
            if ((visited = checkVisited(visited, body)) == null)
                return "<--";

            String sep = getFieldSeparator(indentLevel);
            StringBuffer sb = new StringBuffer();
            Object[] arr = (Object[]) body;
            sb.append(getFieldSeparator(indentLevel - 1));
            sb.append("[");
            sb.append(sep);
            for (int i = 0; i < arr.length; i++)
            {
                if (i != 0)
                {
                    sb.append(",");
                    sb.append(sep);
                }
                sb.append(bodyToString(arr[i], indentLevel, visited));
            }
            sb.append(getFieldSeparator(indentLevel - 1));
            sb.append("]");
            return sb.toString();
        }
        // This is here so we can format maps with Object[] as values properly
        // and with the proper indent
        else if (body instanceof Map)
        {
            Map bodyMap = (Map) body;
            StringBuffer buf = new StringBuffer();
            buf.append("{");
            Iterator it = bodyMap.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry e = (Map.Entry) it.next();
                Object key = e.getKey();
                Object value = e.getValue();
                buf.append(key == this ? "(recursive Map as key)" : key);
                buf.append("=");
                if (value == this)
                    buf.append("(recursive Map as value)");
                else if (Log.isExcludedProperty(key.toString()))
                    buf.append(Log.VALUE_SUPRESSED);
                else
                    buf.append(bodyToString(value, indentLevel + 1, visited));

                if (it.hasNext())
                    buf.append(", ");
            }
            buf.append("}");
            return buf.toString();
        }
        else if (body instanceof AbstractMessage)
        {
            return ((AbstractMessage) body).toString(indentLevel);
        }
        else if (body != null)
            return body.toString();
        else
            return "null";
    }

    /**
     * @exclude To support efficient serialization for SmallMessage implementations, this utility method reads in the property flags from an ObjectInput stream. Flags are read in one byte at a time.
     *          Flags make use of sign-extension so that if the high-bit is set to 1 this indicates that another set of flags follows.
     * 
     * @return The array of property flags.
     */
    protected short[] readFlags(ObjectInput input) throws IOException
    {
        boolean hasNextFlag = true;
        short[] flagsArray = new short[2];
        int i = 0;

        while (hasNextFlag)
        {
            short flags = (short) input.readUnsignedByte();
            if (i == flagsArray.length)
            {
                short[] tempArray = new short[i * 2];
                System.arraycopy(flagsArray, 0, tempArray, 0, flagsArray.length);
                flagsArray = tempArray;
            }

            flagsArray[i] = flags;

            if ((flags & HAS_NEXT_FLAG) != 0)
                hasNextFlag = true;
            else
                hasNextFlag = false;

            i++;
        }

        return flagsArray;
    }

    /**
     * Returns a category to use when logging against this message type.
     */
    public String logCategory()
    {
        return LogCategories.MESSAGE_GENERAL;
    }

    private Map checkVisited(Map visited, Object obj)
    {
        if (visited == null)
            visited = new IdentityHashMap();
        else if (visited.get(obj) != null)
            return null;

        visited.put(obj, Boolean.TRUE);

        return visited;
    }

}
