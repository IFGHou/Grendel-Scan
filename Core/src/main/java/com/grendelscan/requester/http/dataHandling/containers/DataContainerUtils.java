/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;

/**
 * @author david
 *
 */
public class DataContainerUtils
{
	public static List<DataContainer<?>> getContainerChildren(DataContainer<?> container)
	{
		List<DataContainer<?>> containers = new ArrayList<DataContainer<?>>();
		for(Data d: container.getDataChildren())
		{
			if (d instanceof DataContainer)
			{
				containers.add((DataContainer<?>) d);
			}
		}
		return containers;
	}


	public static List<Data> getAllDataDescendents(DataContainer<?> container)
	{
		List<Data> data = new ArrayList<Data>();
		addData(data, container, false);
		return data;
	}
	
	public static List<Data> getAllMutableDataDescendents(DataContainer<?> container)
	{
		List<Data> data = new ArrayList<Data>();
		addData(data, container, true);
		return data;
	}
	



	public static List<NameValuePairDataContainer> getAllNamedContaners(DataContainer<?> container)
	{
		List<NameValuePairDataContainer> containers = new ArrayList<NameValuePairDataContainer>();
		addContainers(containers, container, NameValuePairDataContainer.class);
		return containers;
	}

	public static List<NameValuePairDataContainer> getAllNamedContanersByName(DataContainer<?> container, String name)
	{
		List<NameValuePairDataContainer> containers = new ArrayList<NameValuePairDataContainer>();
		addByName(containers, container, name);
		return containers;
	}

	public static NameValuePairDataContainer getFirstNamedContanerByName(DataContainer<?> container, String name)
	{
		return getAllNamedContanersByName(container, name).get(0);
	}

	private static void addByName(List<NameValuePairDataContainer> list, DataContainer<?> container, String name)
	{
		for(DataContainer<?> child: getContainerChildren(container))
		{
			if (child instanceof NameValuePairDataContainer)
			{
				String n = new String(DataUtils.getBytes(((NameValuePairDataContainer) child).getNameData()));
				if (name.equals(n))
					list.add((NameValuePairDataContainer) child);
			}
			addByName(list, child, name);
		}
	}
	

	private static <D extends DataContainer<?>> void addContainers(List<D> list, DataContainer<?> container, Class<D> clazz)
	{
		for(DataContainer<?> child: getContainerChildren(container))
		{
			if (clazz.isInstance(child))
			{
				list.add((D) child);
			}
			addContainers(list, child, clazz);
		}
	}
	
	private static void addData(List<Data> list, DataContainer<?> container, boolean mutableOnly)
	{
		for(Data child: container.getDataChildren())
		{
			if (child instanceof DataContainer)
			{
				addData(list, (DataContainer<?>)child, mutableOnly);
			}
			if (child instanceof EncodedDataContainer || !(child instanceof DataContainer))
			{
				if (child instanceof MutableData)
				{
					if (((MutableData) child).isMutable())
					{
						list.add(child);
					}
				}
				else
				{
					list.add(child);
				}
			}
		}
	}
	
	public static DataReferenceChain getReferenceChain(Data data)
	{
		DataReferenceChain chain = new DataReferenceChain();
		chain.add(data.getReference());
		DataContainer<?> parent = data.getParent();
		while (parent != null && !(parent instanceof TransactionContainer))
		{
			chain.add(chain.size(), parent.getReference());
			parent = parent.getParent();
		}
		return chain;
	}
	
//	public static Data resolveReferenceCousin(StandardHttpTransaction transaction, Data datum)
//	{
//		return resolveReferenceChain(transaction.getTransactionContainer(), datum.getReferenceChain());
//	}

	public static Data resolveReferenceChain(TransactionContainer root, DataReferenceChain chain)
	{
		DataContainer<?> current = root;
		int i = chain.size();
		while (current != null)
		{
			DataReference ref = chain.get(--i);
			Data child = current.getChildUnsafeType(ref);
			if (i == 0)
			{
				return child;
				
			}
			if (child instanceof DataContainer)
			{
				current = (DataContainer<?>) child;
			}
			else
			{
				throw new IllegalArgumentException("Chain doesn't resolve");
			}
		}
		throw new IllegalArgumentException("Chain doesn't resolve");
	}

}
