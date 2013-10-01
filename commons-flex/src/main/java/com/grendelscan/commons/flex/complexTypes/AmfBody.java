package com.grendelscan.commons.flex.complexTypes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.AmfPrimitiveData;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.commons.http.dataHandling.references.amf.AmfBodyComponentReference;
import com.grendelscan.commons.flex.AmfUtils;
import com.grendelscan.commons.StringUtils;

import flex.messaging.io.amf.MessageBody;

public class AmfBody extends AbstractAmfDataContainer<AmfBodyComponentReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfBody.class);
	private static final long	serialVersionUID	= 1L;
	private AmfPrimitiveData targetURI;
	private AmfPrimitiveData responseURI;
	private AbstractAmfData data;
	
	public AmfBody(MessageBody body, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		this(body.getTargetURI(), body.getResponseURI(), parent, transactionId, amfRoot);
		data = AmfUtils.parseAmfData(body.getData(), parent, transactionId, true);
		data.setName("Body data");
		data.setTypeLocked(true);
		data.setNameLocked(true);
	}
	
	public AmfBody(String targetURI, String responseURI, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super("", AmfDataType.kAmfBody, parent, false, transactionId);
		data = new AmfPrimitiveData("Body data", AmfDataType.kNull, new byte[0], this, transactionId, true);
		data.setTypeLocked(true);
		data.setNameLocked(true);
		this.targetURI = new AmfPrimitiveData("Target URI", targetURI.getBytes(), this, transactionId, true);
		this.responseURI = new AmfPrimitiveData("Response URI", responseURI.getBytes(), this, transactionId, true);
		this.targetURI.setTypeLocked(true);
		this.responseURI.setTypeLocked(true);
		this.targetURI.setNameLocked(true);
		this.responseURI.setNameLocked(true);
	}
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(1);
		children.add(targetURI);
		children.add(responseURI);
		children.add(data);
		return children;
	}
	
	@Override public boolean isNameLocked()
	{
		return true;
	}
	
	@Override public boolean isTypeLocked()
	{
		return true;
	}

	@Override public boolean isValueLocked()
	{
		return true;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {targetURI, responseURI, data};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		throw new NotImplementedException("Can't remove children from AmfBody");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(AmfBodyComponentReference reference)
	{
		switch(reference.getLocation())
		{
			case _BODY_DATA:
				return data;
			case _RESPONSE_URI:
				return responseURI;
			case _TARGET_URI:
				return targetURI;
		}
		throw new NotImplementedException("Unknown data location");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((AmfBodyComponentReference) reference);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference, com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(AmfBodyComponentReference reference, Data child)
	{
		switch(reference.getLocation())
		{
			case _RESPONSE_URI:
				if (child instanceof AmfPrimitiveData)
				{
					responseURI = (AmfPrimitiveData) child;
					return;
				}
				throw new IllegalArgumentException("Must be of type AmfPrimitiveData");
			case _TARGET_URI:
				if (child instanceof AmfPrimitiveData)
				{
					targetURI = (AmfPrimitiveData) child;
					return;
				}
				throw new IllegalArgumentException("Must be of type AmfPrimitiveData");
			case _BODY_DATA:
				if (child instanceof AbstractAmfData)
				{
					data = (AbstractAmfData) child;
					return;
				}
				throw new IllegalArgumentException("Must be of type AbstractAmfData");

		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		
		outputStream.setAmf3Active(false); // Reset at the begining of each body
		try
		{
			if (targetURI == null)
				outputStream.writeUTF("null");
			else
				outputStream.writeUTF(targetURI.getValue());
			
			if (responseURI == null)
				outputStream.writeUTF("null");
			else
				outputStream.writeUTF(responseURI.getValue());
			outputStream.writeInt(UNKNOWN_CONTENT_LENGTH);
		}
		catch (IOException e)
		{
			LOGGER.error("Weird problem writing AMF: " + e.toString(), e);
		}
		data.writeBytes(out);
		
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nTarget URI: ");
		sb.append(StringUtils.indentLines(targetURI.debugString(), 1));
		sb.append("\nResponse URI: ");
		sb.append(StringUtils.indentLines(responseURI.debugString(), 1));
		sb.append("\nData: ");
		sb.append(StringUtils.indentLines(data.debugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public AmfBodyComponentReference getChildsReference(Data child)
	{
		if (child == targetURI)
		{
			return AmfBodyComponentReference.TARGET_URI;
		}
		else if (child == responseURI)
		{
			return AmfBodyComponentReference.RESPONSE_URI;
		}
		else if (child == data)
		{
			return AmfBodyComponentReference.BODY_DATA;
		}
		
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}
	
}
