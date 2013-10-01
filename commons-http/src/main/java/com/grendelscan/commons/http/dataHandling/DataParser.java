/**
 * 
 */
package com.grendelscan.commons.http.dataHandling;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.containers.EncodedDataContainer;
import com.grendelscan.commons.http.dataHandling.containers.UrlEncodedQueryStringDataContainer;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.formatting.DataFormat;
import com.grendelscan.commons.formatting.DataFormatException;
import com.grendelscan.commons.formatting.DataFormatUtils;

/**
 * @author david
 *
 */
public class DataParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataParser.class);
	public static Data parseRawData(byte[] data, DataContainer parent, DataFormat format, int transactionId, boolean mutable) 
	{
		if ((data == null || data.length == 0) && format == null)
		{
			return new ByteData(parent, new byte[0], transactionId, mutable);
		}

		if (format == null)
		{
			try
			{
				format = DataFormatUtils.getDataFormat(data, null);
			}
			catch (DataFormatException e1)
			{
				LOGGER.warn("Couldn't figure out data format. Using raw bytes", e1);
				return new ByteData(parent, data, transactionId, mutable);
			}
		}
		

		if (format.formatType.isEncodeable())
		{
			try
			{
				return new EncodedDataContainer(parent, data, format, transactionId, mutable);
			}
			catch (DataFormatException e)
			{
				LOGGER.warn("Problem parsing encoded data of type " + format.formatType.getName() + ": " + e.toString(), e);
				return new ByteData(parent, data, transactionId, mutable);
			}
		}
		
		if(format.formatType.isContainer())
		{
			return parseContainer(data, format, parent, transactionId, mutable);
		}
		
		return new ByteData(parent, data, transactionId, mutable); // The default for all base data types

	}
	
	public static Data parseRawData(byte[] data, DataContainer parent, String mimeType, int transactionId, boolean mutable) 
	{
		DataFormat format;
		if ((data == null || data.length == 0) && (mimeType == null || mimeType.isEmpty()))
		{
			return new ByteData(parent, new byte[0], transactionId, mutable);
		}
		
		try
		{
			format = DataFormatUtils.getDataFormat(data, mimeType);
		}
		catch (DataFormatException e1)
		{
			LOGGER.warn("Couldn't figure out data format. Using raw bytes", e1);
			return new ByteData(parent, data, transactionId, mutable);
		}
		
		return parseRawData(data, parent, format, transactionId, mutable);
	}
	
	private static DataContainer<?> parseContainer(byte[] data, DataFormat format, DataContainer<?> parent, int transactionId, boolean mutable)
	{
		switch(format.formatType)
		{
			case URL_ENCODED_QUERY_STRING:
				return new UrlEncodedQueryStringDataContainer(parent, data, transactionId, mutable);
			default:
				LOGGER.warn("Problem with fall-through");
		}
		
		throw new NotImplementedException("Why isn't the " + format.formatType.getName() + " container implemented here??");
	}
}
