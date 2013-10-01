/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;

/**
 * @author david
 * 
 */
public class MimeMultipartDataContainer extends AbstractDataContainer<NamedDataContainerDataReference> implements HtmlQueryContainer<NamedDataContainerDataReference>, ExpandableDataContainer<NamedDataContainerDataReference>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MimeMultipartDataContainer.class);
    private static final long serialVersionUID = 1L;
    private boolean mutable;

    /**
     * @param parent
     */
    public MimeMultipartDataContainer(DataContainer<?> parent, byte[] rawData, int transactionId, boolean mutable)
    {
        super(parent, transactionId);
        parseBytes(rawData);
        mutable = mutable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.data.Data)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(@SuppressWarnings("unused") NamedDataContainerDataReference reference, Data child)
    {
        addChild(child);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.HtmlQueryContainer#addParameter(java.lang.String, java.lang.String)
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
            LOGGER.error("Very, very weird problem creating parameter: " + e.toString(), e);
        }
        // children.add(new NamedQueryParameterDataContainer(this, name.getBytes(StringUtils.getDefaultCharset()),
        // value.getBytes(StringUtils.getDefaultCharset()), getTransactionId(), UrlQueryParamFormat));
    }

    @Override
    public String debugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MimeMultipartDataContainer -\n");
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
    public Data getChild(NamedDataContainerDataReference reference)
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
    public NamedDataContainerDataReference getChildsReference(Data child)
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
    public Data getChildUnsafeType(DataReference reference)
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

        FileUploadBase base = new FileUpload();
        RequestContext ctx = new RequestContext()
        {

            @Override
            public String getCharacterEncoding()
            {
                return "";
            }

            @Override
            public int getContentLength()
            {
                return bytes.length;
            }

            @Override
            public String getContentType()
            {
                return "multipart/form-data";
            }

            @Override
            public InputStream getInputStream() throws IOException
            {
                return new ByteArrayInputStream(bytes);
            }
        };
        try
        {
            base.parseRequest(ctx);
        }
        catch (FileUploadException e)
        {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(NamedDataContainerDataReference reference, Data child)
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
    public void writeBytes(OutputStream out)
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
