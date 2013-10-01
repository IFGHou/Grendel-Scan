package com.grendelscan.commons.flex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.w3c.dom.Document;

import com.grendelscan.commons.html.HtmlNodeWriter;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.flex.arrays.AmfAssociativeArrayData;
import com.grendelscan.commons.flex.arrays.AmfBooleanArray;
import com.grendelscan.commons.flex.arrays.AmfByteArray;
import com.grendelscan.commons.flex.arrays.AmfDoubleArray;
import com.grendelscan.commons.flex.arrays.AmfIntArray;
import com.grendelscan.commons.flex.arrays.AmfMessageBodies;
import com.grendelscan.commons.flex.arrays.AmfMessageHeaders;
import com.grendelscan.commons.flex.arrays.AmfObjectArray;
import com.grendelscan.commons.flex.arrays.AmfPrimitiveArray;
import com.grendelscan.commons.flex.complexTypes.AmfASObject;
import com.grendelscan.commons.flex.complexTypes.AmfActionMessageRoot;
import com.grendelscan.commons.flex.complexTypes.AmfBody;
import com.grendelscan.commons.flex.complexTypes.AmfServerSideObject;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfConstants;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.interfaces.AmfGenericObject;
import com.grendelscan.commons.flex.messages.AmfAcknowledgeMessage;
import com.grendelscan.commons.flex.messages.AmfActionMessageHeader;
import com.grendelscan.commons.flex.messages.AmfAsyncMessage;
import com.grendelscan.commons.flex.messages.AmfCommandMessage;
import com.grendelscan.commons.flex.messages.AmfErrorMessage;
import com.grendelscan.commons.flex.messages.AmfOperation;
import com.grendelscan.commons.flex.messages.AmfRemotingMessage;

import flex.messaging.MessageException;
import flex.messaging.io.amf.ASObject;
import flex.messaging.messages.AcknowledgeMessage;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.ErrorMessage;
import flex.messaging.messages.RemotingMessage;

public class AmfUtils
{
    /**
     * The maximum value for an <code>int</code> that will avoid promotion to an ActionScript Number when sent via AMF 3 is 2<sup>28</sup> - 1, or <code>0x0FFFFFFF</code>.
     */
    public final static int INT28_MAX_VALUE = 0x0FFFFFFF; // 2^28 - 1

    /**
     * The minimum value for an <code>int</code> that will avoid promotion to an ActionScript Number when sent via AMF 3 is -2<sup>28</sup> or <code>0xF0000000</code>.
     */
    public final static int INT28_MIN_VALUE = 0xF0000000; // -2^28 in 2^29 scheme

    /**
     * Internal use only.
     * 
     * @exclude
     */
    private static int UINT29_MASK = 0x1FFFFFFF; // 2^29 - 1

    public static AbstractAmfData amfFactory(final AmfDataType type, final AbstractAmfDataContainer<?> parent, final DataReference reference, final int transactionId, final AmfActionMessageRoot amfRoot) throws IllegalArgumentException
    {
        switch (type)
        {
            case kActionMessageHeader:
                return new AmfActionMessageHeader("", parent, transactionId, amfRoot);
            case kAmfAsyncMessage:
                return new AmfAsyncMessage("", false, parent, transactionId);
            case kAmfBody:
                return new AmfBody("", "", parent, transactionId, amfRoot);
            case kAmfCommandMessage:
                return new AmfCommandMessage("", false, parent, transactionId, amfRoot);
            case kAmfMessage:
                throw new IllegalArgumentException("Can't instantiate AmfMessage (it's abstract)");
            case kAmfMessageBodies:
                return new AmfMessageBodies(parent, transactionId, amfRoot);
            case kAmfMessageHeaders:
                return new AmfMessageHeaders(parent, transactionId, amfRoot);
            case kAmfMessageRoot:
                return new AmfActionMessageRoot(3, transactionId);
            case kASObject:
                return new AmfASObject("", parent, transactionId);
            case kAssociativeArray:
                return new AmfAssociativeArrayData("", parent, transactionId);
            case kAvmPlusXml:
            case kString:
            case kXML:
            case kTrue:
            case kNull:
            case kInteger:
            case kDouble:
            case kBoolean:
            case kUndefined:
            case kFalse:
                return new AmfPrimitiveData("", type, new byte[0], parent, transactionId, true);
            case kBooleanArray:
                return new AmfBooleanArray("", parent, transactionId);
            case kByteArray:
                return new AmfByteArray("", parent, transactionId);
            case kCommandType:
                return new AmfOperation("", parent, transactionId);
            case kDate:
                return new AmfDateTime("", 0, parent, transactionId);
            case kDoubleArray:
                return new AmfDoubleArray("", parent, transactionId, amfRoot);
            case kIntArray:
                return new AmfIntArray("", parent, transactionId);
            case kObjectArray:
                return new AmfObjectArray("", parent, transactionId);
            case kServerSideObject:
                return new AmfServerSideObject("", parent, transactionId);
            case kAmfRemotingMessage:
                return new AmfRemotingMessage("", parent, transactionId);
            default:
                throw new IllegalArgumentException("Unknown AMF type: " + type.toString());
        }
    }

