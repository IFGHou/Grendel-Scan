/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.entity.mime.MIME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.CharsetUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.formatting.DataEncodingStream;
import com.grendelscan.commons.formatting.DataFormat;
import com.grendelscan.commons.formatting.DataFormatType;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.FileUploadReference;

/**
 * @author dbyrne
 * 
 */
public class FileUploadContainer extends AbstractData implements DataContainer<FileUploadReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadContainer.class);

    private Data fileContent;
    private Data fileName;
    private Data fieldName;
    private Data mimeType;

    /**
     * @param parent
     * @param transactionId
     */
    public FileUploadContainer(final DataContainer<?> parent, final int transactionId)
    {
        super(parent, transactionId);
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
        sb.append("FieldName:\n");
        sb.append(StringUtils.indentLines(fieldName.debugString(), 1));
        sb.append("FileName:\n");
        sb.append(StringUtils.indentLines(fileName.debugString(), 1));
        sb.append("MimeType:\n");
        sb.append(StringUtils.indentLines(mimeType.debugString(), 1));
        sb.append("File content (URL encoded):\n");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataFormat format = new DataFormat();
        format.formatType = DataFormatType.URL_BASIC_ENCODED;
        DataEncodingStream des = new DataEncodingStream(baos, format);
        fileContent.writeBytes(des);

        sb.append(StringUtils.indentLines(new String(baos.toByteArray()), 1));

        return sb.toString();
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
        sb.append("FileUploadContainer -\n");
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
    public Data getChild(final FileUploadReference reference)
    {
        switch (reference.getPart())
        {
            case fieldname:
                return fieldName;
            case filecontent:
                return fileContent;
            case filename:
                return fileName;
            case mime:
                return mimeType;
        }
        throw new IllegalStateException("This should be unreachable");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public FileUploadReference getChildsReference(final Data child)
    {
        if (child == fieldName)
        {
            return FileUploadReference.FIELD_NAME;
        }
        if (child == fileName)
        {
            return FileUploadReference.FILENAME;
        }
        if (child == mimeType)
        {
            return FileUploadReference.MIME_TYPE;
        }
        if (child == fileContent)
        {
            return FileUploadReference.FILE_CONTENT;
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
        return getChild((FileUploadReference) reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        return new Data[] { fieldName, fileName, mimeType, fileContent };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(final Data child)
    {
        throw new NotImplementedException("Remove child makes no sense on a file upload parameter");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(final FileUploadReference reference, final Data child)
    {
        switch (reference.getPart())
        {
            case fieldname:
                fieldName = child;
                break;
            case filecontent:
                fileContent = child;
                break;
            case filename:
                fileName = child;
                break;
            case mime:
                mimeType = child;
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
     */
    @Override
    public void writeBytes(final OutputStream out)
    {
        try
        {
            out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, MIME.CONTENT_DISPOSITION + ": form-data; name=\""));
            fieldName.writeBytes(out);
            out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\"; filename=\""));
            fileName.writeBytes(out);
            out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\"\r\n" + MIME.CONTENT_TYPE + ": "));
            mimeType.writeBytes(out);
            out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\"\r\n\r\n"));
            fileContent.writeBytes(out);
            out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\r\n"));
        }
        catch (IOException e)
        {
            LOGGER.error("Very odd problem writing data: " + e.toString(), e);
        }

    }

}
