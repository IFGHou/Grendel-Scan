/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grendelscan.requester.http.dataHandling.containers.AbstractDataContainer;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.containers.ExpandableDataContainer;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.ArbitraryChildren;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.collections.BidiMap;

/**
 * @author david
 *
 */
public abstract class AbstractAmfNamedDataContainer extends AbstractAmfDataContainer<NamedDataContainerDataReference> 
	implements ArbitraryChildren, ExpandableDataContainer<NamedDataContainerDataReference>
{
	protected BidiMap<byte[], NamedAmfDataContainer> properties;
	/**
	 * @param name
	 * @param type
	 * @param parent
	 * @param forceAmf3Code
	 * @param reference
	 * @param transactionId
	 */
	public AbstractAmfNamedDataContainer(String name, AmfDataType type, AbstractAmfDataContainer<?> parent, 
			boolean forceAmf3Code, int transactionId)
	{
		super(name, type, parent, forceAmf3Code, transactionId);
		properties = new BidiMap<byte[], NamedAmfDataContainer>(1);
//		properties = new BidiMap<byte[], NamedAmfDataContainer>(1);
	}

	private static final long	serialVersionUID	= 1L;



	public AbstractAmfData getChild(String name)
	{
		NamedAmfDataContainer param = properties.get(name.getBytes());
		if (param == null)
			return null;
		return param.getValueData();
	}

	public Data putChild(String name, AbstractAmfData child)
	{
		child.setName(name);
		NamedAmfDataContainer container = new NamedAmfDataContainer(this, getTransactionId(), name, child);
		NamedAmfDataContainer oldValue = properties.put(name.getBytes(), container);
//		Data oldValue = properties.put(name.getBytes(), child);
		if (oldValue == null)
			return null;
		return oldValue;
	}

	public AbstractAmfData removeChild(String name)
	{
		NamedAmfDataContainer oldValue = properties.remove(name.getBytes());
		if (oldValue == null || oldValue.getValueData().isDeletable())
			return null;
		return oldValue.getValueData();
	}

	@Override
	public int getSize()
	{
		return properties.size();
	}

	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(properties.size());
		for(NamedAmfDataContainer data: properties.getSortedValues())
		{
			children.add(data.getValueData());
		}
		return children;
	}
	

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return properties.getSortedValues().toArray(new Data[properties.size()]);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		NamedAmfDataContainer amfChild = (NamedAmfDataContainer) child;
		if (amfChild.getValueData().isDeletable())
			properties.removeValue(amfChild);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(NamedDataContainerDataReference reference)
	{
		return properties.get(reference.getName());
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((NamedDataContainerDataReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void addChild(@SuppressWarnings("unused") NamedDataContainerDataReference reference, Data child)
	{
		addChild(child);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void addChild(Data child)
	{
		if (! (child instanceof NamedAmfDataContainer))
		{
			throw new IllegalArgumentException("Children must be of type NamedAmfDataContainer");
		}
		NamedAmfDataContainer amfChild = (NamedAmfDataContainer) child;
		properties.put(DataUtils.getBytes(amfChild.getNameData()), amfChild);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(@SuppressWarnings("unused") NamedDataContainerDataReference reference, Data child)
	{
		addChild(child);
	}

	public List<byte[]> getChildNames()
	{
		return properties.getSortedKeys();
	}

	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("AMF properties:");
		for(byte[] key: properties.keySet())
		{
			sb.append("\n\tkey: ");
			sb.append(key);
			sb.append("\nvalue:\n");
			sb.append(StringUtils.indentLines(properties.get(key).debugString(), 2));
		}
		return sb.toString();
	}

	@Override
	public NamedDataContainerDataReference getChildsReference(Data child)
	{
		if (properties.containsValue(child))
		{
			return new NamedDataContainerDataReference(properties.getKey((NamedAmfDataContainer) child));
		}
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}

	@Override
	public boolean isMutable()
	{
		return true;
	}
}
