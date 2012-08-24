/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
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
		addData(data, container);
		return data;
	}
	


	public static List<NamedDataContainer> getAllNamedContaners(DataContainer<?> container)
	{
		List<NamedDataContainer> containers = new ArrayList<NamedDataContainer>();
		addContainers(containers, container, NamedDataContainer.class);
		return containers;
	}

	public static List<NamedDataContainer> getAllNamedContanersByName(DataContainer<?> container, String name)
	{
		List<NamedDataContainer> containers = new ArrayList<NamedDataContainer>();
		addByName(containers, container, name);
		return containers;
	}

	public static NamedDataContainer getFirstNamedContanerByName(DataContainer<?> container, String name)
	{
		return getAllNamedContanersByName(container, name).get(0);
	}

	private static void addByName(List<NamedDataContainer> list, DataContainer<?> container, String name)
	{
		for(DataContainer<?> child: getContainerChildren(container))
		{
			if (child instanceof NamedDataContainer)
			{
				String n = new String(DataUtils.getBytes(((NamedDataContainer) child).getNameData()));
				if (name.equals(n))
					list.add((NamedDataContainer) child);
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
	
	private static void addData(List<Data> list, DataContainer<?> container)
	{
		for(Data child: container.getDataChildren())
		{
			if (child instanceof DataContainer)
			{
				addData(list, (DataContainer<?>)child);
			}
			if (child instanceof EncodedDataContainer || !(child instanceof DataContainer))
			{
				list.add(child);
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
