package com.grendelscan.commons.flex.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.SingleChildReference;
import com.grendelscan.commons.flex.AmfUtils;


public class AmfOperation extends AbstractAmfData
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfOperation.class);
	private static final long	serialVersionUID	= 1L;
	private CommandMessageTypeEnum commandType;
	
	public AmfOperation(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kCommandType, parent, false, transactionId);
		commandType = CommandMessageTypeEnum.UNKNOWN_OPERATION;
	}
	
	public AmfOperation(String name, CommandMessageTypeEnum commandType, AbstractAmfDataContainer<?> parent, 
			int transactionId)
	{
		super(name, AmfDataType.kCommandType, parent, false, transactionId);
		this.commandType = commandType;
	}
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		return null;
	}
	
	public CommandMessageTypeEnum getCommandType()
	{
		return commandType;
	}
	
	public String getDescription()
	{
		return commandType.getDescription();
	}
	
	public int getValue()
	{
		return commandType.getValue();
	}
	
	@Override
	public boolean isNameLocked()
	{
		return true;
	}
	
	@Override
	public boolean isTypeLocked()
	{
		return true;
	}
	
	public void setCommandType(CommandMessageTypeEnum commandType)
	{
		this.commandType = commandType;
	}
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			AmfUtils.writeAMFInt(outputStream, commandType.getValue(), useAmf3Code);
		}
		catch (IOException e)
		{
			LOGGER.error("Problem writing AMF: " + e.toString(), e);
		}
	}

	
}
