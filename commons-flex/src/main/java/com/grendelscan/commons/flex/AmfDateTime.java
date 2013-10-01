package com.grendelscan.commons.flex;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.references.DataReference;



public class AmfDateTime extends AbstractAmfData
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfDateTime.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private long time;
	
	public AmfDateTime(String name, long time, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kDate, parent, false, transactionId);
		this.time = time;
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
//			AmfUtils.writeUInt29(outputStream, 1);
			
			// Write the time as 64bit value in ms
			outputStream.writeDouble(time);
		}
		catch (IOException e)
		{
			LOGGER.error("Problem writing AMF: " + e.toString(), e);
		}
	}
	
}
