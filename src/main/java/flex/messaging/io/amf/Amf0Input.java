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
package flex.messaging.io.amf;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flex.messaging.io.AbstractProxy;
import flex.messaging.io.ClassAliasRegistry;
import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.UnknownTypeException;
import flex.messaging.util.ClassUtil;

/**
 * An Amf0 input object. 
 * @exclude
 */
public class Amf0Input extends AbstractAmfInput implements AmfTypes
{
    /**
     * Unfortunately the Flash Player starts AMF 3 messages off with the legacy
     * AMF 0 format and uses a type, AmfTypes.kAvmPlusObjectType, to indicate
     * that the next object in the stream is to be deserialized differently. The
     * original hope was for two independent encoding versions... but for now
     * we just keep a reference to objectInput here.
     * @exclude
     */
    protected ActionMessageInput avmPlusInput;

    /**
     * @exclude
     */
    protected List objectsTable;

    public Amf0Input(SerializationContext context)
    {
        super(context);

        objectsTable = new ArrayList(64);
    }

    /**
     * Clear all object reference information so that the instance
     * can be used to deserialize another data structure.
     *
     * Reset should be called before reading a top level object,
     * such as a new header or a new body.
     */
    @Override public void reset()
    {
        super.reset();

        objectsTable.clear();

        if (avmPlusInput != null)
            avmPlusInput.reset();
    }


    //
    // java.io.ObjectInput SERIALIZATION IMPLEMENTATIONS
    //

    /**
     * Public entry point to read a top level AMF Object, such as
     * a header value or a message body.
     */
    @Override public Object readObject() throws ClassNotFoundException, IOException
    {
        int type = in.readByte();

        Object value = readObjectValue(type);
        return value;
    }

    protected Object readObjectValue(int type) throws ClassNotFoundException, IOException
    {
        Object value = null;
        switch (type)
        {
            case kNumberType:
                double d = readDouble();

                if (isDebug)
                    trace.write(d);

                value = new Double(d);

                break;

            case kBooleanType:
                value = Boolean.valueOf(readBoolean());

                if (isDebug)
                    trace.write(value);

                break;

            case kStringType:
                value = readString();
                break;

            case kAvmPlusObjectType:

                if (avmPlusInput == null)
                {
                    avmPlusInput = new Amf3Input(context);
                    avmPlusInput.setDebugTrace(trace);
                    avmPlusInput.setInputStream(in);
                }

                value = avmPlusInput.readObject();
                break;

            case kStrictArrayType:
                value = readArrayValue();
                break;

            case kTypedObjectType:
                String typeName = in.readUTF();
                value = readObjectValue(typeName);
                break;

            case kLongStringType:
                value = readLongUTF();

                if (isDebug)
                    trace.writeString((String)value);
                break;

            case kObjectType:
                value = readObjectValue(null);
                break;

            case kXMLObjectType:
                value = readXml();
                break;

            case kNullType:
                if (isDebug)
                    trace.writeNull();
                break;

            case kDateType:
                value = readDate();

                break;

            case kECMAArrayType:
                value = readECMAArrayValue();
                break;

            case kReferenceType:
                int refNum = in.readUnsignedShort();

                if (isDebug)
                    trace.writeRef(refNum);

                value = objectsTable.get(refNum);
                break;

            case kUndefinedType:
                if (isDebug)
                    trace.writeUndefined();
                break;

            case kUnsupportedType:

                if (isDebug)
                    trace.write("UNSUPPORTED");

                //Unsupported type found in AMF stream.
                UnknownTypeException ex = new UnknownTypeException();
                ex.setMessage(10302);
                throw ex;

            case kObjectEndType:

                if (isDebug)
                    trace.write("UNEXPECTED OBJECT END");

                //Unexpected object end tag in AMF stream.
                UnknownTypeException ex1 = new UnknownTypeException();
                ex1.setMessage(10303);
                throw ex1;

            case kRecordsetType:

                if (isDebug)
                    trace.write("UNEXPECTED RECORDSET");

                //AMF Recordsets are not supported.
                UnknownTypeException ex2 = new UnknownTypeException();
                ex2.setMessage(10304);
                throw ex2;

            default:

                if (isDebug)
                    trace.write("UNKNOWN TYPE");

                UnknownTypeException ex3 = new UnknownTypeException();
                ex3.setMessage(10301, new Object[]{new Integer(type)});
                throw ex3;
        }
        return value;
    }

    protected Date readDate() throws IOException
    {
        long time = (long)in.readDouble();

        /*
            We read in the timezone but do nothing with the value as
            we expect dates to be written in the UTC timezone. Client
            and servers are responsible for applying their own
            timezones.
        */
        in.readShort();

        Date d = new Date(time);

        if (isDebug)
            trace.write(d.toString());

        return d;
    }

