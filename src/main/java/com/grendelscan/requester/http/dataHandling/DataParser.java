/**
 * 
 */
package com.grendelscan.requester.http.dataHandling;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.containers.EncodedDataContainer;
import com.grendelscan.requester.http.dataHandling.containers.UrlEncodedQueryStringDataContainer;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * @author david
 *
 */
public class DataParser
{
	public static Data parseRawData(byte[] data, DataContainer parent, DataFormat format, DataReference reference, int transactionId) 
	{
		if ((data == null || data.length == 0) && format == null)
		{
			return new ByteData(parent, new byte[0], reference, transactionId);
		}

		if (format == null)
		{
			try
			{
				format = DataFormatUtils.getDataFormat(data, null);
			}
			catch (DataFormatException e1)
			{
				Log.warn("Couldn't figure out data format. Using raw bytes", e1);
				return new ByteData(parent, data, reference, transactionId);
			}
		}
		

		if (format.formatType.isEncodeable())
		{
			try
			{
				return new EncodedDataContainer(parent, data, format, reference, transactionId);
			}
			catch (DataFormatException e)
			{
				Log.warn("Problem parsing encoded data of type " + format.formatType.getName() + ": " + e.toString(), e);
				return new ByteData(parent, data, reference, transactionId);
			}
		}
		
		if(format.formatType.isContainer())
		{
			return parseContainer(data, format, parent, reference, transactionId);
		}
		
		return new ByteData(parent, data, reference, transactionId); // The default for all base data types

	}
	
	public static Data parseRawData(byte[] data, DataContainer parent, String mimeType, DataReference reference, int transactionId) 
	{
		DataFormat format;
		if ((data == null || data.length == 0) && (mimeType == null || mimeType.isEmpty()))
		{
			return new ByteData(parent, new byte[0], reference, transactionId);
		}
		
		try
		{
			format = DataFormatUtils.getDataFormat(data, mimeType);
		}
		catch (DataFormatException e1)
		{
			Log.warn("Couldn't figure out data format. Using raw bytes", e1);
			return new ByteData(parent, data, reference, transactionId);
		}
		
		return parseRawData(data, parent, format, reference, transactionId);
	}
	
	private static DataContainer<?> parseContainer(byte[] data, DataFormat format, DataContainer<?> parent, DataReference reference, int transactionId)
	{
		switch(format.formatType)
		{
			case URL_ENCODED_QUERY_STRING:
				return new UrlEncodedQueryStringDataContainer(parent, data, reference, transactionId);
			default:
				Log.warn("Problem with fall-through");
		}
		
		throw new NotImplementedException("Why isn't the " + format.formatType.getName() + " container implemented here??");
	}
}
