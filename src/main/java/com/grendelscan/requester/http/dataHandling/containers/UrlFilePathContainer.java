/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.UrlPathDataReference;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * @author david
 *
 */
public class UrlFilePathContainer extends AbstractData implements DataContainer<UrlPathDataReference>
{

	private static final long	serialVersionUID	= 1L;
	private UrlDirectoryDataContainer dirContainer;
	private UrlFileNameDataContainer fileContainer;
	
	/**
	 * @param parent
	 * @param reference
	 * @param transactionId
	 */
	public UrlFilePathContainer(DataContainer<?> parent, DataReference reference, int transactionId, String path)
	{
		super(parent, reference, transactionId);
		setPath(path);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(UrlPathDataReference reference)
	{
		if (reference.isDirectory())
			return dirContainer;
		return fileContainer;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(UrlPathDataReference reference, Data child)
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


	private void setPath(String path)
	{
		try
		{
			String dir = URIStringUtils.getDirectory(path);
			dirContainer = new UrlDirectoryDataContainer(this, new UrlPathDataReference(true), getTransactionId(), dir);
			String file = URIStringUtils.getFilename(path);
			fileContainer = new UrlFileNameDataContainer(this, new UrlPathDataReference(false), getTransactionId(), file);
		}
		catch (URISyntaxException e)
		{
			throw new IllegalArgumentException("Odd problem parsing path(" + path + "): " + e.toString(), e);
		}

	}

//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.data.Data#clone(com.grendelscan.requester.http.transactions.StandardHttpTransaction)
//	 */
//	@Override
//	public UrlFilePathContainer clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new UrlFilePathContainer(parentClone, getReference().clone(), transaction.getTransactionId(), new String(getBytes()));
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((UrlPathDataReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		dirContainer.writeBytes(out);
		fileContainer.writeBytes(out);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[]{dirContainer, fileContainer};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		throw new NotImplementedException("removeChild not implemented in URLFilePathContainer");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
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

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
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

}
