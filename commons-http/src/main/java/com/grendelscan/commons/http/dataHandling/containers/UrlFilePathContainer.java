/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.UrlPathDataReference;

/**
 * @author david
 * 
 */
public class UrlFilePathContainer extends AbstractData implements DataContainer<UrlPathDataReference>
{

    private static final long serialVersionUID = 1L;
    private UrlDirectoryDataContainer dirContainer;
    private UrlFileNameDataContainer fileContainer;

    /**
     * @param parent
     * @param transactionId
     */
    public UrlFilePathContainer(final DataContainer<?> parent, final int transactionId, final String path)
    {
        super(parent, transactionId);
        setPath(path);
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
        sb.append("Directory container:\n");
        sb.append(StringUtils.indentLines(dirContainer.debugString(), 1));
        sb.append("\nFilename container:\n");
        sb.append(StringUtils.indentLines(fileContainer.debugString(), 1));
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
        sb.append("UrlFilePathContainer -\n");
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
    public Data getChild(final UrlPathDataReference reference)
    {
        if (reference.isDirectory())
        {
            return dirContainer;
        }
        return fileContainer;
    }

    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.data.Data#clone(com.grendelscan.commons.http.transactions.StandardHttpTransaction)
    // */
    // @Override
    // public UrlFilePathContainer clone(TransactionContainer transaction)
    // {
    // DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
    // return new UrlFilePathContainer(parentClone, getReference().clone(), transaction.getTransactionId(), new String(getBytes()));
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public UrlPathDataReference getChildsReference(final Data child)
    {
        if (child == dirContainer)
        {
            return UrlPathDataReference.DIRECTORY_COMPONENT;
        }
        else if (child == fileContainer)
        {
            return UrlPathDataReference.FILENAME_COMPONENT;
        }
        throw new IllegalArgumentException("The data object passed is not a child of this container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return getChild((UrlPathDataReference) reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        if (fileContainer != null)
        {
            return new Data[] { dirContainer, fileContainer };
        }
        return new Data[] { dirContainer };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(final Data child)
    {
        throw new NotImplementedException("removeChild not implemented in URLFilePathContainer");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final UrlPathDataReference reference, final Data child)
    {
        if (reference.isDirectory())
        {
            if (child instanceof UrlDirectoryDataContainer)
            {
                dirContainer = (UrlDirectoryDataContainer) child;
                return;
            }
            throw new IllegalArgumentException("Child must be of type UrlDirectoryDataContainer");
        }
        if (child instanceof UrlFileNameDataContainer)
        {
            fileContainer = (UrlFileNameDataContainer) child;
            return;
        }
        throw new IllegalArgumentException("Child must be of type UrlFileNameDataContainer");
    }

    private void setPath(final String path)
    {
        try
        {
            String dir = URIStringUtils.getDirectory(path);
            dirContainer = new UrlDirectoryDataContainer(this, getTransactionId(), dir, true);
            String file = URIStringUtils.getFilename(path);
            fileContainer = new UrlFileNameDataContainer(this, getTransactionId(), file);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Odd problem parsing path(" + path + "): " + e.toString(), e);
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
        dirContainer.writeBytes(out);
        fileContainer.writeBytes(out);
    }

}
