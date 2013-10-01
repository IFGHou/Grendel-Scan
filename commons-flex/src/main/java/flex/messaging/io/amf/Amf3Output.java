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

import java.io.Externalizable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.RowSet;

import org.w3c.dom.Document;

import flex.messaging.MessageException;
import flex.messaging.io.ArrayCollection;
import flex.messaging.io.PagedRowSet;
import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationDescriptor;
import flex.messaging.io.StatusInfoProxy;
import flex.messaging.util.Trace;

/**
 * Serializes data to an output stream using the new AMF 3 format.
 * <p>
 * This class intends to match the Flash Player 8 C++ code in avmglue/DataIO.cpp
 * </p>
 * 
 * @author Peter Farland
 * @exclude
 */
public class Amf3Output extends AbstractAmfOutput implements Amf3Types
{
    /**
     * @exclude
     */
    protected IdentityHashMap objectTable;

    /**
     * @exclude
     */
    protected HashMap traitsTable;

    /**
     * @exclude
     */
    protected HashMap stringTable;

    public Amf3Output(SerializationContext context)
    {
        super(context);

        objectTable = new IdentityHashMap(64);
        traitsTable = new HashMap(10);
        stringTable = new HashMap(64);

        context.supportDatesByReference = true;
    }

    @Override
    public void reset()
    {
        super.reset();
        objectTable.clear();
        traitsTable.clear();
        stringTable.clear();
    }

    //
    // java.io.ObjectOutput IMPLEMENTATIONS
    //

    /**
     * Serialize an Object using AMF 3.
     */
    @Override
    public void writeObject(Object o) throws IOException
    {
        if (o == null)
        {
            writeAMFNull();
            return;
        }

        if (!context.legacyExternalizable && o instanceof Externalizable)
        {
            writeCustomObject(o);
        }
        else if (o instanceof String || o instanceof Character)
        {
            String s = o.toString();
            writeAMFString(s);
        }
        else if (o instanceof Number)
        {
            if (o instanceof Integer || o instanceof Short || o instanceof Byte)
            {
                int i = ((Number) o).intValue();
                writeAMFInt(i);
            }
            else if (!context.legacyBigNumbers && (o instanceof BigInteger || o instanceof BigDecimal))
            {
                // Using double to write big numbers such as BigInteger or
                // BigDecimal can result in information loss so we write
                // them as String by default...
                writeAMFString(((Number) o).toString());
            }
            else
            {
                double d = ((Number) o).doubleValue();
                writeAMFDouble(d);
            }
        }
        else if (o instanceof Boolean)
        {
            writeAMFBoolean(((Boolean) o).booleanValue());
        }
        // We have a complex type...
        else if (o instanceof Date)
        {
            writeAMFDate((Date) o);
        }
        else if (o instanceof Calendar)
        {
            writeAMFDate(((Calendar) o).getTime());
        }
        else if (o instanceof Document)
        {
            if (context.legacyXMLDocument)
                out.write(kXMLType); // Legacy flash.xml.XMLDocument Type
            else
                out.write(kAvmPlusXmlType); // New E4X XML Type
            if (!byReference(o))
            {
                String xml = documentToString(o);
                if (isDebug)
                    trace.write(xml);

                writeAMFUTF(xml);
            }
        }
        else
        {
            // We have an Object or Array type...
            Class cls = o.getClass();

            if (context.legacyMap && o instanceof Map && !(o instanceof ASObject))
            {
                writeMapAsECMAArray((Map) o);
            }
            else if (o instanceof Collection)
            {
                if (context.legacyCollection)
                    writeCollection((Collection) o, null);
                else
                    writeArrayCollection((Collection) o, null);
            }
            else if (cls.isArray())
            {
                writeAMFArray(o, cls.getComponentType());
            }
            else
            {
                // Special Case: wrap RowSet in PageableRowSet for Serialization
                if (o instanceof RowSet)
                {
                    o = new PagedRowSet((RowSet) o, Integer.MAX_VALUE, false);
                }
                else if (context.legacyThrowable && o instanceof Throwable)
                {
                    o = new StatusInfoProxy((Throwable) o);
                }

                writeCustomObject(o);
            }
        }
    }

