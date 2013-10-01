/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.data;

import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public abstract class AbstractData implements Data
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private DataContainer<?> parent;
	private int transactionId;
	public AbstractData(DataContainer<?> parent, int transactionId)
	{
		this.parent = parent;
		this.transactionId = transactionId;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataContainers.Data#getParent()
	 */
	@Override
	public DataContainer<?> getParent()
	{
		return parent;
	}
	
	@Override public void setTransactionId(int transactionId)
	{
		this.transactionId = transactionId;
	}
	
	protected void setParent(DataContainer<?> parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean isDataAncestor(DataContainer<?> container)
	{
		DataContainer<?> ancestor = parent;
		while (ancestor != null)
		{
			if (ancestor == container)
				return true;
			ancestor = ancestor.getParent();
		}
		return false;
	}

	@Override
	public void removeFromCollection()
	{
		parent.removeChild(this);
	}

//	@Override
//	public DataReference getReference()
//	{
//		return reference;
//	}

//	public final void setReference(DataReference reference)
//	{
//		this.reference = reference;
//	}

	@Override public final int getTransactionId()
	{
		return transactionId;
	}

	@Override
	public DataReferenceChain getReferenceChain()
	{
		return DataContainerUtils.getReferenceChain(this);
	}

	public String abstractDataDebugString()
	{
		StringBuilder sb = new StringBuilder();
//		sb.append("Parent:\n");
//		if (parent == null)
//		{
//			sb.append("\t<null>\n");
//		}
//		else
//		{
//			sb.append(StringUtils.indentLines(parent.debugString(), 1));
//		}
		sb.append("Reference: ");
		sb.append(getReference().debugString());
		sb.append("\nTransaction ID: ");
		sb.append(transactionId);
		return sb.toString();
	}

	@Override
	public DataReference getReference()
	{
		return parent.getChildsReference(this);
	}

}