    public static String getAmfValue(final AbstractAmfData data)
    {
        String value = null;
        if (data instanceof AmfPrimitiveData)
        {
            value = ((AmfPrimitiveData) data).getValue();
        }
        // else if (data instanceof AmfGenericObject)
        // {
        // value = ((AmfGenericObject) data).getClassName();
        // }
        else if (data instanceof AmfOperation)
        {
            value = ((AmfOperation) data).getDescription();
        }
        if (value == null)
        {
            value = "";
        }
        return value;
    }

    private static AmfPrimitiveArray getArray(final Object o, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        if (o instanceof Boolean)
        {
            return new AmfBooleanArray("", parent, transactionId);
        }
        else if (o instanceof Integer || o instanceof Short || o instanceof Long)
        {
            return new AmfIntArray("", parent, transactionId);
        }
        else
        {
            return new AmfObjectArray("", parent, transactionId);
        }
    }

    public static AbstractAmfData parseAmfArray(final Object[] objects, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutableChildren)
    {
        AbstractAmfData array;
        if (objects.length > 0)
        {
            if (objects[0] instanceof Byte)
            {
                byte[] bytes = new byte[objects.length];
                for (int i = 0; i < objects.length; i++)
                {
                    bytes[i] = (Byte) objects[i];
                }
                array = new AmfByteArray("", bytes, parent, transactionId);
            }
            else
            {
                array = getArray(objects[0], parent, transactionId);
                for (Object object : objects)
                {
                    ((AmfPrimitiveArray) array).addChild(parseAmfData(object, parent, transactionId, mutableChildren));
                }
            }
        }
        else
        {
            array = new AmfObjectArray("", parent, transactionId);
        }
        return array;
    }

    public static AbstractAmfData parseAmfCollection(final Collection<Object> collection, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutableChildren)
    {
        return parseAmfArray(collection.toArray(), parent, transactionId, mutableChildren);
    }

