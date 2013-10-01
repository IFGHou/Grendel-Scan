package com.grendelscan.commons.flex.arrays;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.flex.AmfUtils;


public class AmfByteArray extends AbstractAmfData
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfByteArray.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private ArrayList<Byte> data;
	
	public AmfByteArray(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kByteArray, parent, false, transactionId);
		data = new ArrayList<Byte>(1);
	}
	
	public AmfByteArray(String name, byte[] bytes, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		this(name, parent, transactionId);
		data = new ArrayList<Byte>(bytes.length);
		for (byte b: bytes)
		{
			data.add(b);
		}
	}


	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		return null;
	}

	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			writeCodeToStream(outputStream);
			if (useAmf3Code)
			{
				// Write out an invalid reference, storing the length in the unused
				// 28-bits.
				AmfUtils.writeUInt29(outputStream, (data.size() << 1) | 1);
			}
			else
			{
				outputStream.writeInt(data.size());
			}
			
			for (Byte b: data)
			{
				if (b == null)
				{
					outputStream.write(0);
				}
				else
				{
					outputStream.write(b);
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Problem writing AMF: " + e.toString(), e);
		}
	}
}
