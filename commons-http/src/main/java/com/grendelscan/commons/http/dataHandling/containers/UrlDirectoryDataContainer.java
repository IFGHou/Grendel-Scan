/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NumberedListDataReference;

/**
 * @author david
 * 
 */
public class UrlDirectoryDataContainer extends AbstractDataContainer<NumberedListDataReference> implements ExpandableDataContainer<NumberedListDataReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlDirectoryDataContainer.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final boolean mutable;

    /**
     * @param parent
     * @param transactionId
     */
    public UrlDirectoryDataContainer(final DataContainer<?> parent, final int transactionId, final String path, final boolean mutable)
    {
        super(parent, transactionId);
        setPath(path);
        this.mutable = mutable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(final Data child)
    {
        children.add(child);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(final NumberedListDataReference reference, final Data child)
    {
        children.set(reference.getIndex(), child);
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
        sb.append("UrlDirectoryDataContainer -\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("\n");
        sb.append(StringUtils.indentLines(childrenDebugString(), 1));
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final NumberedListDataReference reference)
    {
        return children.get(reference.getIndex());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public NumberedListDataReference getChildsReference(final Data child)
    {
        int index = children.indexOf(child);
        if (index < 0)
        {
            throw new IllegalArgumentException("The passed data object is not a child of this container");
        }
        return new NumberedListDataReference(index);
    }

    // @Override
    // public UrlDirectoryDataContainer clone(TransactionContainer transaction)
    // {
    // DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
    // return new UrlDirectoryDataContainer(parentClone, getReference().clone(), transaction.getTransactionId(), new String(getBytes()));
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return getChild((NumberedListDataReference) reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.ModifiableData#isMutable()
     */
    @Override
    public boolean isMutable()
    {
        return mutable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final NumberedListDataReference reference, final Data child)
    {
        children.set(reference.getIndex(), child);
    }

    private void setPath(final String path)
    {
        children.clear();
        for (String d : path.split("/"))
        {
            children.add(new ByteData(this, d.getBytes(), getTransactionId(), true));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        for (Data child : children)
        {
            try
            {
                out.write('/');
                child.writeBytes(out);
                out.write('/');
            }
            catch (IOException e)
            {
                LOGGER.error("Problem writing path component: " + e.toString(), e);
            }
        }
    }

}
