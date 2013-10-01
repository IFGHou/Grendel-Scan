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

import java.io.Externalizable;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfServerSideObject;

import flex.messaging.io.AbstractProxy;
import flex.messaging.io.ClassAliasRegistry;
import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationException;
import flex.messaging.io.UnknownTypeException;
import flex.messaging.util.ClassUtil;
import flex.messaging.util.Trace;

/**
 * Reads AMF 3 formatted data stream.
 * <p>
 * This class intends to matches the Flash Player 8 C++ code
 * in avmglue/DataIO.cpp
 * </p>
 *
 * @author Peter Farland
 * @exclude
 */
public class Amf3Input extends AbstractAmfInput implements Amf3Types
{
    /**
     * @exclude
     */
    protected List objectTable;

    /**
     * @exclude
     */
    protected List stringTable;

    /**
     * @exclude
     */
    protected List traitsTable;

    public Amf3Input(SerializationContext context)
    {
        super(context);

        stringTable = new ArrayList(64);
        objectTable = new ArrayList(64);
        traitsTable = new ArrayList(10);
    }

    /**
     * Reset should be called before reading a top level object,
     * such as a new header or a new body.
     */
    @Override public void reset()
    {
        super.reset();
        stringTable.clear();
        objectTable.clear();
        traitsTable.clear();
    }

/* TODO UCdetector: Remove unused code: 
    public Object saveObjectTable()
    {
        Object table = objectTable;
        objectTable = new ArrayList(64);
        return table;
    }
*/

/* TODO UCdetector: Remove unused code: 
    public void restoreObjectTable(Object table)
    {
        objectTable = (ArrayList) table;
    }
*/

/* TODO UCdetector: Remove unused code: 
    public Object saveTraitsTable()
    {
        Object table = traitsTable;
        traitsTable = new ArrayList(10);
        return table;
    }
*/

/* TODO UCdetector: Remove unused code: 
    public void restoreTraitsTable(Object table)
    {
        traitsTable = (ArrayList) table;
    }
*/

/* TODO UCdetector: Remove unused code: 
    public Object saveStringTable()
    {
        Object table = stringTable;
        stringTable = new ArrayList(64);
        return table;
    }
*/

/* TODO UCdetector: Remove unused code: 
    public void restoreStringTable(Object table)
    {
        stringTable = (ArrayList) table;
    }
*/

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

    /**
     * @exclude
     */
    protected Object readObjectValue(int type) throws ClassNotFoundException, IOException
    {
        Object value = null;

        switch (type)
        {
            case kStringType:
                value = readString();

                if (isDebug)
                    trace.writeString((String)value);
                break;

            case kObjectType:
                value = readScriptObject();
                break;

            case kArrayType:
                value = readArray();
                break;

            case kFalseType:
                value = Boolean.FALSE;

                if (isDebug)
                    trace.write(value);
                break;

            case kTrueType:
                value = Boolean.TRUE;

                if (isDebug)
                    trace.write(value);
                break;

            case kIntegerType:
                int i = readUInt29();
                // Symmetric with writing an integer to fix sign bits for negative values...
                i = (i << 3) >> 3;
                value = new Integer(i);

                if (isDebug)
                    trace.write(value);
                break;

            case kDoubleType:
                value = new Double(in.readDouble());

                if (isDebug)
                    trace.write(value);
                break;

            case kUndefinedType:
                if (isDebug)
                    trace.writeUndefined();
                break;

            case kNullType:

                if (isDebug)
                    trace.writeNull();
                break;

            case kXMLType:
            case kAvmPlusXmlType:
                value = readXml();
                break;

            case kDateType:
                value = readDate();

                if (isDebug)
                    trace.write(value.toString());
                break;

            case kByteArrayType:
                value = readByteArray();
                break;
            default:
                // Unknown object type tag {type}
                UnknownTypeException ex = new UnknownTypeException();
                ex.setMessage(10301, new Object[]{new Integer(type)});
                throw ex;
        }

        return value;
    }

    /**
     * @exclude
     */
    protected String readString() throws IOException
    {
        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            // This is a reference
             return getStringReference(ref >> 1);
        }
		// Read the string in
		int len = (ref >> 1);

		// writeString() special cases the empty string
		// to avoid creating a reference.
		if (0 == len)
		{
		    return EMPTY_STRING;
		}

		String str = readUTF(len);

		// Remember String
		stringTable.add(str);