    // public static void writeAMFUTF(DataOutputStream outputStream, boolean writeType, String str) throws IOException
    // {
    //
    // int strlen = str.length();
    // int utflen = 0;
    // int c, count = 0;
    //
    // char[] charr = new char[strlen];
    // str.getChars(0, strlen, charr, 0);
    //
    // for (int i = 0; i < strlen; i++)
    // {
    // c = charr[i];
    // if (c <= 0x007F)
    // {
    // utflen++;
    // }
    // else if (c > 0x07FF)
    // {
    // utflen += 3;
    // }
    // else
    // {
    // utflen += 2;
    // }
    // }
    //
    // int type;
    // // if (forceLong)
    // // {
    // // type = kLongStringType;
    // // }
    // // else
    // // {
    // if (utflen <= 65535)
    // type = AmfDataType.kStringType;
    // else
    // type = AmfDataType.kLongStringType;
    // // }
    //
    // byte[] bytearr;
    // if (writeType)
    // {
    // bytearr = new byte[(utflen + (type == AmfDataType.kStringType ? 3 : 5))];
    // bytearr[count++] = (byte)(type);
    // }
    // else
    // bytearr = new byte[(utflen + (type == AmfDataType.kStringType ? 2 : 4))];
    //
    // if (type == AmfDataType.kLongStringType)
    // {
    // bytearr[count++] = (byte)((utflen >>> 24) & 0xFF);
    // bytearr[count++] = (byte)((utflen >>> 16) & 0xFF);
    // }
    // // bytearr[count++] = (byte)((utflen >>> 8) & 0xFF);
    // // bytearr[count++] = (byte)((utflen) & 0xFF);
    // for (int i = 0; i < strlen; i++)
    // {
    // c = charr[i];
    // if (c <= 0x007F)
    // {
    // bytearr[count++] = (byte)c;
    // }
    // else if (c > 0x07FF)
    // {
    // bytearr[count++] = (byte)(0xE0 | ((c >> 12) & 0x0F));
    // bytearr[count++] = (byte)(0x80 | ((c >> 6) & 0x3F));
    // bytearr[count++] = (byte)(0x80 | ((c) & 0x3F));
    // }
    // else
    // {
    // bytearr[count++] = (byte)(0xC0 | ((c >> 6) & 0x1F));
    // bytearr[count++] = (byte)(0x80 | ((c) & 0x3F));
    // }
    // }
    // outputStream.write(bytearr, 0, count);
    //
    //
    //
    // // int strlen = s.length();
    // // int utflen = 0;
    // // int c, count = 0;
    // //
    // // char[] charr = new char[strlen];
    // // s.getChars(0, strlen, charr, 0);
    // //
    // // for (int i = 0; i < strlen; i++)
    // // {
    // // c = charr[i];
    // // if (c <= 0x007F)
    // // {
    // // utflen++;
    // // }
    // // else if (c > 0x07FF)
    // // {
    // // utflen += 3;
    // // }
    // // else
    // // {
    // // utflen += 2;
    // // }
    // // }
    // //
    // // writeUInt29(outputStream, (utflen << 1) | 1);
    // //
    // // byte[] bytearr = new byte[utflen];
    // //
    // // for (int i = 0; i < strlen; i++)
    // // {
    // // c = charr[i];
    // // if (c <= 0x007F)
    // // {
    // // bytearr[count++] = (byte) c;
    // // }
    // // else if (c > 0x07FF)
    // // {
    // // bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
    // // bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
    // // bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
    // // }
    // // else
    // // {
    // // bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
    // // bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
    // // }
    // // }
    // // outputStream.write(bytearr, 0, utflen);
    // }

