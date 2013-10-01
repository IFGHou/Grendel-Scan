package com.grendelscan.commons.flex.messages;

import java.io.IOException;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.AcknowledgeMessage;

public class AmfAcknowledgeMessage extends AmfAsyncMessage
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected AmfAcknowledgeMessage(final String name, final AcknowledgeMessage message, final AmfDataType type, final String className, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, type, className, parent, transactionId);

    }

    public AmfAcknowledgeMessage(final String name, final AcknowledgeMessage message, final String className, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, AmfDataType.kAmfAcknowledgeMessage, className, parent, transactionId);
    }

    @Override
    public void writeExternal(final AmfOutputStream outputStream) throws IOException
    {
        super.writeExternal(outputStream);

        short flags = 0;
        outputStream.writeByte(flags);
    }

}
