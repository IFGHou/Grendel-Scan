package com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NumberedListDataReference;
import com.grendelscan.requester.http.dataHandling.references.SingleChildReference;
import com.grendelscan.requester.http.dataHandling.references.TransactionDataReference;
import com.grendelscan.requester.http.dataHandling.references.amf.AmfActionMessageRootComponentReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfMessageBodies;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfMessageHeaders;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages.AmfActionMessageHeader;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.StringUtils;

import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.MessageHeader;

public class AmfActionMessageRoot extends AbstractAmfDataContainer<AmfActionMessageRootComponentReference>
{
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
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {headers, bodies};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(@SuppressWarnings("unused") Data child)
	{
		throw new NotImplementedException("Can't remove children from message root");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(AmfActionMessageRootComponentReference reference)
	{
		if (reference.isBodies())
			return bodies;
		return headers;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((AmfActionMessageRootComponentReference) reference);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(AmfActionMessageRootComponentReference reference, Data child)
	{
		throw new NotImplementedException("Can't add children from message root");
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
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
			Log.error("Weird problem writing AMF: " + e.toString(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
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
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.requester.http.dataHandling.data.Data)
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
