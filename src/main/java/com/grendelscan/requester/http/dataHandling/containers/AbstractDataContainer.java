/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.util.*;

import com.grendelscan.requester.http.dataHandling.data.*;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.utils.StringUtils;


/**
 * @author david
 *
 */
public abstract class AbstractDataContainer<ReferenceType extends DataReference> extends AbstractData 
	implements DataContainer<ReferenceType>
{
	private static final long	serialVersionUID	= 1L;
	protected final List<Data> children;
	
	
	public AbstractDataContainer(DataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(parent, reference, transactionId);
		children = new ArrayList<Data>(1);
	}


	@Override
	public synchronized Data[] getDataChildren()
	{
		return children.toArray(new Data[children.size()]);
	}


	@Override
	public void removeChild(Data child)
	{
		children.remove(child);
	}

	@Override public void setTransactionId(int transactionId)
	{
		super.setTransactionId(transactionId);
		for(Data child: getDataChildren())
		{
			child.setTransactionId(transactionId);
		}
	}


	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Children:");
		for(Data child: children)
		{
			sb.append("\n");
			sb.append(StringUtils.indentLines(child.debugString(), 1));
		}
		return sb.toString();
	}


}
