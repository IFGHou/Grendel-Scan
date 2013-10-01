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
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.dataFormating.DataEncodingStream;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatType;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * @author david
 *
 */
public class NamedQueryParameterDataContainer extends AbstractData implements NameValuePairDataContainer
{
	private static final long	serialVersionUID	= 1L;
	protected Data name;
	protected Data value;
	protected DataFormat childFormat;
	
	
	public NamedQueryParameterDataContainer(DataContainer<?> parent, byte[] name, byte[] value, int transactionId, DataFormat childFormat)
	{
		super(parent, transactionId);
		this.childFormat = childFormat;
		setName(name);
		setValue(value);
	}
	

	@Override public void setName(byte[] name)
	{
		this.name = DataParser.parseRawData(name, this, childFormat, getTransactionId(), false);
//		updateReference();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer#setValue(byte[])
	 */
	@Override
	public void setValue(byte[] value)
	{
		this.value = DataParser.parseRawData(value, this, childFormat, getTransactionId(), true);
	}

	@Override
	public synchronized Data[] getDataChildren()
	{
		// Neither should be null; one might be an empty byte array, but not null
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

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name:\n");
		sb.append(StringUtils.indentLines(name.debugString(), 1));
		sb.append("Value:\n");
		sb.append(StringUtils.indentLines(value.debugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("NamedQueryParameterDataContainer -\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append("\n\tChild format: ");
		sb.append(childFormat.formatType);
		sb.append("\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public NameOrValueReference getChildsReference(Data child)
	{
		if (child == name)
		{
			return NameOrValueReference.NAME;
		}
		else if (child == value)
		{
			return NameOrValueReference.VALUE;
		}
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}


}
