package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.IOException;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfByteArray;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.AbstractMessage;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

public class AmfAsyncMessage extends AmfAbstractMessage
{
	
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// Serialization constants
	private static byte CORRELATION_ID_FLAG = 1;
	private static byte CORRELATION_ID_BYTES_FLAG = 2;
	private static final String correlationIdName = "correlationId";
	
	
	public AmfAsyncMessage(String name, boolean externalizable, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kAmfAsyncMessage, externalizable, parent, transactionId);
		initCorrelationId("");
	}
	
	protected AmfAsyncMessage(String name, AbstractMessage message, AmfDataType type, String className, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, type, className, parent, transactionId);
		initCorrelationId("");
	}
	
	public AmfAsyncMessage(String name, AsyncMessage message, String className, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, AmfDataType.kAmfAsyncMessage, className, parent, transactionId);
		initCorrelationId(message.getCorrelationId());
		
	}
	
	protected AmfAsyncMessage(String name, AmfDataType type, boolean externalizable, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, type, externalizable, parent, transactionId);
		initCorrelationId("");
	}
	
	private void initCorrelationId(String value)
	{
		addFixedField(correlationIdName, new AmfPrimitiveData(correlationIdName, value.getBytes(), this, getTransactionId(), true), true);
	}
	
	private AmfPrimitiveData getCorrelationId()
	{
		return (AmfPrimitiveData) properties.get(correlationIdName).getValueData();
	}
	
	@Override
	public void writeExternal(AmfOutputStream outputStream) throws IOException
	{
		super.writeExternal(outputStream);
		
		byte[] correlationIdBytes = UUIDUtils.toByteArray(getCorrelationId().getValue());
		
		short flags = 0;
		
		if (getCorrelationId() != null && correlationIdBytes == null)
		{
			flags |= CORRELATION_ID_FLAG;
		}
		
		if (correlationIdBytes != null)
		{
			flags |= CORRELATION_ID_BYTES_FLAG;
		}
		
		outputStream.writeByte(flags);
		
		if (correlationIdBytes == null)
		{
			getCorrelationId().writeBytes(outputStream);
		}
		else
		{
			AmfByteArray aba = new AmfByteArray("Correlation ID", correlationIdBytes, this, getTransactionId());
			aba.writeBytes(outputStream);
		}
	}

//	@Override
//    protected ArrayList<String> getParameterNames()
//    {
//		ArrayList<String> names = super.getParameterNames();
//		names.add(correlationIdName);
//		return names;
//    }
}