    public static AbstractAmfData parseAmfData(final Object o, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        AbstractAmfData data = null;
        if (o == null)
        {
            data = new AmfPrimitiveData("", AmfDataType.kNull, new byte[0], parent, transactionId, mutable);
        }
        else if (o instanceof String || o instanceof Character)
        {
            data = new AmfPrimitiveData("", o.toString().getBytes(), parent, transactionId, mutable);
        }
        else if (o instanceof Number)
        {
            if (o instanceof Integer || o instanceof Short || o instanceof Byte)
            {
                int i = ((Number) o).intValue();
                data = new AmfPrimitiveData("", i, parent, transactionId, mutable);
            }
            else if (o instanceof BigInteger || o instanceof BigDecimal)
            {
                // Using double to write big numbers such as BigInteger or
                // BigDecimal can result in information loss so we write
                // them as String by default...
                data = new AmfPrimitiveData("", o.toString().getBytes(), parent, transactionId, mutable);
            }
            else
            {
                double d = ((Number) o).doubleValue();
                data = new AmfPrimitiveData("", d, parent, transactionId, mutable);
            }
        }
        else if (o instanceof Boolean)
        {
            data = new AmfPrimitiveData("", ((Boolean) o).booleanValue(), parent, transactionId, mutable);
        }
        // We have a complex type...
        else if (o instanceof Date)
        {
            data = new AmfDateTime("", ((Date) o).getTime(), parent, transactionId);
        }
        else if (o instanceof Calendar)
        {
            data = new AmfDateTime("", ((Calendar) o).getTimeInMillis(), parent, transactionId);
        }
        else if (o instanceof Document)
        {
            String xml = HtmlNodeWriter.write((Document) o, true, null);
            data = new AmfPrimitiveData("", AmfDataType.kAvmPlusXml, xml.getBytes(), parent, transactionId, mutable);
        }
        else if (o instanceof ASObject)
        {
            data = new AmfASObject("", (ASObject) o, parent, transactionId);
        }
        else if (o instanceof Map && !(o instanceof ASObject))
        {
            data = parseAmfMap((Map<Object, Object>) o, parent, transactionId, mutable);
        }
        else if (o instanceof Collection)
        {
            data = parseAmfCollection((Collection<Object>) o, parent, transactionId, mutable);
        }
        else if (o instanceof AmfServerSideObject)
        {
            AmfServerSideObject sso = (AmfServerSideObject) o;
            data = sso;
            sso.initialize(parent, transactionId);
        }
        else if (o instanceof CommandMessage)
        {
            data = new AmfCommandMessage("", (CommandMessage) o, o.getClass().getCanonicalName(), parent, transactionId);
        }
        else if (o instanceof ErrorMessage)
        {
            data = new AmfErrorMessage("", (ErrorMessage) o, parent, transactionId);
        }
        else if (o instanceof AcknowledgeMessage)
        {
            data = new AmfAcknowledgeMessage("", (AcknowledgeMessage) o, o.getClass().getCanonicalName(), parent, transactionId);
        }
        else if (o instanceof AsyncMessage)
        {
            data = new AmfAsyncMessage("", (AsyncMessage) o, o.getClass().getCanonicalName(), parent, transactionId);
        }
        else if (o instanceof RemotingMessage)
        {
            data = new AmfRemotingMessage("", (RemotingMessage) o, parent, transactionId);
        }
        else if (o.getClass().isArray())
        {
            data = parseAmfArray((Object[]) o, parent, transactionId, mutable);
        }
        else
        {
            data = new AmfServerSideObject(o, parent, transactionId);
            // LOGGER.warn("Problem with unknown data type in AmfUtils: " + o.getClass().getCanonicalName() + "\n" + o.toString());
        }
        return data;
    }

    // public static void WriteObjectTraits(DataOutputStream outputStream, boolean externalizable, boolean dynamic, int count, String className)
    // {
    // writeUInt29(outputStream, 3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
    // writeStringWithoutType(outputStream, className);
    //
    // if (!externalizable && propertyNames != null)
    // {
    // for (int i = 0; i < count; i++)
    // {
    // String propName = (String)ti.getProperty(i);
    // writeStringWithoutType(outputStream, propName);
    // }
    // }
    //
    // }

    public static AmfAssociativeArrayData parseAmfMap(final Map<Object, Object> map, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutableChildren)
    {
        AmfAssociativeArrayData data = new AmfAssociativeArrayData("", parent, transactionId);
        for (Object o : map.keySet())
        {
            String key = o.toString();
            data.putChild(key, parseAmfData(map.get(o), parent, transactionId, mutableChildren));
        }

        return data;
    }

    public static void setAmfValue(final AbstractAmfData data, final String value)
    {
        if (data instanceof AmfPrimitiveData)
        {
            ((AmfPrimitiveData) data).setValue(value);
        }
        else if (data instanceof AmfGenericObject)
        {
            ((AmfGenericObject) data).setClassName(value);
        }
    }

