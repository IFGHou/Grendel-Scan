/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.AbstractDataContainer;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class NamedAmfDataContainer extends AbstractData implements DataContainer<NameOrValueReference>, NamedDataContainer
{

	private static final long	serialVersionUID	= 1L;


	/**
	 * @param parent
	 * @param reference
	 * @param transactionId
	 */
	public NamedAmfDataContainer(DataContainer<?> parent, int transactionId, Data name, AbstractAmfData value)
	{
		super(parent, new NamedDataContainerDataReference(DataUtils.getBytes(name)), transactionId);
		this.name = name;
		this.value = value;
	}

	public NamedAmfDataContainer(DataContainer<?> parent, int transactionId, String name, AbstractAmfData value)
	{
		super(parent, new NamedDataContainerDataReference(name.getBytes()), transactionId);
		this.name = new ByteData(this, name.getBytes(), NameOrValueReference.NAME, transactionId);
		this.value = value;
	}

	private Data name;
	private AbstractAmfData value;

	
	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public synchronized Data[] getDataChildren()
	{
		return new Data[]{name, value};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Remove child makes no sense on a named AMF container");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(NameOrValueReference reference)
	{
		if (reference.isName())
			return name;
		return value;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((NameOrValueReference) reference);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(NameOrValueReference reference, Data child)
	{
		if (reference.isName())
		{
			name = child;
		}
		else
		{
			value = (AbstractAmfData) child;
		}
	}


//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.data.Data#setBytes(byte[])
//	 */
//	@Override
//	public void setBytes(@SuppressWarnings("unused") byte[] bytes)
//	{
//		throw new NotImplementedException("Not used in AMF");
//	}

	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getName()
	 */
	@Override
	public String getName()
	{
		throw new NotImplementedException("Not used in AMF");
	}

	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getValue()
	 */
	@Override
	public String getValue()
	{
		throw new NotImplementedException("Not used in AMF");
	}

//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getNameBytes()
//	 */
//	@Override
//	public byte[] getNameBytes()
//	{
//		return name.getBytes();
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#setValue(byte[])
	 */
	@Override
	public void setValue(@SuppressWarnings("unused") byte[] value)
	{
		throw new NotImplementedException("Not used in AMF");
	}

//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getValueBytes()
//	 */
//	@Override
//	public byte[] getValueBytes()
//	{
//		return value.getBytes();
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getValueData()
	 */
	@Override
	public AbstractAmfData getValueData()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getNameData()
	 */
	@Override
	public Data getNameData()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		throw new NotImplementedException("Not used in AMF");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#setName(byte[])
	 */
	@Override
	public void setName(byte[] name)
	{
		throw new NotImplementedException("Not used in AMF");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("NamedAmfDataContainer:\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return null;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name:\n");
		sb.append(StringUtils.indentLines(name.debugString(), 1));
		sb.append("\nValue:\n");
		sb.append(StringUtils.indentLines(value.debugString(), 1));
		return sb.toString();
	}

}
