/**
 * 
 */
package com.grendelscan.commons.flex;

import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;

/**
 * @author david
 * 
 */
public class NamedAmfDataContainer extends AbstractData implements NameValuePairDataContainer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedAmfDataContainer.class);

    private static final long serialVersionUID = 1L;

    private Data name;

    private AbstractAmfData value;

    /**
     * @param parent
     * @param reference
     * @param transactionId
     */
    public NamedAmfDataContainer(final DataContainer<?> parent, final int transactionId, final Data name, final AbstractAmfData value)
    {
        super(parent, transactionId);
        this.name = name;
        this.value = value;
    }

    public NamedAmfDataContainer(final DataContainer<?> parent, final int transactionId, final String name, final AbstractAmfData value)
    {
        super(parent, transactionId);
        this.name = new ByteData(this, name.getBytes(), transactionId, false);
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
     */
    @Override
    public String childrenDebugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Named AMF data container-\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("Name:\n");
        sb.append(StringUtils.indentLines(name.debugString(), 1));
        sb.append("\nValue:\n");
        sb.append(StringUtils.indentLines(value.debugString(), 1));
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#debugString()
     */
    @Override
    public String debugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("NamedAmfDataContainer:\n");
        sb.append(StringUtils.indentLines(childrenDebugString(), 1));
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final NameOrValueReference reference)
    {
        if (reference.isName())
        {
            return name;
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public NameOrValueReference getChildsReference(final Data child)
    {
        if (child == name)
        {
            return NameOrValueReference.NAME;
        }
        else if (child == value)
        {
            return NameOrValueReference.VALUE;
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
        return getChild((NameOrValueReference) reference);
    }

    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.data.Data#setBytes(byte[])
    // */
    // @Override
    // public void setBytes(@SuppressWarnings("unused") byte[] bytes)
    // {
    // throw new NotImplementedException("Not used in AMF");
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public synchronized Data[] getDataChildren()
    {
        return new Data[] { name, value };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.http.NameValuePair#getName()
     */
    @Override
    public String getName()
    {
        throw new NotImplementedException("Not used in AMF");
    }

    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#getNameBytes()
    // */
    // @Override
    // public byte[] getNameBytes()
    // {
    // return name.getBytes();
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#getNameData()
     */
    @Override
    public Data getNameData()
    {
        return name;
    }

    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#getValueBytes()
    // */
    // @Override
    // public byte[] getValueBytes()
    // {
    // return value.getBytes();
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.http.NameValuePair#getValue()
     */
    @Override
    public String getValue()
    {
        throw new NotImplementedException("Not used in AMF");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#getValueData()
     */
    @Override
    public AbstractAmfData getValueData()
    {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(@SuppressWarnings("unused") final Data child)
    {
        throw new NotImplementedException("Remove child makes no sense on a named AMF container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final NameOrValueReference reference, final Data child)
    {
        if (reference.isName())
        {
            name = child;
        }
        else
        {
            value = (AbstractAmfData) child;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#setName(byte[])
     */
    @Override
    public void setName(final byte[] name)
    {
        throw new NotImplementedException("Not used in AMF");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.NamedDataContainer#setValue(byte[])
     */
    @Override
    public void setValue(@SuppressWarnings("unused") final byte[] value)
    {
        throw new NotImplementedException("Not used in AMF");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        throw new NotImplementedException("Not used in AMF");
    }

}
