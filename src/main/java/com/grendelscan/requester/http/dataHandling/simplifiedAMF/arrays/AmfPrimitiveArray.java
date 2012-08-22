package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.ExpandableDataContainer;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NumberedListDataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.Orderable;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;


public abstract class AmfPrimitiveArray extends AbstractAmfDataContainer<NumberedListDataReference> 
	implements Orderable, ExpandableDataContainer<NumberedListDataReference>
{
	private static final long	serialVersionUID	= 1L;
	private final ArrayList<AbstractAmfData> data;

	
	public AmfPrimitiveArray(String name, AmfDataType type, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, type, parent, false, reference, transactionId);
		data = new ArrayList<AbstractAmfData>(1);
	}
	
	@Override
	public Data[] getDataChildren()
	{
    	return data.toArray(new Data[data.size()]);
	}
	
	@Override public void removeChild(int index)
    {
		data.remove(index);
		renumberChildren();
    }
	
	@Override public void addChild(AbstractAmfData value)
	{
		data.add(value);
		value.setDeletable(true);
		renumberChildren();
	}
	
	@Override public void addChild(int index, AbstractAmfData value)
	{
		data.add(index, value);
		value.setDeletable(true);
		renumberChildren();
	}
	
	public AbstractAmfData get(int index)
	{
		return data.get(index);
	}
	
	@Override abstract public AmfDataType[] getChildTypes();
	
	@Override
    public boolean isValueLocked()
	{
		return true;
	}

	protected void writeArrayHeaderToStream(AmfOutputStream outputStream)
	{
		try
		{
			writeCodeToStream(outputStream);
			if (useAmf3Code)
			{
				// Write out an invalid reference, storing the length in the unused
				// 28-bits.
				AmfUtils.writeUInt29(outputStream, (getSize() << 1) | 1);
				
				// Send an empty string to imply no named keys
				AmfUtils.writeAMFUTF(outputStream, false, "", useAmf3Code);
			}
			else
			{
				outputStream.writeInt(getSize());
			}
		}
		catch (IOException e)
		{
			Log.error("Problem writing AMF array header: " + e.toString(), e);
		}
	}
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		writeArrayHeaderToStream(outputStream);
		
		for (Data d: data)
		{
			AbstractAmfData datum = (AbstractAmfData) d;
			datum.writeBytes(outputStream);
		}
	}

	public int getSize()
	{
		return data.size();
	}
	
	public void renumberChildren()
	{
		for (int i = 0; i < data.size(); i++)
		{
			AbstractAmfData datum = data.get(i);
			datum.setName(getName() + "[" + i + "]"); 
		}
	}
	
	public void remove(AbstractAmfData child)
    {
	    data.remove(child);
    }

	@Override
	public void setName(String name)
	{
		super.setName(name);
		renumberChildren();
	}

	@Override
	public void addChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Can only have AMF children");
	}


	@Override
	public void removeChild(Data child)
	{
		data.remove(child);
	}

	@Override
	public Data getChild(NumberedListDataReference reference)
	{
		return data.get(reference.getIndex());
	}

	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((NumberedListDataReference) reference);
	}

	@Override
	public void addChild(NumberedListDataReference reference, Data child)
	{
		data.add(reference.getIndex(), (AbstractAmfData) child);
	}

	@Override
	public void replaceChild(NumberedListDataReference reference, Data child)
	{
		data.set(reference.getIndex(), (AbstractAmfData) child);
	}

	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		return data;
	}



}