    public static void writeAMFInt(final AmfOutputStream outputStream, int i, final boolean useAmf3Code) throws IOException
    {
        if (i >= INT28_MIN_VALUE && i <= INT28_MAX_VALUE)
        {
            // We have to be careful when the MSB is set, as (value >> 3) will sign extend.
            // We know there are only 29-bits of precision, so truncate. This requires
            // similar care when reading an integer.
            // i = ((i >> 3) & UINT29_MASK);
            i = i & UINT29_MASK; // Mask is 2^29 - 1
            AmfDataType.kInteger.writeCode(outputStream, useAmf3Code);
            writeUInt29(outputStream, i);
        }
        else
        {
            // Promote large int to a double
            AmfDataType.kDouble.writeCode(outputStream, useAmf3Code);
            outputStream.writeDouble(i);
        }
    }

    public static void writeAMFUTF(final DataOutputStream outputStream, final boolean writeType, final String str, final boolean useAMF3) throws IOException
    {
        if (useAMF3)
        {
            writeAmfUTF3(outputStream, writeType, str);
        }
        else
        {
            writeAmfUTF0(outputStream, writeType, str);
        }
    }

    protected static void writeAmfUTF0(final DataOutputStream outputStream, final boolean writeType, final String str) throws IOException
    {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        char[] charr = new char[strlen];
        str.getChars(0, strlen, charr, 0);

        for (int i = 0; i < strlen; i++)
        {
            c = charr[i];
            if (c <= 0x007F)
            {
                utflen++;
            }
            else if (c > 0x07FF)
            {
                utflen += 3;
            }
            else
            {
                utflen += 2;
            }
        }

        int type;
        // if (forceLong)
        // {
        // type = kLongStringType;
        // }
        // else
        {
            if (utflen <= 65535)
            {
                type = AmfConstants.kStringType;
            }
            else
            {
                type = AmfConstants.kLongStringType;
            }
        }

        byte[] bytearr;
        if (writeType)
        {
            bytearr = new byte[utflen + (type == AmfConstants.kStringType ? 3 : 5)];
            bytearr[count++] = (byte) type;
        }
        else
        {
            bytearr = new byte[utflen + (type == AmfConstants.kStringType ? 2 : 4)];
        }

        if (type == AmfConstants.kLongStringType)
        {
            bytearr[count++] = (byte) (utflen >>> 24 & 0xFF);
            bytearr[count++] = (byte) (utflen >>> 16 & 0xFF);
        }
        bytearr[count++] = (byte) (utflen >>> 8 & 0xFF);
        bytearr[count++] = (byte) (utflen & 0xFF);
        for (int i = 0; i < strlen; i++)
        {
            c = charr[i];
            if (c <= 0x007F)
            {
                bytearr[count++] = (byte) c;
            }
            else if (c > 0x07FF)
            {
                bytearr[count++] = (byte) (0xE0 | c >> 12 & 0x0F);
                bytearr[count++] = (byte) (0x80 | c >> 6 & 0x3F);
                bytearr[count++] = (byte) (0x80 | c & 0x3F);
            }
            else
            {
                bytearr[count++] = (byte) (0xC0 | c >> 6 & 0x1F);
                bytearr[count++] = (byte) (0x80 | c & 0x3F);
            }
        }
        outputStream.write(bytearr, 0, count);
    }

    // public static void writeStringWithoutType(DataOutputStream outputStream, String string) throws IOException
    // {
    // if (string.length() == 0)
    // {
    // // don't create a reference for the empty string,
    // // as it's represented by the one byte value 1
    // // len = 0, ((len << 1) | 1).
    // writeUInt29(outputStream, 1);
    // }
    // else
    // {
    // writeAMFUTF(outputStream, string);
    // }
    // }

