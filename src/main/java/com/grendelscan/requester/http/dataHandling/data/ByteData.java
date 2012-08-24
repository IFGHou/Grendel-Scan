/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.data;

import java.io.IOException;
import java.io.OutputStream;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class ByteData extends AbstractData implements MutableData
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private byte[] bytes;
	
	public ByteData(DataContainer<?> parent, byte[] bytes, DataReference reference, int transactionId)
	{
		super(parent, reference, transactionId);
		if (bytes == null || bytes.length == 0)
		{
			Log.debug("Empty byte data");
		}
		this.bytes = bytes;
	}
	

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#setValue(byte[])
	 */
	@Override
	public void setBytes(byte[] bytes)
	{
		this.bytes = bytes;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream stream)
	{
		try
		{
			stream.write(bytes);
		}
		catch (IOException e)
		{
			Log.error("Very weird problem writing bytes: " + e.toString(), e);
		}
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ByteData:\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		if (bytes == null)
		{
			sb.append("\t<null>");
		}
		else
		{
			sb.append(StringUtils.indentLines(new String(bytes), 1));
		}
		return sb.toString();
	}




//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.data.Data#clone(com.grendelscan.requester.http.transactions.StandardHttpTransaction)
//	 */
//	@Override
//	public ByteData clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new ByteData(parentClone, Arrays.clone(bytes), getReference().clone(), transaction.getTransactionId());
//	}




}
