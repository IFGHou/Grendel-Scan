package com.grendelscan.requester.http.dataHandling.simplifiedAMF;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;



public class AmfDateTime extends AbstractAmfData
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private long time;
	
	public AmfDateTime(String name, long time, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kDate, parent, false, reference, transactionId);
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
			Log.error("Problem writing AMF: " + e.toString(), e);
		}
	}
	
}
