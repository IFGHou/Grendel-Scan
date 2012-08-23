/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NumberedListDataReference;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class UrlDirectoryDataContainer extends AbstractDataContainer<NumberedListDataReference> 
	implements ExpandableDataContainer<NumberedListDataReference>
{
	

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @param parent
	 * @param reference
	 * @param transactionId
	 */
	public UrlDirectoryDataContainer(DataContainer<?> parent, DataReference reference, int transactionId, String path)
	{
		super(parent, reference, transactionId);
		setPath(path);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(NumberedListDataReference reference)
	{
		return children.get(reference.getIndex());
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void addChild(NumberedListDataReference reference, Data child)
	{
		children.set(reference.getIndex(), child);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void addChild(Data child)
	{
		children.add(child);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(NumberedListDataReference reference, Data child)
	{
		children.set(reference.getIndex(), child);
	}


	private void setPath(String path)
	{
		children.clear();
		int i = 0;
		for(String d: path.split("/"))
		{
			children.add(new ByteData(this, d.getBytes(), new NumberedListDataReference(i++), getTransactionId()));
		}
	}
	
//	@Override
//	public UrlDirectoryDataContainer clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new UrlDirectoryDataContainer(parentClone, getReference().clone(), transaction.getTransactionId(), new String(getBytes()));
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((NumberedListDataReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		for(Data child: children)
		{
			try
			{
				out.write('/');
				child.writeBytes(out);
				out.write('/');
			}
			catch (IOException e)
			{
				Log.error("Problem writing path component: " + e.toString(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UrlDirectoryDataContainer\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return sb.toString();
	}

}
