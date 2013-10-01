package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.requester.http.dataHandling.references.amf.AmfActionMessageHeaderComponentReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;

import flex.messaging.io.amf.MessageHeader;

public class AmfActionMessageHeader extends AbstractAmfDataContainer<AmfActionMessageHeaderComponentReference>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private AmfPrimitiveData mustUnderstand;
	private AbstractAmfData data;
	
	public AmfActionMessageHeader(MessageHeader header, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super(header.getName(), AmfDataType.kActionMessageHeader, parent, false, transactionId);
		mustUnderstand = new AmfPrimitiveData("Must understand", header.getMustUnderstand(), this, transactionId, true);
		mustUnderstand.setTypeLocked(true);
		data = AmfUtils.parseAmfData(header.getData(), parent, transactionId, true);
	}
	
	public AmfActionMessageHeader(String name, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super(name, AmfDataType.kActionMessageHeader, parent, false, transactionId);
		mustUnderstand = new AmfPrimitiveData("Must understand", false, this, transactionId, true);
		mustUnderstand.setTypeLocked(true);
	}
	
/* TODO UCdetector: Remove unused code: 
	public AmfActionMessageHeader(String name, boolean mustUnderstand, AbstractAmfData data, AbstractAmfData parent)
	{
		super(name, AmfDataType.kActionMessageHeader, parent);
		this.mustUnderstand = new AmfPrimitiveData("Must understand", mustUnderstand, this);
		this.mustUnderstand.setTypeLocked(true);
		this.data = data;
	}
*/
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(1);
		children.add(data);
		return children;
	}
	
	public AbstractAmfData getData()
	{
		return data;
	}
	
	public void setData(AbstractAmfData data)
	{
		this.data = data;
	}
	

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {mustUnderstand, data};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Can't remove anything from AMF Action Message Header");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(AmfActionMessageHeaderComponentReference reference)
	{
		if (reference.isData())
			return data;
		return mustUnderstand;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((AmfActionMessageHeaderComponentReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(AmfActionMessageHeaderComponentReference reference, Data child)
	{
		if (reference.isData())
		{
			data = (AbstractAmfData) child;
		}
		else
		{
			mustUnderstand = (AmfPrimitiveData) child;
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			AmfUtils.writeAMFUTF(outputStream, true, getName(), useAmf3Code);
			mustUnderstand.writeBytes(out);
			outputStream.writeInt(UNKNOWN_CONTENT_LENGTH);
		}
		catch (IOException e)
		{
			Log.error("Weird problem writing AMF: " + e.toString(), e);
		}
		data.writeBytes(out);
		
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		return data.debugString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public AmfActionMessageHeaderComponentReference getChildsReference(Data child)
	{
		if (child == mustUnderstand)
		{
			return AmfActionMessageHeaderComponentReference.MUST_UNDERSTAND;
		}
		else if (child == data)
		{
			return AmfActionMessageHeaderComponentReference.DATA;
		}
		
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}
}
