package com.grendelscan.commons.flex.messages;

import java.io.IOException;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.AmfPrimitiveData;
import com.grendelscan.commons.flex.arrays.AmfByteArray;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.AbstractMessage;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

public class AmfAsyncMessage extends AmfAbstractMessage
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    // Serialization constants
    private static byte CORRELATION_ID_FLAG = 1;
    private static byte CORRELATION_ID_BYTES_FLAG = 2;
    private static final String correlationIdName = "correlationId";

    protected AmfAsyncMessage(final String name, final AbstractMessage message, final AmfDataType type, final String className, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, type, className, parent, transactionId);
        initCorrelationId("");
    }

    protected AmfAsyncMessage(final String name, final AmfDataType type, final boolean externalizable, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, type, externalizable, parent, transactionId);
        initCorrelationId("");
    }

    public AmfAsyncMessage(final String name, final AsyncMessage message, final String className, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, AmfDataType.kAmfAsyncMessage, className, parent, transactionId);
        initCorrelationId(message.getCorrelationId());

    }

    public AmfAsyncMessage(final String name, final boolean externalizable, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, AmfDataType.kAmfAsyncMessage, externalizable, parent, transactionId);
        initCorrelationId("");
    }

    private AmfPrimitiveData getCorrelationId()
    {
        return (AmfPrimitiveData) properties.get(correlationIdName).getValueData();
    }

    private void initCorrelationId(final String value)
    {
        addFixedField(correlationIdName, new AmfPrimitiveData(correlationIdName, value.getBytes(), this, getTransactionId(), true), true);
    }

    @Override
    public void writeExternal(final AmfOutputStream outputStream) throws IOException
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

    // @Override
    // protected ArrayList<String> getParameterNames()
    // {
    // ArrayList<String> names = super.getParameterNames();
    // names.add(correlationIdName);
    // return names;
    // }
}
