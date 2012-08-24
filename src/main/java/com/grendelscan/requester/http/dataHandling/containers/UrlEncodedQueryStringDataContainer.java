/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.utils.ByteArrayUtils;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatType;

/**
 * @author david
 *
 */
public class UrlEncodedQueryStringDataContainer extends AbstractDataContainer<NamedDataContainerDataReference> 
	implements HtmlQueryContainer<NamedDataContainerDataReference>, ExpandableDataContainer<NamedDataContainerDataReference>
{

	private static final long	serialVersionUID	= 1L;

	private static final DataFormat UrlQueryParamFormat = new DataFormat();
	static
	{
		UrlQueryParamFormat.formatType = DataFormatType.URL_BASIC_ENCODED;
	}
	/**
	 * @param parent
	 */
	public UrlEncodedQueryStringDataContainer(DataContainer<?> parent, byte[] rawData, DataReference reference, int transactionId)
	{
		super(parent, reference, transactionId);
		parseBytes(rawData);
	}


	private void parseBytes(byte[] bytes)
	{
		children.clear();
		if (bytes == null || bytes.length == 0)
		{
			return; //Empty is just fine here
		}

		for(byte[] param: ByteArrayUtils.split(bytes, (byte) '&'))
		{
			if (param.length == 0)
				continue; //Ignore and fix extra ampersands
			byte[][] nv = ByteArrayUtils.splitOnFirst(param, (byte) '=');
			children.add(new NamedQueryParameterDataContainer(this, nv[0], nv[1], getTransactionId(), UrlQueryParamFormat));
		}
	}

//	@Override
//	public Data clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new UrlEncodedQueryStringDataContainer(parentClone, getBytes(), getReference().clone(), transaction.getTransactionId());
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(NamedDataContainerDataReference reference)
	{
		for(Data d: children)
		{
			NamedQueryParameterDataContainer param = (NamedQueryParameterDataContainer) d;
			if (Arrays.areEqual(reference.getName(), DataUtils.getBytes(param.getNameData())))
			{
				return param;
			}
		}
		return null;
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
		if (child instanceof NamedQueryParameterDataContainer)
		{
			children.add(child);
		}
		else
		{
			throw new IllegalArgumentException("Children of a URL-encoded query must be UriQueryParameterDataContainer");
		}
	}

	
	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(NamedDataContainerDataReference reference, Data child)
	{
		removeChild(getChild(reference));
		addChild(child);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.HtmlQueryContainer#addParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public void addParameter(String name, String value)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			out.write(name.getBytes());
			out.write('=');
			out.write(value.getBytes());
		}
		catch (IOException e)
		{
			Log.error("Very, very weird problem creating parameter: " + e.toString(), e);
		}
		children.add(new NamedQueryParameterDataContainer(this, name.getBytes(StringUtils.getDefaultCharset()), 
				value.getBytes(StringUtils.getDefaultCharset()), getTransactionId(), UrlQueryParamFormat));
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
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		boolean first = true;
		for(Data child: children)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				try
				{
					out.write('&');
				}
				catch (IOException e)
				{
					Log.error("Very odd problem writing ampersand", e);
				}
			}
			child.writeBytes(out);
		}
	}
	
	@Override public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UrlEncodedQueryStringDataContainer -\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append("\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return sb.toString();
	}
}