		return str;
    }

    /**
     * Deserialize the bits of a date-time value w/o a prefixing type byte.
     */
    protected Date readDate() throws IOException
    {
        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            // This is a reference
            return (Date)getObjectReference(ref >> 1);
        }
		long time = (long)in.readDouble();

		Date d = new Date(time);

		//Remember Date
		objectTable.add(d);

		if (isDebug)
		    trace.write(d);

		return d;
    }

    /**
     * @exclude
     */
    protected Object readArray() throws ClassNotFoundException, IOException
    {
        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            // This is a reference
            return getObjectReference(ref >> 1);
        }
		int len = (ref >> 1);
		Object array = null;

		// First, look for any string based keys. If any
		// non-ordinal indices were used, or if the Array is
		// sparse, we represent the structure as a Map.
		Map map = null;
		for (; ;)
		{
		    String name = readString();
		    if (name == null || name.length() == 0) break;

		    if (map == null)
		    {
		        map = new HashMap();
		        array = map;

		        //Remember Object
		        objectTable.add(array);

		        if (isDebug)
		            trace.startECMAArray(objectTable.size() - 1);
		    }

		    Object value = readObject();
		    map.put(name, value);
		}

		// If we didn't find any string based keys, we have a
		// dense Array, so we represent the structure as a List.
		if (map == null)
		{
		    // Legacy Flex 1.5 behavior was to return a java.util.Collection for Array
		    if (context.legacyCollection)
		    {
		        List list = new ArrayList(len);
		        array = list;

		        // Remember List
		        objectTable.add(array);

		        if (isDebug)
		            trace.startAMFArray(objectTable.size() - 1);

		        for (int i = 0; i < len; i++)
		        {
		            if (isDebug)
		                trace.arrayElement(i);

		            Object item = readObject();
		            list.add(i, item);
		        }
		    }
		    else
		    {
		        // New Flex 2+ behavior is to return Object[] for AS3 Array
		        array = new Object[len];

		        // Remember native Object[]
		        objectTable.add(array);

		        if (isDebug)
		            trace.startAMFArray(objectTable.size() - 1);

		        for (int i = 0; i < len; i++)
		        {
		            if (isDebug)
		                trace.arrayElement(i);

		            Object item = readObject();
		            Array.set(array, i, item);
		        }
		    }
		}
		else
		{
		    for (int i = 0; i < len; i++)
		    {
		        if (isDebug)
		            trace.arrayElement(i);

		        Object item = readObject();
		        map.put(Integer.toString(i), item);
		    }
		}

		if (isDebug)
		    trace.endAMFArray();

		return array;
    }

    /**
     * @exclude
     */
    protected Object readScriptObject() throws ClassNotFoundException, IOException
    {
        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            return getObjectReference(ref >> 1);
        }
		TraitsInfo ti = readTraits(ref);
		String className = ti.getClassName();
		boolean externalizable = ti.isExternalizable();
		Object object;
		PropertyProxy proxy = null;

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
		else if (context.instantiateTypes 
				|| className.startsWith("flex.")
//            		|| className.equals(AcknowledgeMessageExt.CLASS_ALIAS)
//            		|| className.equals(CommandMessageExt.CLASS_ALIAS)
//            		|| className.equals(AsyncMessageExt.CLASS_ALIAS)
				)
		{
			try
		    {
				String logicalClassName;
//            		if (className.equals(AcknowledgeMessageExt.CLASS_ALIAS))
//            			logicalClassName = "flex.messaging.messages.AcknowledgeMessage";
//            		else if (className.equals(CommandMessageExt.CLASS_ALIAS))
//            			logicalClassName = "flex.messaging.messages.CommandMessage";
//            		else if (className.equals(AsyncMessageExt.CLASS_ALIAS))
//            			logicalClassName = "flex.messaging.messages.AsyncMessage";
//            		else
					logicalClassName = className;

//            		if (!logicalClassName.equals(className) && externalizable)
//            		{
//            			logicalClassName = logicalClassName + "Ext";
//            		}
					
				
				// This is just to see if the class is actually known locally
				Class targetClass = Class.forName(logicalClassName);
				Class desiredClass = AbstractProxy.getClassFromClassName(logicalClassName, context.createASObjectForMissingType);

		        proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);
		        if (proxy == null)
		            object = ClassUtil.createDefaultInstance(desiredClass, null);
		        else
		            object = proxy.createInstance(logicalClassName);
		    }
		    catch (ClassNotFoundException e)
		    {
		    	object = new AmfServerSideObject(className);
		        proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(AmfServerSideObject.class);
		    }
		}
		else
		{
		    // Just return type info with an ASObject...
		    object = new ASObject();
		    ((ASObject)object).setType(className);
		}

		if (proxy == null)
		    proxy = PropertyProxyRegistry.getProxyAndRegister(object);

		// Remember our instance in the object table
		int objectId = objectTable.size();
		objectTable.add(object);

		if (externalizable)
		{
		    readExternalizable(className, object);
		}
		else
		{
		    if (isDebug)
		    {
		        trace.startAMFObject(className, objectTable.size() - 1);
		    }

		    int len = ti.getProperties().size();

		    for (int i = 0; i < len; i++)
		    {
		        String propName = ti.getProperty(i);

		        if (isDebug)
		            trace.namedElement(propName);

		        Object value = readObject();
		        proxy.setValue(object, propName, value);
		    }

		    if (ti.isDynamic())
		    {
		        for (; ;)
		        {
		            String name = readString();
		            if (name == null || name.length() == 0) break;

		            if (isDebug)
		                trace.namedElement(name);

		            Object value = readObject();
		            proxy.setValue(object, name, value);
		        }
		    }
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
		    objectTable.set(objectId, newObj);
		    object = newObj;
		}

		return object;
    }

    /**
     * @exclude
     */
    protected void readExternalizable(String className, Object object) throws ClassNotFoundException, IOException
    {
        if (object instanceof Externalizable)
        {
            if (isDebug)
            {
                trace.startExternalizableObject(className, objectTable.size() - 1);
            }

            ((Externalizable)object).readExternal(this);
        }
        else
        {
            //Class '{className}' must implement java.io.Externalizable to receive client IExternalizable instances.
            SerializationException ex = new SerializationException();
            ex.setMessage(10305, new Object[] {object.getClass().getName()});
            throw ex;
        }
    }

    /**
     * @exclude
     */
    protected byte[] readByteArray() throws IOException
    {
        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            return (byte[])getObjectReference(ref >> 1);
        }
        else
        {
            int len = (ref >> 1);

            byte[] ba = new byte[len];

            // Remember byte array object
            objectTable.add(ba);

            in.readFully(ba, 0, len);

            if (isDebug)
                trace.startByteArray(objectTable.size() - 1, len);

            return ba;
        }
    }

    /**
     * @exclude
     */
    protected TraitsInfo readTraits(int ref) throws IOException
    {
        if ((ref & 3) == 1)
        {
            // This is a reference
            return getTraitReference(ref >> 2);
        }
        else
        {
            boolean externalizable = ((ref & 4) == 4);
            boolean dynamic = ((ref & 8) == 8);
            int count = (ref >> 4); /* uint29 */
            String className = readString();

            TraitsInfo ti = new TraitsInfo(className, dynamic, externalizable, count);

            // Remember Trait Info
            traitsTable.add(ti);

            for (int i = 0; i < count; i++)
            {
                String propName = readString();
                ti.addProperty(propName);
            }

            return ti;
        }
    }

    /**
     * @exclude
     */
    protected String readUTF(int utflen) throws IOException
    {
        char[] charr = getTempCharArray(utflen);
        byte[] bytearr = getTempByteArray(utflen);
        int c, char2, char3;
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

    /**
     * AMF 3 represents smaller integers with fewer bytes using the most
     * significant bit of each byte. The worst case uses 32-bits
     * to represent a 29-bit number, which is what we would have
     * done with no compression.
     * <pre>
     * 0x00000000 - 0x0000007F : 0xxxxxxx
     * 0x00000080 - 0x00003FFF : 1xxxxxxx 0xxxxxxx
     * 0x00004000 - 0x001FFFFF : 1xxxxxxx 1xxxxxxx 0xxxxxxx
     * 0x00200000 - 0x3FFFFFFF : 1xxxxxxx 1xxxxxxx 1xxxxxxx xxxxxxxx
     * 0x40000000 - 0xFFFFFFFF : throw range exception
     * </pre>
     *
     * @return A int capable of holding an unsigned 29 bit integer.
     * @throws IOException
     * @exclude
     */
    protected int readUInt29() throws IOException
    {
        int value;

        // Each byte must be treated as unsigned
        int b = in.readByte() & 0xFF;

        if (b < 128)
        {
            return b;
        }

        value = (b & 0x7F) << 7;
        b = in.readByte() & 0xFF;

        if (b < 128)
        {
            return (value | b);
        }

        value = (value | (b & 0x7F)) << 7;
        b = in.readByte() & 0xFF;

        if (b < 128)
        {
            return (value | b);
        }

        value = (value | (b & 0x7F)) << 8;
        b = in.readByte() & 0xFF;

        return (value | b);
    }

    /**
     * @exclude
     */
    protected Object readXml() throws IOException
    {
        String xml = null;

        int ref = readUInt29();

        if ((ref & 1) == 0)
        {
            // This is a reference
            xml = (String)getObjectReference(ref >> 1);
        }
        else
        {
            // Read the string in
            int len = (ref >> 1);

            // writeString() special case the empty string
            // for speed.  Do add a reference
            if (0 == len)
                xml = EMPTY_STRING;
            else
                xml = readUTF(len);

            //Remember Object
            objectTable.add(xml);

            if (isDebug)
                trace.write(xml);
        }

        return stringToDocument(xml);
    }

    /**
     * @exclude
     */
    protected Object getObjectReference(int ref)
    {
        if (isDebug)
        {
            trace.writeRef(ref);
        }

        return objectTable.get(ref);
    }

    /**
     * @exclude
     */
    protected String getStringReference(int ref)
    {
        String str = (String)stringTable.get(ref);

        if (Trace.amf && isDebug)
        {
            trace.writeStringRef(ref);
        }

        return str;
    }

    /**
     * @exclude
     */
    protected TraitsInfo getTraitReference(int ref)
    {
        if (Trace.amf && isDebug)
        {
            trace.writeTraitsInfoRef(ref);
        }

        return (TraitsInfo)traitsTable.get(ref);
    }

}
