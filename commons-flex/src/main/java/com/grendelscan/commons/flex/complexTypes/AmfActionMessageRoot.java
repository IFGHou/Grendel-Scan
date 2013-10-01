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
import com.grendelscan.commons.flex.arrays.AmfMessageBodies;
import com.grendelscan.commons.flex.arrays.AmfMessageHeaders;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.messages.AmfActionMessageHeader;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NumberedListDataReference;
import com.grendelscan.commons.http.dataHandling.references.SingleChildReference;
import com.grendelscan.commons.http.dataHandling.references.TransactionDataReference;
import com.grendelscan.commons.http.dataHandling.references.amf.AmfActionMessageRootComponentReference;
import com.grendelscan.commons.StringUtils;

import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.MessageHeader;

public class AmfActionMessageRoot extends AbstractAmfDataContainer<AmfActionMessageRootComponentReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfActionMessageRoot.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private AmfMessageBodies bodies;
	private AmfMessageHeaders headers;
	private int version;
	
	public AmfActionMessageRoot(ActionMessage message, int transactionId)
	{
		this(message.getVersion(), transactionId);
		int i = 0;
		for (Object o: message.getBodies())
		{
			MessageBody body = (MessageBody) o;
			addBody(new AmfBody(body, this, transactionId, this));
		}
		i = 0;
		for (Object o: message.getHeaders())
		{
			headers.addChild(new AmfActionMessageHeader((MessageHeader) o, this, transactionId, this));
		}
		
	}
	
	public AmfActionMessageRoot(int version, int transactionId)
	{
		super("Main message", AmfDataType.kAmfMessageRoot, null, version == 3, transactionId);
		this.version = version;
		bodies = new AmfMessageBodies(this, transactionId, this);
		headers = new AmfMessageHeaders(this, transactionId, this);
	}
	
	public void addBody(AmfBody body)
	{
		bodies.addChild(body);
	}
	
	
	public AmfMessageBodies getBodies()
	{
		return bodies;
	}
	
	public AmfBody getBody(int index)
	{
		return (AmfBody) bodies.get(index);
	}
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(1);
		children.add(headers);
		children.add(bodies);
		return children;
	}
	
	public int getVersion()
	{
		return version;
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

	public void setVersion(int version)
	{
		this.version = version;
	}
	
//	@Override
//	public void writeBytesToStream(AmfOutputStream outputStream) throws IOException
//	{
//		outputStream.writeShort(version);
//		
//		int headerCount = headers.getSize();
//		outputStream.writeShort(headerCount);
//		for (AbstractAmfData header: headers.getChildren())
//		{
//			header.writeBytesToStream(outputStream);
//		}
//		
//		// Write out the bodies
//		int bodyCount = bodies.getSize();
//		outputStream.writeShort(bodyCount);
//		for (AbstractAmfData body: bodies.getChildren())
//		{
//			body.writeBytesToStream(outputStream);
//		}
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {headers, bodies};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Can't remove children from message root");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(AmfActionMessageRootComponentReference reference)
	{
		if (reference.isBodies())
			return bodies;
		return headers;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((AmfActionMessageRootComponentReference) reference);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference, com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(AmfActionMessageRootComponentReference reference, Data child)
	{
		throw new NotImplementedException("Can't add children from message root");
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream amfOutputStream = AmfOutputStreamRegistry.getStream(out);
		
		try
		{
			amfOutputStream.writeShort(version);
			int headerCount = headers.getSize();
			amfOutputStream.writeShort(headerCount);
			for (AbstractAmfData header: headers.getChildren())
			{
				header.writeBytes(out);
			}
			
			// Write out the bodies
			int bodyCount = bodies.getSize();
			amfOutputStream.writeShort(bodyCount);
			for (AbstractAmfData body: bodies.getChildren())
			{
				body.writeBytes(amfOutputStream);
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Weird problem writing AMF: " + e.toString(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("AMF action message root:\n");
		sb.append("\tHeaders:");
		sb.append(StringUtils.indentLines(headers.childrenDebugString(), 1));
		sb.append("\n\tBodies:");
		sb.append(StringUtils.indentLines(bodies.childrenDebugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public AmfActionMessageRootComponentReference getChildsReference(Data child)
	{
		if (child == bodies)
		{
			return AmfActionMessageRootComponentReference.BODIES;
		}
		else if (child == headers)
		{
			return AmfActionMessageRootComponentReference.HEADERS;
		}
		
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}
}
