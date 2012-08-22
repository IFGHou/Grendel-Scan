/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.DataParser;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.utils.dataFormating.DataEncodingStream;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatType;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * @author david
 *
 */
public class NamedQueryParameterDataContainer extends AbstractDataContainer<NameOrValueReference> 
	implements NamedDataContainer
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Data name;
	private Data value;
	
//	public NamedQueryParameterDataContainer(DataContainer<?> parent, byte[] data, int transactionId)
//	{
//
//		super(parent, null, transactionId);
//		setBytes(data);
//		setReference(new NamedDataContainerDataReference(DataUtils.getBytes(name)));
//	}
	
	public NamedQueryParameterDataContainer(DataContainer<?> parent, byte[] name, byte[] value, int transactionId)
	{
		super(parent, null, transactionId);
		setName(name);
		setValue(value);
		setReference(new NamedDataContainerDataReference(name));
	}
	
	private void updateReference()
	{
		setReference(new NamedDataContainerDataReference(DataUtils.getBytes(name)));
	}
	


//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataContainers.Data#clone(com.grendelscan.requester.http.dataContainers.DataContainer)
//	 */
//	@Override
//	public Data clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new NamedQueryParameterDataContainer(parentClone, getBytes(), transaction.getTransactionId());
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataContainers.NamedDataContainer#getName()
	 */
//	@Override
//	public byte[] getNameBytes()
//	{
//		return name.getBytes();
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataContainers.Data#setValue(byte[])
	 */
//	@Override
//	public void setBytes(byte[] bytes)
//	{
//		if (bytes == null || bytes.length == 0)
//		{
//			throw new IllegalArgumentException("For a parameter to exist, something must be here.");
//		}
//		byte[] n;
//		byte[] v;
//		int i;
//		for (i = 0; i < bytes.length; i++)
//		{
//			if(bytes[i] == '=')
//				break;
//		}
//		if (i == 0)
//		{
//			n = new byte[0];
//		}
//		else 
//		{
//			if (i == bytes.length)
//				i--;
//			n = Arrays.copyOfRange(bytes, 0, i);
//		}
//		if (i < bytes.length)
//		{
//			v = Arrays.copyOfRange(bytes, i + 1, bytes.length);
//		}
//		else
//		{
//			v = new byte[0];
//		}
//		setName(n);
//		setValue(v);
//	}

	@Override public void setName(byte[] name)
	{
		this.name = DataParser.parseRawData(name, this, null, NameOrValueReference.NAME, getTransactionId());
		updateReference();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#setValue(byte[])
	 */
	@Override
	public void setValue(byte[] value)
	{
		this.value = DataParser.parseRawData(value, this, null, NameOrValueReference.VALUE, getTransactionId());
	}

	@Override
	public synchronized Data[] getDataChildren()
	{
		return new Data[]{name, value};
	}

	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Remove child makes no sense on a query parameter");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(NameOrValueReference reference)
	{
		if(reference.isName())
		{
			return name;
		}
		return value;
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
			value = child;
		}

	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getValue()
	 */
//	@Override
//	public byte[] getValueBytes()
//	{
//		return value.getBytes();
//	}

	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getName()
	 */
	@Override
	public String getName()
	{
		return new String(DataUtils.getBytes(name));
	}

	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getValue()
	 */
	@Override
	public String getValue()
	{
		return new String(DataUtils.getBytes(value));
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
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#getValueData()
	 */
	@Override
	public Data getValueData()
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
	public void writeBytes(OutputStream stream)
	{
		try
		{
			DataFormat format = new DataFormat();
			format.formatType = DataFormatType.URL_BASIC_ENCODED;
			DataEncodingStream des = new DataEncodingStream(stream, format);
			name.writeBytes(des);
			stream.write('=');
			value.writeBytes(des);
		}
		catch (IOException e)
		{
			Log.error("Very odd problem encoding data: " + e.toString(), e);
		}
		
	}


}
