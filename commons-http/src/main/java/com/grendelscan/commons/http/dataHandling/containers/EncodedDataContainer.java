/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.jgroups.util.ExposedByteArrayOutputStream;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.formatting.DataEncodingStream;
import com.grendelscan.commons.formatting.DataFormat;
import com.grendelscan.commons.formatting.DataFormatException;
import com.grendelscan.commons.formatting.DataFormatUtils;
import com.grendelscan.commons.http.dataHandling.DataParser;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.SingleChildReference;

/**
 * @author david
 * 
 */
public class EncodedDataContainer extends AbstractData implements DataContainer<SingleChildReference>
{

    private static final long serialVersionUID = 1L;
    private final DataFormat format;
    private Data child;
    private final boolean mutableChildren;

    /**
     * @param transactionId
     * @throws DataFormatException
     */
    public EncodedDataContainer(final DataContainer<?> parent, final byte[] encodedData, final DataFormat format, final int transactionId, final boolean mutableChildren) throws DataFormatException
    {
        super(parent, transactionId);
        this.format = format;
        this.mutableChildren = mutableChildren;
        byte[] decodedData = DataFormatUtils.decodeData(encodedData, format.formatType);
        child = DataParser.parseRawData(decodedData, this, (DataFormat) null, getTransactionId(), mutableChildren);
    }

    //
    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.data.Data#clone(com.grendelscan.commons.http.dataHandling.containers.DataContainer)
    // */
    // @Override
    // public Data clone(TransactionContainer transaction)
    // {
    // DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
    // try
    // {
    // return new EncodedDataContainer(parentClone, getBytes(), format, getReference().clone(), transaction.getTransactionId());
    // }
    // catch (DataFormatException e)
    // {
    // LOGGER.error("Problem cloning: " + e.toString(), e);
    // throw new RuntimeException("Very odd problem cloning", e);
    // }
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
     */
    @Override
    public String childrenDebugString()
    {
        // Not usually called directly
        return child.debugString();
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
        sb.append("EncodedDataContainer\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("\n\tFormat: ");
        sb.append(format.formatType);
        sb.append("\n\tRaw Data:\n");
        sb.append(StringUtils.indentLines(child.debugString(), 2));

        ExposedByteArrayOutputStream out = new ExposedByteArrayOutputStream();
        writeBytes(out);

        sb.append("\n\tEncoded Data:\n");
        sb.append(StringUtils.indentLines(out.toString(), 2));
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(@SuppressWarnings("unused") final SingleChildReference reference)
    {
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public SingleChildReference getChildsReference(final Data child)
    {
        if (child == this.child)
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
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        // Child should never be null
        return new Data[] { child };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(final Data child)
    {
        throw new NotImplementedException("Doesn't make sense for an encoded data container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(@SuppressWarnings("unused") final SingleChildReference reference, final Data child)
    {
        this.child = child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        DataEncodingStream des = new DataEncodingStream(out, format);
        child.writeBytes(des);
    }
}