    protected static void writeAmfUTF3(final DataOutputStream outputStream, final boolean writeType, final String str) throws IOException
    {
        if (writeType)
        {
            outputStream.write(AmfConstants.kStringType);
        }

        if (str.length() == 0)
        {
            // don't create a reference for the empty string,
            // as it's represented by the one byte value 1
            // len = 0, ((len << 1) | 1).
            writeUInt29(outputStream, 1);
            return;
        }

        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        char[] charr = new char[strlen];
        str.getChars(0, strlen, charr, 0);

        for (int i = 0; i < strlen; i++)
        {
            c = charr[i];
            if (c <= 0x007F)
            {
                utflen++;
            }
            else if (c > 0x07FF)
            {
                utflen += 3;
            }
            else
            {
                utflen += 2;
            }
        }

        writeUInt29(outputStream, utflen << 1 | 1);

        byte[] bytearr = new byte[utflen];

        for (int i = 0; i < strlen; i++)
        {
            c = charr[i];
            if (c <= 0x007F)
            {
                bytearr[count++] = (byte) c;
            }
            else if (c > 0x07FF)
            {
                bytearr[count++] = (byte) (0xE0 | c >> 12 & 0x0F);
                bytearr[count++] = (byte) (0x80 | c >> 6 & 0x3F);
                bytearr[count++] = (byte) (0x80 | c >> 0 & 0x3F);
            }
            else
            {
                bytearr[count++] = (byte) (0xC0 | c >> 6 & 0x1F);
                bytearr[count++] = (byte) (0x80 | c >> 0 & 0x3F);
            }
        }
        outputStream.write(bytearr, 0, utflen);
    }

    public static void writeBoolean(final AmfOutputStream outputStream, final boolean value, final boolean useAmf3Code) throws IOException
    {
        if (useAmf3Code)
        {
            if (value)
            {
                AmfDataType.kTrue.writeCode(outputStream, useAmf3Code);
            }
            else
            {
                AmfDataType.kFalse.writeCode(outputStream, useAmf3Code);
            }
        }
        else
        {
            AmfDataType.kBoolean.writeCode(outputStream, useAmf3Code);
            outputStream.writeBoolean(value);
        }
    }

    public static void writeUInt29(final DataOutputStream outputStream, final int value) throws IOException
    {
        // Represent smaller integers with fewer bytes using the most
        // significant bit of each byte. The worst case uses 32-bits
        // to represent a 29-bit number, which is what we would have
        // done with no compression.

        // 0x00000000 - 0x0000007F : 0xxxxxxx
        // 0x00000080 - 0x00003FFF : 1xxxxxxx 0xxxxxxx
        // 0x00004000 - 0x001FFFFF : 1xxxxxxx 1xxxxxxx 0xxxxxxx
        // 0x00200000 - 0x3FFFFFFF : 1xxxxxxx 1xxxxxxx 1xxxxxxx xxxxxxxx
        // 0x40000000 - 0xFFFFFFFF : throw range exception
        if (value < 0x80)
        {
            // 0x00000000 - 0x0000007F : 0xxxxxxx
            outputStream.writeByte(value);
        }
        else if (value < 0x4000)
        {
            // 0x00000080 - 0x00003FFF : 1xxxxxxx 0xxxxxxx
            outputStream.writeByte(value >> 7 & 0x7F | 0x80);
            outputStream.writeByte(value & 0x7F);

        }
        else if (value < 0x200000)
        {
            // 0x00004000 - 0x001FFFFF : 1xxxxxxx 1xxxxxxx 0xxxxxxx
            outputStream.writeByte(value >> 14 & 0x7F | 0x80);
            outputStream.writeByte(value >> 7 & 0x7F | 0x80);
            outputStream.writeByte(value & 0x7F);

        }
        else if (value < 0x40000000)
        {
            // 0x00200000 - 0x3FFFFFFF : 1xxxxxxx 1xxxxxxx 1xxxxxxx xxxxxxxx
            outputStream.writeByte(value >> 22 & 0x7F | 0x80);
            outputStream.writeByte(value >> 15 & 0x7F | 0x80);
            outputStream.writeByte(value >> 8 & 0x7F | 0x80);
            outputStream.writeByte(value & 0xFF);

        }
        else
        {
            // 0x40000000 - 0xFFFFFFFF : throw range exception
            throw new MessageException("Integer out of range: " + value);
        }
    }

}
