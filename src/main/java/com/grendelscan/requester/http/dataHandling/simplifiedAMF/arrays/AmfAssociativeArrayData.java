package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfNamedDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.NamedAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;


public class AmfAssociativeArrayData extends AbstractAmfNamedDataContainer
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	

	public AmfAssociativeArrayData(String name, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kAssociativeArray, parent, false, reference, transactionId);
	}
	
	public AmfAssociativeArrayData(String name, Map<?, ?> map, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		this(name, parent, reference, transactionId);
		if (map != null)
		{
			for (Object o: map.keySet())
			{
				AbstractAmfData amfObject = AmfUtils.parseAmfData(map.get(o), parent, NameOrValueReference.VALUE, transactionId);
				amfObject.setName(o.toString());
				NamedAmfDataContainer container = new NamedAmfDataContainer(this, getTransactionId(), o.toString(), amfObject);
				properties.put(o.toString().getBytes(), container);
				amfObject.setDeletable(true);
			}
		}
	}
	
	
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			writeCodeToStream(outputStream);
			AmfUtils.writeUInt29(outputStream, (0 << 1) | 1);
			
			for (byte[] name: properties.getSortedKeys())
			{
//				NamedAmfDataContainer container = properties.get(name);
				Data value = properties.get(name);
//				container.getValueData().writeBytes(out);
				AmfUtils.writeAMFUTF(outputStream, false, new String(DataUtils.getBytes(value)), useAmf3Code);
				value.writeBytes(outputStream);
			}
			AmfUtils.writeAMFUTF(outputStream, false, "", useAmf3Code);
		}
		catch (IOException e)
		{
			Log.error("Problem writing AMF: " + e.toString(), e);
		}

	}
	
	@Override
    public boolean isValueLocked()
	{
		return true;
	}

}
