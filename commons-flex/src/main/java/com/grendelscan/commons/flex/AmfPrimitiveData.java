package com.grendelscan.commons.flex;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.SingleChildReference;

public class AmfPrimitiveData extends AbstractAmfData implements DataContainer<SingleChildReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfPrimitiveData.class);
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private Data data;

    public AmfPrimitiveData(final String name, final AmfDataType type, final byte[] data, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        super(name, type, parent, false, transactionId);
        this.data = new ByteData(parent, data, transactionId, mutable);
    }

    public AmfPrimitiveData(final String name, final boolean data, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        super(name, data ? AmfDataType.kTrue : AmfDataType.kFalse, parent, false, transactionId);
        this.data = new ByteData(parent, Boolean.toString(data).getBytes(), transactionId, mutable);
    }

    /*
     * TODO UCdetector: Remove unused code: public AmfPrimitiveData(String name, byte[] data, AbstractAmfData parent) { super(name, AmfDataType.kByteArray, parent); this.data = new String(data,
     * StringUtils.getDefaultCharset()); }
     */

    public AmfPrimitiveData(final String name, final byte[] data, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        super(name, AmfDataType.kString, parent, false, transactionId);
        this.data = new ByteData(parent, data, transactionId, mutable);
    }

    public AmfPrimitiveData(final String name, final double data, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        super(name, AmfDataType.kDouble, parent, false, transactionId);
        this.data = new ByteData(parent, Double.toString(data).getBytes(), transactionId, mutable);
    }

    public AmfPrimitiveData(final String name, final int data, final AbstractAmfDataContainer<?> parent, final int transactionId, final boolean mutable)
    {
        super(name, AmfDataType.kInteger, parent, false, transactionId);
        this.data = new ByteData(parent, Integer.toString(data).getBytes(), transactionId, mutable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
     */
    @Override
    public String childrenDebugString()
    {
        return data.debugString();
    }

    // int kUndefinedType = 0;
    // int kNullType = 1;
    // int kFalseType = 2;
    // int kTrueType = 3;
    // int kXMLType = 7;
    // int kDateType = 8;
    // int kArrayType = 9;
    // int kObjectType = 10;
    // int kAvmPlusXmlType = 11;
    // int kByteArrayType = 12;

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final SingleChildReference reference)
    {
        return data;
    }

    @Override
    public ArrayList<AbstractAmfData> getChildren()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public SingleChildReference getChildsReference(final Data child)
    {
        if (child == data)
        {
            return SingleChildReference.getInstance();
        }
        throw new IllegalArgumentException("The passed data object is not a child of this container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        return new Data[] { data };
    }

    public String getValue()
    {
        return toString();
    }

    @Override
    public boolean isValueLocked()
    {
        return getType().equals(AmfDataType.kNull) ? true : super.isValueLocked();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(final Data child)
    {
        throw new IllegalArgumentException("Remove child doesn't make sense for a primitive data type");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final SingleChildReference reference, final Data child)
    {
        data = child;
    }

    public void setValue(final String data)
    {
        this.data = new ByteData(this, data.getBytes(), this.data.getTransactionId(), true);
    }

    @Override
    public String toString()
    {
        return data.toString();
    }

    @Override
    public void writeBytes(final OutputStream out)
    {
        AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);

        if (data == null)
        {
            setType(AmfDataType.kNull);
        }
        try
        {
            // null, true and false are only data types, no real data
            switch (getType())
            {
                case kDouble:
                    writeCodeToStream(outputStream);
                    outputStream.writeDouble(Double.valueOf(toString()));
                    break;
                case kInteger:
                    AmfUtils.writeAMFInt(outputStream, Integer.valueOf(toString()), useAmf3Code);
                    break;
                case kString:
                case kXML:
                case kAvmPlusXml:
                    // if (data.equals(""))
                    // {
                    // AmfDataType.kNull.writeCode(outputStream, useAmf3Code);
                    // }
                    // else
                    // {
                    writeCodeToStream(outputStream);
                    AmfUtils.writeAMFUTF(outputStream, false, data.toString(), useAmf3Code);
                    // }
                    break;
                case kTrue:
                case kFalse:
                case kBoolean:
                    AmfUtils.writeBoolean(outputStream, Boolean.valueOf(toString()), useAmf3Code);
                    break;
                // for null
                default:
                    writeCodeToStream(outputStream);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Problem writing AMF: " + e.toString(), e);
        }
    }
}
