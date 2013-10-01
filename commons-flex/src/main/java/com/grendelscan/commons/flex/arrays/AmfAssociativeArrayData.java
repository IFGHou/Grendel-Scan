package com.grendelscan.commons.flex.arrays;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AbstractAmfNamedDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.NamedAmfDataContainer;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.commons.flex.AmfUtils;


public class AmfAssociativeArrayData extends AbstractAmfNamedDataContainer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfAssociativeArrayData.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	

	public AmfAssociativeArrayData(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kAssociativeArray, parent, false, transactionId);
	}
	
	public AmfAssociativeArrayData(String name, Map<?, ?> map, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		this(name, parent, transactionId);
		if (map != null)
		{
			for (Object o: map.keySet())
			{
				AbstractAmfData amfObject = AmfUtils.parseAmfData(map.get(o), parent, transactionId, true);
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
			LOGGER.error("Problem writing AMF: " + e.toString(), e);
		}

	}
	
	@Override
    public boolean isValueLocked()
	{
		return true;
	}

}