    @Override
    public void writeObjectTraits(TraitsInfo ti) throws IOException
    {
        String className = ti.getClassName();

        if (isDebug)
        {
            if (ti.isExternalizable())
                trace.startExternalizableObject(className, objectTable.size() - 1);
            else
                trace.startAMFObject(className, objectTable.size() - 1);
        }

        if (!byReference(ti))
        {
            int count = 0;
            List propertyNames = null;
            boolean externalizable = ti.isExternalizable();

            if (!externalizable)
            {
                propertyNames = ti.getProperties();
                if (propertyNames != null)
                    count = propertyNames.size();
            }

            boolean dynamic = ti.isDynamic();

            writeUInt29(3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
            writeStringWithoutType(className);

            if (!externalizable && propertyNames != null)
            {
                for (int i = 0; i < count; i++)
                {
                    String propName = ti.getProperty(i);
                    writeStringWithoutType(propName);
                }
            }
        }
    }

    @Override
    public void writeObjectProperty(String name, Object value) throws IOException
    {
        if (isDebug)
            trace.namedElement(name);
        writeObject(value);
    }

    @Override
    public void writeObjectEnd() throws IOException
    {
        // No action required for AMF 3

        if (isDebug)
            trace.endAMFObject();
    }

    //
    // AMF SPECIFIC SERIALIZATION IMPLEMENTATIONS
    //

    /**
     * @exclude
     */
    protected void writeAMFBoolean(boolean b) throws IOException
    {
        if (isDebug)
            trace.write(b);

        if (b)
            out.write(kTrueType);
        else
            out.write(kFalseType);
    }

    /**
     * @exclude
     */
    protected void writeAMFDate(Date d) throws IOException
    {
        out.write(kDateType);

        if (!byReference(d))
        {
            if (isDebug)
                trace.write(d);

            // Write out an invalid reference
            writeUInt29(1);

            // Write the time as 64bit value in ms
            out.writeDouble(d.getTime());
        }
    }

    /**
     * @exclude
     */
    protected void writeAMFDouble(double d) throws IOException
    {
        if (isDebug)
            trace.write(d);

        out.write(kDoubleType);
        out.writeDouble(d);
    }

    /**
     * @exclude
     */
    protected void writeAMFInt(int i) throws IOException
    {
        if (i >= INT28_MIN_VALUE && i <= INT28_MAX_VALUE)
        {
            if (isDebug)
                trace.write(i);

            // We have to be careful when the MSB is set, as (value >> 3) will sign extend.
            // We know there are only 29-bits of precision, so truncate. This requires
            // similar care when reading an integer.
            // i = ((i >> 3) & UINT29_MASK);
            i = i & UINT29_MASK; // Mask is 2^29 - 1
            out.write(kIntegerType);
            writeUInt29(i);
        }
        else
        {
            // Promote large int to a double
            writeAMFDouble(i);
        }
    }

    /**
     * @exclude
     */
    protected void writeMapAsECMAArray(Map map) throws IOException
    {
        out.write(kArrayType);

        if (!byReference(map))
        {
            if (isDebug)
                trace.startECMAArray(objectTable.size() - 1);

            writeUInt29((0 << 1) | 1);

            Iterator it = map.keySet().iterator();
            while (it.hasNext())
            {
                Object key = it.next();
                if (key != null)
                {
                    String propName = key.toString();
                    writeStringWithoutType(propName);

                    if (isDebug)
                        trace.namedElement(propName);

                    writeObject(map.get(key));
                }
            }

            writeStringWithoutType(EMPTY_STRING);

            if (isDebug)
                trace.endAMFArray();
        }
    }

    /**
     * @exclude
     */
    protected void writeAMFNull() throws IOException
    {
        if (isDebug)
            trace.writeNull();

        out.write(kNullType);
    }

    /**
     * @exclude
     */
    protected void writeAMFString(String s) throws IOException
    {
        out.write(kStringType);
        writeStringWithoutType(s);

        if (isDebug)
        {
            trace.writeString(s);
        }
    }

    /**
     * @exclude
     */
    protected void writeStringWithoutType(String s) throws IOException
    {
        if (s.length() == 0)
        {
            // don't create a reference for the empty string,
            // as it's represented by the one byte value 1
            // len = 0, ((len << 1) | 1).
            writeUInt29(1);
            return;
        }

        if (!byReference(s))
        {
            writeAMFUTF(s);
            return;
        }
    }

    /**
     * @exclude
     */
    protected void writeAMFArray(Object o, Class componentType) throws IOException
    {
        if (componentType.isPrimitive())
        {
            writePrimitiveArray(o);
        }
        else if (componentType.equals(Byte.class))
        {
            writeAMFByteArray((Byte[]) o);
        }
        else if (componentType.equals(Character.class))
        {
            writeCharArrayAsString((Character[]) o);
        }
        else
        {
            writeObjectArray((Object[]) o, null);
        }
    }

    /**
     * @exclude
     */
    protected void writeArrayCollection(Collection col, SerializationDescriptor desc) throws IOException
    {
        out.write(kObjectType);

        if (!byReference(col))
        {
            ArrayCollection ac;

            if (col instanceof ArrayCollection)
            {
                ac = (ArrayCollection) col;
                // TODO: QUESTION: Pete, ignoring the descriptor here... not sure if
                // we should modify the user's AC as that could cause corruption?
            }
            else
            {
                // Wrap any Collection in an ArrayCollection
                ac = new ArrayCollection(col);
                if (desc != null)
                    ac.setDescriptor(desc);
            }

            // Then wrap ArrayCollection in PropertyProxy for bean-like serialization
            PropertyProxy proxy = PropertyProxyRegistry.getProxy(ac);
            writePropertyProxy(proxy, ac);
        }
    }

    /**
     * @exclude
     */
    protected void writeCustomObject(Object o) throws IOException
    {
        PropertyProxy proxy = null;

        if (o instanceof PropertyProxy)
        {
            proxy = (PropertyProxy) o;
            o = proxy.getDefaultInstance();

            // The proxy may wrap a null default instance, if so, short circuit here.
            if (o == null)
            {
                writeAMFNull();
                return;
            }

            // HACK: Short circuit and unwrap if PropertyProxy is wrapping an Array
            // or Collection or Map with legacyMap as true since we don't yet have
            // the ability to proxy multiple AMF types. We write an AMF Array directly
            // instead of an AMF Object...
            else if (o instanceof Collection)
            {
                if (context.legacyCollection)
                    writeCollection((Collection) o, proxy.getDescriptor());
                else
                    writeArrayCollection((Collection) o, proxy.getDescriptor());
                return;
            }
            else if (o.getClass().isArray())
            {
                writeObjectArray((Object[]) o, proxy.getDescriptor());
                return;
            }
            else if (context.legacyMap && o instanceof Map && !(o instanceof ASObject))
            {
                writeMapAsECMAArray((Map) o);
                return;
            }
        }

        out.write(kObjectType);

        if (!byReference(o))
        {
            if (proxy == null)
            {
                proxy = PropertyProxyRegistry.getProxyAndRegister(o);
            }

            writePropertyProxy(proxy, o);
        }
    }

    /**
     * @exclude
     */
    protected void writePropertyProxy(PropertyProxy proxy, Object instance) throws IOException
    {
        /*
         * At this point we substitute the instance we want to serialize.
         */
        Object newInst = proxy.getInstanceToSerialize(instance);
        if (newInst != instance)
        {
            // We can't use writeAMFNull here I think since we already added this object
            // to the object table on the server side. The player won't have any way
            // of knowing we have this reference mapped to null.
            if (newInst == null)
                throw new MessageException("PropertyProxy.getInstanceToSerialize class: " + proxy.getClass() + " returned null for instance class: " + instance.getClass().getName());

            // Grab a new proxy if necessary for the new instance
            proxy = PropertyProxyRegistry.getProxyAndRegister(newInst);
            instance = newInst;
        }

        List propertyNames = null;
        boolean externalizable = proxy.isExternalizable(instance);

        if (!externalizable)
            propertyNames = proxy.getPropertyNames(instance);

        TraitsInfo ti = new TraitsInfo(proxy.getAlias(instance), proxy.isDynamic(), externalizable, propertyNames);
        writeObjectTraits(ti);

        if (externalizable)
        {
            // Call user defined serialization
            ((Externalizable) instance).writeExternal(this);
        }
        else if (propertyNames != null)
        {
            Iterator it = propertyNames.iterator();
            while (it.hasNext())
            {
                String propName = (String) it.next();
                Object value = null;
                value = proxy.getValue(instance, propName);
                writeObjectProperty(propName, value);
            }
        }

        writeObjectEnd();
    }

    /**
     * Serialize an array of primitives.
     * <p>
     * Primitives include the following: boolean, char, double, float, long, int, short, byte
     * </p>
     * 
     * @param obj
     *            An array of primitives
     * @exclude
     */
    protected void writePrimitiveArray(Object obj) throws IOException
    {
        Class aType = obj.getClass().getComponentType();

        if (aType.equals(Character.TYPE))
        {
            // Treat char[] as a String
            char[] c = (char[]) obj;
            writeCharArrayAsString(c);
        }
        else if (aType.equals(Byte.TYPE))
        {
            writeAMFByteArray((byte[]) obj);
        }
        else
        {
            out.write(kArrayType);

            if (!byReference(obj))
            {
                if (aType.equals(Boolean.TYPE))
                {
                    boolean[] b = (boolean[]) obj;

                    // Write out an invalid reference, storing the length in the unused 28-bits.
                    writeUInt29((b.length << 1) | 1);

                    // Send an empty string to imply no named keys
                    writeStringWithoutType(EMPTY_STRING);

                    if (isDebug)
                    {
                        trace.startAMFArray(objectTable.size() - 1);

                        for (int i = 0; i < b.length; i++)
                        {
                            trace.arrayElement(i);
                            writeAMFBoolean(b[i]);
                        }

                        trace.endAMFArray();
                    }
                    else
                    {
                        for (int i = 0; i < b.length; i++)
                        {
                            writeAMFBoolean(b[i]);
                        }
                    }
                }
                else if (aType.equals(Integer.TYPE) || aType.equals(Short.TYPE))
                {
                    // We have a primitive number, either an int or short
                    // We write all of these as Integers...
                    int length = Array.getLength(obj);

                    // Write out an invalid reference, storing the length in the unused 28-bits.
                    writeUInt29((length << 1) | 1);
                    // Send an empty string to imply no named keys
                    writeStringWithoutType(EMPTY_STRING);

                    if (isDebug)
                    {
                        trace.startAMFArray(objectTable.size() - 1);

                        for (int i = 0; i < length; i++)
                        {
                            trace.arrayElement(i);
                            int v = Array.getInt(obj, i);
                            writeAMFInt(v);
                        }

                        trace.endAMFArray();
                    }
                    else
                    {
                        for (int i = 0; i < length; i++)
                        {
                            int v = Array.getInt(obj, i);
                            writeAMFInt(v);
                        }
                    }
                }
                else
                {
                    // We have a primitive number, either a double, float, or long
                    // We write all of these as doubles...
                    int length = Array.getLength(obj);

                    // Write out an invalid reference, storing the length in the unused 28-bits.
                    writeUInt29((length << 1) | 1);
                    // Send an empty string to imply no named keys
                    writeStringWithoutType(EMPTY_STRING);

                    if (isDebug)
                    {
                        trace.startAMFArray(objectTable.size() - 1);

                        for (int i = 0; i < length; i++)
                        {
                            trace.arrayElement(i);
                            double v = Array.getDouble(obj, i);
                            writeAMFDouble(v);
                        }

                        trace.endAMFArray();
                    }
                    else
                    {
                        for (int i = 0; i < length; i++)
                        {
                            double v = Array.getDouble(obj, i);
                            writeAMFDouble(v);
                        }
                    }
                }
            }
        }
    }

    /**
     * @exclude
     */
    protected void writeAMFByteArray(byte[] ba) throws IOException
    {
        out.write(kByteArrayType);

        if (!byReference(ba))
        {
            int length = ba.length;

            // Write out an invalid reference, storing the length in the unused 28-bits.
            writeUInt29((length << 1) | 1);

            if (isDebug)
            {
                trace.startByteArray(objectTable.size() - 1, length);
            }

            out.write(ba, 0, length);
        }
    }

    /**
     * @exclude
     */
    protected void writeAMFByteArray(Byte[] ba) throws IOException
    {
        out.write(kByteArrayType);

        if (!byReference(ba))
        {
            int length = ba.length;

            // Write out an invalid reference, storing the length in the unused 28-bits.
            writeUInt29((length << 1) | 1);

            if (isDebug)
            {
                trace.startByteArray(objectTable.size() - 1, length);
            }

            for (int i = 0; i < ba.length; i++)
            {
                Byte b = ba[i];
                if (b == null)
                    out.write(0);
                else
                    out.write(b.byteValue());
            }
        }
    }

    /**
     * @exclude
     */
    protected void writeCharArrayAsString(Character[] ca) throws IOException
    {
        int length = ca.length;
        char[] chars = new char[length];

        for (int i = 0; i < length; i++)
        {
            Character c = ca[i];
            if (c == null)
                chars[i] = 0;
            else
                chars[i] = ca[i].charValue();
        }
        writeCharArrayAsString(chars);
    }

    /**
     * @exclude
     */
    protected void writeCharArrayAsString(char[] ca) throws IOException
    {
        String str = new String(ca);
        writeAMFString(str);
    }

    /**
     * @exclude
     */
    protected void writeObjectArray(Object[] values, SerializationDescriptor descriptor) throws IOException
    {
        out.write(kArrayType);

        if (!byReference(values))
        {
            if (isDebug)
                trace.startAMFArray(objectTable.size() - 1);

            writeUInt29((values.length << 1) | 1);

            // Send an empty string to imply no named keys
            writeStringWithoutType(EMPTY_STRING);

            for (int i = 0; i < values.length; ++i)
            {
                if (isDebug)
                    trace.arrayElement(i);

                Object item = values[i];
                if (item != null && descriptor != null && !(item instanceof String) && !(item instanceof Number) && !(item instanceof Boolean) && !(item instanceof Character))
                {
                    PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
                    proxy = (PropertyProxy) proxy.clone();
                    proxy.setDescriptor(descriptor);
                    proxy.setDefaultInstance(item);
                    item = proxy;
                }
                writeObject(item);
            }

            if (isDebug)
                trace.endAMFArray();
        }
    }

    /**
     * @exclude
     */
    protected void writeCollection(Collection c, SerializationDescriptor descriptor) throws IOException
    {
        out.write(kArrayType);

        // Note: We process Collections independently of Object[]
        // as we want the reference to be based on the actual
        // Collection.
        if (!byReference(c))
        {
            if (isDebug)
                trace.startAMFArray(objectTable.size() - 1);

            writeUInt29((c.size() << 1) | 1);

            // Send an empty string to imply no named keys
            writeStringWithoutType(EMPTY_STRING);

            Iterator it = c.iterator();
            int i = 0;
            while (it.hasNext())
            {
                if (isDebug)
                    trace.arrayElement(i);

                Object item = it.next();

                if (item != null && descriptor != null && !(item instanceof String) && !(item instanceof Number) && !(item instanceof Boolean) && !(item instanceof Character))
                {
                    PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
                    proxy = (PropertyProxy) proxy.clone();
                    proxy.setDescriptor(descriptor);
                    proxy.setDefaultInstance(item);
                    item = proxy;
                }
                writeObject(item);

                i++;
            }

            if (isDebug)
                trace.endAMFArray();
        }
    }

    /**
     * @exclude
     */
    protected void writeUInt29(int ref) throws IOException
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
        if (ref < 0x80)
        {
            // 0x00000000 - 0x0000007F : 0xxxxxxx
            out.writeByte(ref);
        }
        else if (ref < 0x4000)
        {
            // 0x00000080 - 0x00003FFF : 1xxxxxxx 0xxxxxxx
            out.writeByte(((ref >> 7) & 0x7F) | 0x80);
            out.writeByte(ref & 0x7F);

        }
        else if (ref < 0x200000)
        {
            // 0x00004000 - 0x001FFFFF : 1xxxxxxx 1xxxxxxx 0xxxxxxx
            out.writeByte(((ref >> 14) & 0x7F) | 0x80);
            out.writeByte(((ref >> 7) & 0x7F) | 0x80);
            out.writeByte(ref & 0x7F);

        }
        else if (ref < 0x40000000)
        {
            // 0x00200000 - 0x3FFFFFFF : 1xxxxxxx 1xxxxxxx 1xxxxxxx xxxxxxxx
            out.writeByte(((ref >> 22) & 0x7F) | 0x80);
            out.writeByte(((ref >> 15) & 0x7F) | 0x80);
            out.writeByte(((ref >> 8) & 0x7F) | 0x80);
            out.writeByte(ref & 0xFF);

        }
        else
        {
            // 0x40000000 - 0xFFFFFFFF : throw range exception
            throw new MessageException("Integer out of range: " + ref);
        }
    }

