/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.HttpConstants;
import com.grendelscan.commons.http.PrimaryQueryType;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.dataHandling.DataParser;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.commons.http.dataHandling.references.TransactionDataReference;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

/**
 * @author david
 * 
 */
public class TransactionContainer extends AbstractData implements DataContainer<TransactionDataReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionContainer.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private UrlEncodedQueryStringDataContainer urlQueryDataContainer;
    private Data bodyData;

    /**
     * @param parent
     * @param reference
     * @param transactionId
     */
    public TransactionContainer(final int transactionId)
    {
        super(null, transactionId);
        parseData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
     */
    @Override
    public String childrenDebugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Container -\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("\nURL container:\n");
        if (urlQueryDataContainer == null)
        {
            sb.append("\t<null>");
        }
        else
        {
            sb.append(StringUtils.indentLines(urlQueryDataContainer.debugString(), 1));
        }
        sb.append("\nBody container:\n");
        if (bodyData == null)
        {
            sb.append("\t<null>");
        }
        else
        {
            sb.append(StringUtils.indentLines(bodyData.debugString(), 1));
        }
        return sb.toString();
    }

    private void createHtmlQueryContainer()
    {
        switch (getPrimaryQueryType())
        {
            case MIME_MULTIPART:
                setBodyData(new MimeMultipartDataContainer(this, getTransaction().getRequestWrapper().getBody(), getTransactionId(), true));
                return;

            case URL_ENCODED_BODY:
                // setBodyData(DataParser.parseRawData(getTransaction().getRequestWrapper().getBody(), this,
                // getTransaction().getRequestWrapper().getHeaders().getMimeType(), getTransactionId(), false));
                setBodyData(new UrlEncodedQueryStringDataContainer(this, getTransaction().getRequestWrapper().getBody(), getTransactionId(), true));
                return;

            case URL_QUERY:
                String query = "";
                try
                {
                    query = URIStringUtils.getQuery(getTransaction().getRequestWrapper().getURI());
                }
                catch (URISyntaxException e)
                {
                    LOGGER.error("Problem parsing URI query (" + getTransaction().getRequestWrapper().getURI() + ": " + e.toString(), e);
                }
                setUrlQueryDataContainer(new UrlEncodedQueryStringDataContainer(this, query.getBytes(), getTransactionId(), true));
                return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#debugString()
     */
    @Override
    public String debugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("TransactionContainer:\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("\n");
        sb.append(StringUtils.indentLines(childrenDebugString(), 1));
        return sb.toString();
    }

    public final Data getBodyData()
    {
        return bodyData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final TransactionDataReference reference)
    {
        if (reference.isBody())
        {
            return getBodyData();
        }
        return getUrlQueryDataContainer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public TransactionDataReference getChildsReference(final Data child)
    {
        if (child == bodyData)
        {
            return TransactionDataReference.REQUEST_BODY;
        }
        else if (child == urlQueryDataContainer)
        {
            return TransactionDataReference.URL_QUERY;
        }
        throw new IllegalArgumentException("The passed data object is not a child of this container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return getChild((TransactionDataReference) reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        List<Data> data = new ArrayList<Data>(2);
        if (urlQueryDataContainer != null)
        {
            data.add(urlQueryDataContainer);
        }
        if (bodyData != null)
        {
            data.add(bodyData);
        }
        return data.toArray(new Data[0]);
    }

    public HtmlQueryContainer<?> getHtmlQueryContainer()
    {
        switch (getPrimaryQueryType())
        {
            case URL_ENCODED_BODY:
            case MIME_MULTIPART:
                return (HtmlQueryContainer<?>) getBodyData();

            case URL_QUERY:
                return getUrlQueryDataContainer();

        }
        throw new NotImplementedException("This query type is unknown: " + getPrimaryQueryType().toString());
    }

    public synchronized HtmlQueryContainer<?> getOrCreateHtmlQueryContainer()
    {
        if (getHtmlQueryContainer() == null)
        {
            createHtmlQueryContainer();
        }
        return getHtmlQueryContainer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#getParent()
     */
    @Override
    public DataContainer<?> getParent()
    {
        return null;
    }

    public PrimaryQueryType getPrimaryQueryType()
    {
        if (getTransaction().getRequestWrapper().getMethod().equals(HttpPost.METHOD_NAME) || getTransaction().getRequestWrapper().getMethod().equals(HttpPut.METHOD_NAME))
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

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#getReference()
     */
    @Override
    public DataReference getReference()
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#getReferenceChain()
     */
    @Override
    public DataReferenceChain getReferenceChain()
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

    // /* (non-Javadoc)
    // * @see com.grendelscan.commons.http.dataHandling.data.Data#clone(int)
    // */
    // @Override
    // public Data clone(TransactionContainer transaction)
    // {
    // throw new NotImplementedException("Not implemented at the transaction level");
    // }

    private StandardHttpTransaction getTransaction()
    {
        return Scan.getInstance().getTransactionRecord().getTransaction(getTransactionId());
    }

    public final UrlEncodedQueryStringDataContainer getUrlQueryDataContainer()
    {
        return urlQueryDataContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#isAncestor(com.grendelscan.commons.http.dataHandling.containers.DataContainer)
     */
    @Override
    public boolean isDataAncestor(@SuppressWarnings("unused") final DataContainer<?> container)
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

    private synchronized void parseData()
    {
        StandardHttpTransaction transaction = getTransaction();
        byte[] body = transaction.getRequestWrapper().getBody();
        if (body != null && body.length > 0)
        {
            setBodyData(DataParser.parseRawData(body, this, transaction.getRequestWrapper().getHeaders().getMimeType(), getTransactionId(), true));
        }

        try
        {
            byte[] urlQuery = URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()).getBytes();
            if (urlQuery != null && urlQuery.length > 0)
            {
                setUrlQueryDataContainer(new UrlEncodedQueryStringDataContainer(this, urlQuery, getTransactionId(), true));
            }
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(@SuppressWarnings("unused") final Data child)
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#removeFromCollection()
     */
    @Override
    public void removeFromCollection()
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final TransactionDataReference reference, final Data child)
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

    protected final void setBodyData(final Data bodyData)
    {
        this.bodyData = bodyData;
    }

    protected final void setUrlQueryDataContainer(final UrlEncodedQueryStringDataContainer urlQueryDataContainer)
    {
        this.urlQueryDataContainer = urlQueryDataContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        throw new NotImplementedException("Not implemented at the transaction level");
    }

}
