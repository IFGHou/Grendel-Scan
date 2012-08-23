/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.OutputStream;
import java.net.URISyntaxException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.PrimaryQueryType;
import com.grendelscan.requester.http.dataHandling.DataParser;
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.requester.http.dataHandling.references.TransactionDataReference;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * @author david
 *
 */
public class TransactionContainer extends AbstractData implements DataContainer<TransactionDataReference>
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private UrlEncodedQueryStringDataContainer urlQueryDataContainer;
	private Data bodyData;
	
	/**
	 * @param parent
	 * @param reference
	 * @param transactionId
	 */
	public TransactionContainer(int transactionId)
	{
		super(null, null, transactionId);
		parseData();
	}

	private StandardHttpTransaction getTransaction()
	{
		return Scan.getInstance().getTransactionRecord().getTransaction(getTransactionId());
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#getParent()
	 */
	@Override
	public DataContainer<?> getParent()
	{
		return null;
	}

	
	private synchronized void parseData()
	{
		StandardHttpTransaction transaction = getTransaction(); 
		byte[] body = transaction.getRequestWrapper().getBody();
		if (body != null && body.length > 0)
		{
			setBodyData( DataParser.parseRawData(body, this, transaction.getRequestWrapper().getHeaders().getMimeType(), new TransactionDataReference(true), getTransactionId()));
		}
		
		try
		{
			byte[] urlQuery = URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()).getBytes();
			if (urlQuery != null && urlQuery.length > 0)
			{
				setUrlQueryDataContainer(new UrlEncodedQueryStringDataContainer(this, urlQuery, new TransactionDataReference(false), getTransactionId()));
			}
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
 
	public HtmlQueryContainer<?> getHtmlQueryContainer()
	{
		switch(getPrimaryQueryType())
		{
			case URL_ENCODED_BODY:
			case MIME_MULTIPART:
				return (HtmlQueryContainer<?>) getBodyData();
				
			case URL_QUERY:
				return getUrlQueryDataContainer();
			
		}
		throw new NotImplementedException("This query type is unknown: " + getPrimaryQueryType().toString());
	}

	public PrimaryQueryType getPrimaryQueryType()
	{
		if (getTransaction().getRequestWrapper().getMethod().equals(HttpPost.METHOD_NAME) ||
				getTransaction().getRequestWrapper().getMethod().equals(HttpPut.METHOD_NAME) )
		{
			String mime = getTransaction().getRequestWrapper().getHeaders().getMimeType().toLowerCase();
			if (mime.equals(HttpConstants.ENCODING_APPLICATION_X_WWW_FORM_URLENCODED))
			{
				return PrimaryQueryType.URL_ENCODED_BODY;
			}
			else if (mime.equals(HttpConstants.ENCODING_MULTIPART_FORM_DATA))
			{
				return PrimaryQueryType.MIME_MULTIPART;
			}
		}
		return PrimaryQueryType.URL_QUERY;
	}

	
	private void createHtmlQueryContainer()
	{
		switch(getPrimaryQueryType())
		{
			case MIME_MULTIPART:
				return;
			
			case URL_ENCODED_BODY:
				setBodyData(DataParser.parseRawData(getTransaction().getRequestWrapper().getBody(), this, 
						getTransaction().getRequestWrapper().getHeaders().getMimeType(), new TransactionDataReference(true), getTransactionId()));
				return;
				
			case URL_QUERY:
				String query = "";
				try
				{
					query = URIStringUtils.getQuery(getTransaction().getRequestWrapper().getURI());
				}
				catch (URISyntaxException e)
				{
					Log.error("Problem parsing URI query (" + getTransaction().getRequestWrapper().getURI() + ": " + e.toString(), e);
				}
				setUrlQueryDataContainer(new UrlEncodedQueryStringDataContainer(this, query.getBytes(), new TransactionDataReference(false), getTransactionId()));
				return;
		}
	}
	
	public synchronized HtmlQueryContainer<?> getOrCreateHtmlQueryContainer()
	{
		if (getHtmlQueryContainer() == null)
		{
			createHtmlQueryContainer();
		}
		return getHtmlQueryContainer();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(TransactionDataReference reference)
	{
		if (reference.isBody())
		{
			return getBodyData();
		}
		return getUrlQueryDataContainer();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#isAncestor(com.grendelscan.requester.http.dataHandling.containers.DataContainer)
	 */
	@Override
	public boolean isDataAncestor(@SuppressWarnings("unused") DataContainer<?> container)
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#removeFromCollection()
	 */
	@Override
	public void removeFromCollection()
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#getReference()
	 */
	@Override
	public DataReference getReference()
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(TransactionDataReference reference, Data child)
	{
		if (reference.isBody())
		{
			setBodyData(child);
		}
		else
		{
			if (child instanceof UrlEncodedQueryStringDataContainer)
			{
				setUrlQueryDataContainer((UrlEncodedQueryStringDataContainer) child);
			}
			else
			{
				throw new IllegalArgumentException("Child must be UrlEncodedQueryStringDataContainer");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#getReferenceChain()
	 */
	@Override
	public DataReferenceChain getReferenceChain()
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}


//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.data.Data#clone(int)
//	 */
//	@Override
//	public Data clone(TransactionContainer transaction)
//	{
//		throw new NotImplementedException("Not implemented at the transaction level");
//	}


	public final UrlEncodedQueryStringDataContainer getUrlQueryDataContainer()
	{
		return urlQueryDataContainer;
	}

	public final Data getBodyData()
	{
		return bodyData;
	}


	protected final void setUrlQueryDataContainer(UrlEncodedQueryStringDataContainer urlQueryDataContainer)
	{
		this.urlQueryDataContainer = urlQueryDataContainer;
	}

	protected final void setBodyData(Data bodyData)
	{
		this.bodyData = bodyData;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((TransactionDataReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		throw new NotImplementedException("Not implemented at the transaction level");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("TransactionContainer:\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {urlQueryDataContainer, bodyData};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("URL container:\n");
		sb.append(StringUtils.indentLines(urlQueryDataContainer.debugString(), 1));
		sb.append("\nBody container:\n");
		sb.append(StringUtils.indentLines(bodyData.debugString(), 1));
		
		return sb.toString();
	}
	

}