    /**
     * Deserialize the bits of an ECMA array w/o a prefixing type byte.
     */
    protected Map readECMAArrayValue() throws ClassNotFoundException, IOException
    {
        int size = in.readInt();
        HashMap h;
        if (size == 0)
            h = new HashMap();
        else
            h = new HashMap(size);

        rememberObject(h);

        if (isDebug)
            trace.startECMAArray(objectsTable.size() - 1);

        String name = in.readUTF();
        int type = in.readByte();
        while (type != kObjectEndType)
        {
            if (type != kObjectEndType)
            {
                if (isDebug)
                    trace.namedElement(name);

                // Always read value but be careful to ignore erroneous 'length' prop that is sometimes sent by the player.
                Object value = readObjectValue(type);
                if (!name.equals("length"))
                    h.put(name, value);
            }

            name = in.readUTF();
            type = in.readByte();
        }

        if (isDebug)
            trace.endAMFArray();

        return h;
    }

    protected String readString() throws IOException
    {
        String s = readUTF();

        if (isDebug)
            trace.writeString(s);

        return s;
    }


    /**
     * Deserialize the bits of an array w/o a prefixing type byte.
     */
    protected Object readArrayValue() throws ClassNotFoundException, IOException
    {
        int size = in.readInt();
        ArrayList l = new ArrayList(size);
        rememberObject(l);

        if (isDebug)
            trace.startAMFArray(objectsTable.size() - 1);

        for (int i = 0; i < size; ++i)
        {
            // Get element type
            int type = in.readByte();

            if (isDebug)
                trace.arrayElement(i);

            // Add value to the array
            l.add(readObjectValue(type));
        }

        if (isDebug)
            trace.endAMFArray();

        if (context.legacyCollection)
            return l;
        else
            return l.toArray();
    }




    /**
     * Deserialize the bits of a map w/o a prefixing type byte.
     */
    protected Object readObjectValue(String className) throws ClassNotFoundException, IOException
    {
        PropertyProxy proxy = null;
        Object object;

        // Check for any registered class aliases
        String aliasedClass = ClassAliasRegistry.getRegistry().getClassName(className);
        if (aliasedClass != null)
            className = aliasedClass;

        if (className == null || className.length() == 0)
        {
            object = new ASObject();
        }
        else if (className.startsWith(">")) // Handle [RemoteClass] (no server alias)
        {
            object = new ASObject();
            ((ASObject)object).setType(className);
        }
        else if (context.instantiateTypes || className.startsWith("flex."))
        {
            Class desiredClass = AbstractProxy.getClassFromClassName(className, context.createASObjectForMissingType);

            proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);

            if (proxy == null)
                object = ClassUtil.createDefaultInstance(desiredClass, null);
            else
                object = proxy.createInstance(className);
        }
        else
        {
            // Just return type info with an ASObject...
            object = new ASObject();
            ((ASObject)object).setType(className);
        }

        if (proxy == null)
            proxy = PropertyProxyRegistry.getProxyAndRegister(object);

        int objectId = rememberObject(object);

        if (isDebug)
            trace.startAMFObject(className, objectsTable.size() - 1);

        String propertyName = in.readUTF();
        int type = in.readByte();
        while (type != kObjectEndType)
        {
            if (isDebug)
                trace.namedElement(propertyName);

            Object value = readObjectValue(type);
            proxy.setValue(object, propertyName, value);
            propertyName = in.readUTF();
            type = in.readByte();
        }

        if (isDebug)
            trace.endAMFObject();

        // This lets the BeanProxy substitute a new instance into the BeanProxy
        // at the end of the serialization.  You might for example create a Map, store up
        // the properties, then construct the instance based on that.  Note that this does
        // not support recursive references to the parent object however.
        Object newObj = proxy.instanceComplete(object);

        // TODO: It is possible we gave out references to the
        // temporary object.  it would be possible to warn users about
        // that problem by tracking if we read any references to this object
        // in the readObject call above.
        if (newObj != object)
        {
            objectsTable.set(objectId, newObj);
            object = newObj;
        }

        return object;
    }


    /**
     * This code borrows heavily from DataInputStreat.readUTF().
     * However, it uses a 32-bit string length.
     *
     * @return the read String
     * @throws java.io.UTFDataFormatException if the UTF-8 encoding is incorrect
     * @throws IOException            if an I/O error occurs.
     */
    protected String readLongUTF() throws IOException
    {
        int utflen = in.readInt();
        int c, char2, char3;
        char[] charr = getTempCharArray(utflen);
        byte bytearr [] = getTempByteArray(utflen);
        int count = 0;
        int chCount = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen)
        {
            c = bytearr[count] & 0xff;
            switch (c >> 4)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx*/
                    count++;
                    charr[chCount] = (char)c;
                    break;
                case 12:
                case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException();
                    char2 = bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException();
                    charr[chCount] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException();
                    char2 = bytearr[count - 2];
                    char3 = bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException();
                    charr[chCount] = (char)
                        (((c & 0x0F) << 12) |
                         ((char2 & 0x3F) << 6) |
                         ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException();
            }
            chCount++;
        }
        // The number of chars produced may be less than utflen
        return new String(charr, 0, chCount);
    }

    protected Object readXml() throws IOException
    {
        String xml = readLongUTF();

        if (isDebug)
            trace.write(xml);

        return stringToDocument(xml);
    }


    /**
     * Remember a deserialized object so that you can use it later through a reference.
     */
    protected int rememberObject(Object obj)
    {
        int id = objectsTable.size();
        objectsTable.add(obj);
        return id;
    }
}