    /**
     * @exclude
     */
    public void writeAMFUTF(String s) throws IOException
    {
        int strlen = s.length();
        int utflen = 0;
        int c, count = 0;

        char[] charr = getTempCharArray(strlen);
        s.getChars(0, strlen, charr, 0);

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

        writeUInt29((utflen << 1) | 1);

        byte[] bytearr = getTempByteArray(utflen);

        for (int i = 0; i < strlen; i++)
        {
            c = charr[i];
            if (c <= 0x007F)
            {
                bytearr[count++] = (byte) c;
            }
            else if (c > 0x07FF)
            {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
            else
            {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        out.write(bytearr, 0, utflen);
    }

    /**
     * Attempts to serialize the object as a reference. If the object cannot be serialized as a reference, it is stored in the reference collection for potential future encounter.
     * 
     * @return Success/failure indicator as to whether the object could be serialized as a reference.
     * @exclude
     */
    protected boolean byReference(Object o) throws IOException
    {
        Object ref = objectTable.get(o);

        if (ref != null)
        {
            try
            {
                int refNum = ((Integer) ref).intValue();

                if (isDebug)
                    trace.writeRef(refNum);

                writeUInt29(refNum << 1);
            }
            catch (ClassCastException e)
            {
                throw new IOException("Object reference is not an Integer");
            }
        }
        else
        {
            objectTable.put(o, new Integer(objectTable.size()));
        }

        return (ref != null);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // */
    // public void addObjectReference(Object o) throws IOException
    // {
    // byReference(o);
    // }

    /**
     * @exclude
     */
    protected boolean byReference(String s) throws IOException
    {
        Object ref = stringTable.get(s);

        if (ref != null)
        {
            try
            {
                int refNum = ((Integer) ref).intValue();

                writeUInt29(refNum << 1);

                if (Trace.amf && isDebug)
                {
                    trace.writeStringRef(refNum);
                }
            }
            catch (ClassCastException e)
            {
                throw new IOException("String reference is not an Integer");
            }
        }
        else
        {
            stringTable.put(s, new Integer(stringTable.size()));
        }

        return (ref != null);
    }

    /**
     * @exclude
     */
    protected boolean byReference(TraitsInfo ti) throws IOException
    {
        Object ref = traitsTable.get(ti);

        if (ref != null)
        {
            try
            {
                int refNum = ((Integer) ref).intValue();

                writeUInt29((refNum << 2) | 1);

                if (Trace.amf && isDebug)
                {
                    trace.writeTraitsInfoRef(refNum);
                }
            }
            catch (ClassCastException e)
            {
                throw new IOException("TraitsInfo reference is not an Integer");
            }
        }
        else
        {
            traitsTable.put(ti, new Integer(traitsTable.size()));
        }

        return (ref != null);
    }
}
