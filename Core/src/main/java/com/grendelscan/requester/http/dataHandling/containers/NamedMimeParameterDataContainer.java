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
import org.apache.http.entity.mime.MIME;
import org.apache.http.util.ByteArrayBuffer;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.DataParser;
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.utils.CharsetUtils;
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
public class NamedMimeParameterDataContainer extends NamedQueryParameterDataContainer
{
	private static final long	serialVersionUID	= 1L;
	
	
	public NamedMimeParameterDataContainer(DataContainer<?> parent, byte[] name, byte[] value, int transactionId, DataFormat childFormat)
	{
		super(parent, name, value, transactionId, childFormat);
	}
	


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		try
		{
			out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, MIME.CONTENT_DISPOSITION + ": form-data; name=\""));
			name.writeBytes(out);
			out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\"\r\n\r\n"));
			value.writeBytes(out);
			out.write(CharsetUtils.encode(MIME.DEFAULT_CHARSET, "\r\n"));
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
		sb.append("\nValue:\n");
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
		sb.append("NamedMimeParameterDataContainer -\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append("\n\tChild format: ");
		sb.append(childFormat.formatType);
		sb.append("\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return sb.toString();
	}

}
