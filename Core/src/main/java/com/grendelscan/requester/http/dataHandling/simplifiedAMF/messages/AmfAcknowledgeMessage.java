package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.IOException;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.AcknowledgeMessage;

public class AmfAcknowledgeMessage extends AmfAsyncMessage
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AmfAcknowledgeMessage(String name, AcknowledgeMessage message, String className, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, AmfDataType.kAmfAcknowledgeMessage, className, parent, transactionId);
	}
	
	protected AmfAcknowledgeMessage(String name, AcknowledgeMessage message, AmfDataType type, String className, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, type, className, parent, transactionId);

	}
	
	@Override
    public void writeExternal(AmfOutputStream outputStream) throws IOException
    {
	    super.writeExternal(outputStream);

        short flags = 0;
        outputStream.writeByte(flags);
    }

}
