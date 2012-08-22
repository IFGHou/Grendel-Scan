package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.SingleChildReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;


public class AmfOperation extends AbstractAmfData
{
	private static final long	serialVersionUID	= 1L;
	private CommandMessageTypeEnum commandType;
	
	public AmfOperation(String name, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kCommandType, parent, false, reference, transactionId);
		commandType = CommandMessageTypeEnum.UNKNOWN_OPERATION;
	}
	
	public AmfOperation(String name, CommandMessageTypeEnum commandType, AbstractAmfDataContainer<?> parent, 
			DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kCommandType, parent, false, reference, transactionId);
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
			Log.error("Problem writing AMF: " + e.toString(), e);
		}
	}

	
}
