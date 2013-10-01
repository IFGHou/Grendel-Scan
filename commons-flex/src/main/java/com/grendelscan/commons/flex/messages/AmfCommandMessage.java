package com.grendelscan.commons.flex.messages;

import java.io.IOException;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.complexTypes.AmfActionMessageRoot;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.CommandMessage;

public class AmfCommandMessage extends AmfAsyncMessage
{
    // private static final String[] operationNames =
    // { "subscribe", "unsubscribe", "poll", "unused3", "client_sync", "client_ping", "unused6",
    // "cluster_request", "login", "logout", "subscription_invalidate", "multi_subscribe", "disconnect",
    // "trigger_connect" };

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final static byte OPERATION_FLAG = 1;
    protected final static String OPERATION_PROPERTY_NAME = "operation";

    public AmfCommandMessage(final String name, final boolean externalizable, final AbstractAmfDataContainer<?> parent, final int transactionId, final AmfActionMessageRoot amfRoot)
    {
        super(name, AmfDataType.kAmfCommandMessage, externalizable, parent, transactionId);
        AmfOperation operation = new AmfOperation(OPERATION_PROPERTY_NAME, CommandMessageTypeEnum.UNKNOWN_OPERATION, this, transactionId);
        addFixedField(OPERATION_PROPERTY_NAME, operation, true);
    }

    public AmfCommandMessage(final String name, final CommandMessage message, final String className, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, AmfDataType.kAmfCommandMessage, className, parent, transactionId);
        AmfOperation operation = new AmfOperation(OPERATION_PROPERTY_NAME, CommandMessageTypeEnum.getByValue(message.getOperation()), this, transactionId);
        addFixedField(OPERATION_PROPERTY_NAME, operation, true);
    }

    public CommandMessageTypeEnum getCommandType()
    {
        return getOperation().getCommandType();
    }

    private AmfOperation getOperation()
    {
        return (AmfOperation) properties.get(OPERATION_PROPERTY_NAME).getValueData();
    }

    public void setCommandType(final CommandMessageTypeEnum commandType)
    {
        getOperation().setCommandType(commandType);
    }

    @Override
    public void writeExternal(final AmfOutputStream outputStream) throws IOException
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

    // @Override
    // protected ArrayList<String> getParameterNames()
    // {
    // ArrayList<String> names = super.getParameterNames();
    // names.add(OPERATION_PROPERTY_NAME);
    // return names;
    // }

}
