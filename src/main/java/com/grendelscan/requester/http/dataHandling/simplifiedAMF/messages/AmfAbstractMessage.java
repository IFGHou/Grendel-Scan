package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.NamedAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfAssociativeArrayData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfByteArray;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.AmfGenericObject;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;

import flex.messaging.messages.AbstractMessage;
import flex.messaging.util.UUIDUtils;

public abstract class AmfAbstractMessage extends AbstractAmfNamedDataContainer implements AmfGenericObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// Serialization constants
	private static final short HAS_NEXT_FLAG = 128;
	private static final short BODY_FLAG = 1;
	private static final short CLIENT_ID_FLAG = 2;
	private static final short DESTINATION_FLAG = 4;
	private static final short HEADERS_FLAG = 8;
	private static final short MESSAGE_ID_FLAG = 16;
	private static final short TIMESTAMP_FLAG = 32;
	private static final short TIME_TO_LIVE_FLAG = 64;
	private static final short CLIENT_ID_BYTES_FLAG = 1;
	private static final short MESSAGE_ID_BYTES_FLAG = 2;
	
	
	private static final String BODY = "body";
	private static final String HEADERS = "headers";
	private static final String CLIENT_ID = "ClientId";
	private static final String DESTINATION = "destination";
	private static final String MESSAGE_ID = "messageId";
	private static final String TIMESTAMP = "timestamp";
	private static final String TIME_TO_LIVE = "timeToLive";
	
	
	// The className property is not a AmfPrimitive data. It is the "value" of
	// the object
	protected String className;
	
	
	
	/**
	 * Externalizable basically means that property names will not be printed because
	 * the reciever knows how the object will be formated.
	 */
	private boolean externalizable;
	
	
	protected AmfAbstractMessage(String name, AbstractMessage message, AmfDataType type, String className, 
			AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, type, parent, false, reference, transactionId);
		this.className = className;
		
		createHeaders(message.getHeaders());
		
		addFixedField(BODY, AmfUtils.parseAmfData(message.getBody(), this, NameOrValueReference.VALUE, transactionId), false);
		addFixedField(CLIENT_ID, AmfUtils.parseAmfData(message.getClientId(), this, NameOrValueReference.VALUE, transactionId), false);
		addFixedField(DESTINATION, AmfUtils.parseAmfData(message.getDestination(), this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(MESSAGE_ID, AmfUtils.parseAmfData(message.getMessageId(), this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(TIMESTAMP, AmfUtils.parseAmfData(message.getTimestamp(), this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(TIME_TO_LIVE, AmfUtils.parseAmfData(message.getTimeToLive(), this, NameOrValueReference.VALUE, transactionId), true);
		
		externalizable = message instanceof Externalizable;
	}
	
	protected AmfAbstractMessage(String name, AmfDataType type, boolean externalizable, AbstractAmfDataContainer<?> parent, 
			DataReference reference, int transactionId)
	{
		super(name, type, parent, false, reference, transactionId);
		createHeaders(null);
		addFixedField(BODY, AmfUtils.parseAmfData(null, this, NameOrValueReference.VALUE, transactionId), false);
		addFixedField(CLIENT_ID, AmfUtils.parseAmfData("", this, NameOrValueReference.VALUE, transactionId), false);
		addFixedField(DESTINATION, AmfUtils.parseAmfData("", this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(MESSAGE_ID, AmfUtils.parseAmfData("", this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(TIMESTAMP, AmfUtils.parseAmfData(0, this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(TIME_TO_LIVE, AmfUtils.parseAmfData(0, this, NameOrValueReference.VALUE, transactionId), true);

		this.externalizable = externalizable;
	}
	
	protected String getBodyDisplayName()
	{
		return "body";
	}
	
	private void createHeaders(Map<?, ?> map)
	{
		addFixedField(HEADERS, new AmfAssociativeArrayData(HEADERS, map, this, NameOrValueReference.VALUE, getTransactionId()), true);
	}
	
	public AbstractAmfData getBody()
	{
		return properties.get(getBodyDisplayName()).getValueData();
	}
	
	
	@Override public String getClassName()
	{
		return className;
	}
	
	@Override public void setClassName(String className)
    {
    	this.className = className;
    }

	
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			writeCodeToStream(outputStream);
	        writeAttributes(outputStream);
	        
	        if (externalizable)
	        {
	        	writeExternal(outputStream);
	        }
	        else
	        {
	        	for (Data datum: properties.getSortedValues())
	        	{
	        		datum.writeBytes(out);
	        	}
	        }
		}
		catch (IOException e)
		{
			Log.error("Problem writing AMF: " + e.toString(), e);
		}
		
	}

	private void writeAttributes(DataOutputStream outputStream) throws IOException
	{
        boolean dynamic = false;
        int count = 0;
        if (! externalizable)
        {
        	count = properties.size();
        }
		AmfUtils.writeUInt29(outputStream, 3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
		AmfUtils.writeAMFUTF(outputStream, false, className, useAmf3Code);

		if (! externalizable)
		{
        	for (NamedAmfDataContainer container: properties.getSortedValues())
        	{
				AmfUtils.writeAMFUTF(outputStream, false, new String(DataUtils.getBytes(container.getNameData())), useAmf3Code);
			}
		}
	}
	
	protected void writeExternal(AmfOutputStream outputStream) throws IOException
	{
		short flags = 0;
		
		byte[] clientIdBytes = null;
		byte[] messageIdBytes = null;

//		clientIdBytes = UUIDUtils.toByteArray(((AmfPrimitiveData) properties.get(CLIENT_ID)).getValue());
//		messageIdBytes = UUIDUtils.toByteArray(((AmfPrimitiveData) properties.get(MESSAGE_ID)).getValue());
		clientIdBytes = UUIDUtils.toByteArray(new String(DataUtils.getBytes(properties.get(CLIENT_ID))));
		messageIdBytes = UUIDUtils.toByteArray(new String(DataUtils.getBytes(properties.get(MESSAGE_ID))));
		
		if (getBody() != null)
		{
			flags |= BODY_FLAG;
		}
		
		if (properties.get(CLIENT_ID) != null && clientIdBytes == null)
		{
			flags |= CLIENT_ID_FLAG;
		}
		
		if (properties.get(DESTINATION) != null)
		{
			flags |= DESTINATION_FLAG;
		}
		
		if (((AmfAssociativeArrayData)properties.get(HEADERS).getValueData()).getSize() > 0)
		{
			flags |= HEADERS_FLAG;
		}
		
		if ((properties.get(MESSAGE_ID) != null) && (messageIdBytes == null))
		{
			flags |= MESSAGE_ID_FLAG;
		}
		
		if (!((AmfPrimitiveData) properties.get(TIMESTAMP).getValueData()).getValue().equals("0"))
		{
			flags |= TIMESTAMP_FLAG;
		}
		
		if (!((AmfPrimitiveData) properties.get(TIME_TO_LIVE).getValueData()).getValue().equals("0"))
		{
			flags |= TIME_TO_LIVE_FLAG;
		}
		
		if ((clientIdBytes != null) || (messageIdBytes != null))
		{
			flags |= HAS_NEXT_FLAG;
		}
		
		outputStream.writeByte(flags);
		
		flags = 0;
		
		if (clientIdBytes != null)
		{
			flags |= CLIENT_ID_BYTES_FLAG;
		}
		
		if (messageIdBytes != null)
		{
			flags |= MESSAGE_ID_BYTES_FLAG;
		}
		
		if (flags != 0)
		{
			outputStream.writeByte(flags);
		}
		
		if (getBody() != null)
		{
			getBody().writeBytes(outputStream);
		}
		
		if ((properties.get(CLIENT_ID) != null) && (clientIdBytes == null))
		{
			properties.get(CLIENT_ID).writeBytes(outputStream);
		}
		
		if (properties.get(DESTINATION) != null)
		{
			properties.get(DESTINATION).writeBytes(outputStream);
		}
		
		if ((properties.get(HEADERS)) != null)
		{
			properties.get(HEADERS).writeBytes(outputStream);
		}
		
		if ((properties.get(MESSAGE_ID) != null) && (messageIdBytes == null))
		{
			properties.get(MESSAGE_ID).writeBytes(outputStream);
		}
		
		if (!((AmfPrimitiveData) properties.get(TIMESTAMP).getValueData()).getValue().equals("0"))
		{
			properties.get(TIMESTAMP).writeBytes(outputStream);
		}
		
		if (!((AmfPrimitiveData) properties.get(TIME_TO_LIVE).getValueData()).getValue().equals("0"))
		{
			properties.get(TIME_TO_LIVE).writeBytes(outputStream);
		}
		
		if (clientIdBytes != null)
		{
			AmfByteArray aba = new AmfByteArray("Client ID Bytes", clientIdBytes, this, null, -1);
			aba.writeBytes(outputStream);
		}
		
		if (messageIdBytes != null)
		{
			AmfByteArray aba = new AmfByteArray("Message ID Bytes", messageIdBytes, this, null, -1);
			aba.writeBytes(outputStream);
		}

	}
	

	protected void addFixedField(String name, AbstractAmfData value, boolean lockType)
	{
		value.setDeletable(false);
		value.setNameLocked(true);
		value.setTypeLocked(lockType);
		value.setName(name);
		putChild(name, value);
	}

}
