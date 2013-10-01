/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ByteArrayUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.formatting.DataFormat;
import com.grendelscan.commons.formatting.DataFormatType;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;

/**
 * @author david
 * 
 */
public class UrlEncodedQueryStringDataContainer extends AbstractDataContainer<NamedDataContainerDataReference> implements HtmlQueryContainer<NamedDataContainerDataReference>, ExpandableDataContainer<NamedDataContainerDataReference>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlEncodedQueryStringDataContainer.class);
    private static final long serialVersionUID = 1L;
    private static final DataFormat UrlQueryParamFormat = new DataFormat();
    private final boolean mutable;
    static
    {
        UrlQueryParamFormat.formatType = DataFormatType.URL_BASIC_ENCODED;
    }

    /**
     * @param parent
     */
    public UrlEncodedQueryStringDataContainer(final DataContainer<?> parent, final byte[] rawData, final int transactionId, final boolean mutable)
    {
        super(parent, transactionId);
        parseBytes(rawData);
        this.mutable = mutable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(final Data child)
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

    // @Override
    // public Data clone(TransactionContainer transaction)
    // {
    // DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
    // return new UrlEncodedQueryStringDataContainer(parentClone, getBytes(), getReference().clone(), transaction.getTransactionId());
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(@SuppressWarnings("unused") final NamedDataContainerDataReference reference, final Data child)
    {
        addChild(child);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.HtmlQueryContainer#addParameter(java.lang.String, java.lang.String)
     */
    @Override
    public void addParameter(final String name, final String value)
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
            LOGGER.error("Very, very weird problem creating parameter: " + e.toString(), e);
        }
        children.add(new NamedQueryParameterDataContainer(this, name.getBytes(StringUtils.getDefaultCharset()), value.getBytes(StringUtils.getDefaultCharset()), getTransactionId(), UrlQueryParamFormat));
    }

    @Override
    public String debugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("UrlEncodedQueryStringDataContainer -\n");
        sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
        sb.append("\n");
        sb.append(StringUtils.indentLines(childrenDebugString(), 1));
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final NamedDataContainerDataReference reference)
    {
        for (Data d : children)
        {
            NamedQueryParameterDataContainer param = (NamedQueryParameterDataContainer) d;
            if (Arrays.areEqual(reference.getName(), DataUtils.getBytes(param.getNameData())))
            {
                return param;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public NamedDataContainerDataReference getChildsReference(final Data child)
    {
        if (children.contains(child))
        {
            NamedQueryParameterDataContainer param = (NamedQueryParameterDataContainer) child;
            return new NamedDataContainerDataReference(param.getName().getBytes());
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
        return getChild((NamedDataContainerDataReference) reference);
    }

    @Override
    public boolean isMutable()
    {
        return mutable;
    }

    private void parseBytes(final byte[] bytes)
    {
        children.clear();
        if (bytes == null || bytes.length == 0)
        {
            return; // Empty is just fine here
        }

        for (byte[] param : ByteArrayUtils.split(bytes, (byte) '&'))
        {
            if (param.length == 0)
            {
                continue; // Ignore and fix extra ampersands
            }
            byte[][] nv = ByteArrayUtils.splitOnFirst(param, (byte) '=');
            children.add(new NamedQueryParameterDataContainer(this, nv[0], nv[1], getTransactionId(), UrlQueryParamFormat));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final NamedDataContainerDataReference reference, final Data child)
    {
        removeChild(getChild(reference));
        addChild(child);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        boolean first = true;
        for (Data child : children)
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
                    LOGGER.error("Very odd problem writing ampersand", e);
                }
            }
            child.writeBytes(out);
        }
    }
}
