package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.IOException;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.CommandMessage;

public class AmfCommandMessage extends AmfAsyncMessage
{
//	private static final String[] operationNames =
//	        { "subscribe", "unsubscribe", "poll", "unused3", "client_sync", "client_ping", "unused6",
//	                "cluster_request", "login", "logout", "subscription_invalidate", "multi_subscribe", "disconnect",
//	                "trigger_connect" };
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final static byte OPERATION_FLAG = 1;
	protected final static String OPERATION_PROPERTY_NAME = "operation";

	
	public AmfCommandMessage(String name, boolean externalizable, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super(name, AmfDataType.kAmfCommandMessage, externalizable, parent, transactionId);
		AmfOperation operation = new AmfOperation(OPERATION_PROPERTY_NAME, CommandMessageTypeEnum.UNKNOWN_OPERATION, this, transactionId);
		addFixedField(OPERATION_PROPERTY_NAME, operation, true);
	}
	
	public AmfCommandMessage(String name, CommandMessage message, String className, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, AmfDataType.kAmfCommandMessage, className, parent, transactionId);
		AmfOperation operation = new AmfOperation(OPERATION_PROPERTY_NAME, CommandMessageTypeEnum.getByValue(message.getOperation()), this, transactionId);
		addFixedField(OPERATION_PROPERTY_NAME, operation, true);
	}

	
	private AmfOperation getOperation()
	{
		return (AmfOperation) properties.get(OPERATION_PROPERTY_NAME).getValueData();
	}
	
	public CommandMessageTypeEnum getCommandType()
	{
		return getOperation().getCommandType();
	}
	
	public void setCommandType(CommandMessageTypeEnum commandType)
	{
		getOperation().setCommandType(commandType);
	}
	
	@Override
	public void writeExternal(AmfOutputStream outputStream) throws IOException
	{
		super.writeExternal(outputStream);
		
		short flags = 0;
		
		if (getOperation().getValue() != 0)
		{
			flags |= OPERATION_FLAG;
		}
		
		outputStream.writeByte(flags);
		
		if (getOperation().getValue() != 0)
		{
			getOperation().writeBytes(outputStream);
		}
	}

	
//	@Override
//    protected ArrayList<String> getParameterNames()
//    {
//		ArrayList<String> names = super.getParameterNames();
//		names.add(OPERATION_PROPERTY_NAME);
//		return names;
//    }

}
