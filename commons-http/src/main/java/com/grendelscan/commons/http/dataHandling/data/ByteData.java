/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.data;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.StringUtils;

/**
 * @author david
 *
 */
public class ByteData extends AbstractData implements MutableData
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteData.class);
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private byte[] bytes;
	private boolean mutable;
	
	public ByteData(DataContainer<?> parent, byte[] bytes, int transactionId, boolean mutable)
	{
		super(parent, transactionId);
		if (bytes == null || bytes.length == 0)
		{
			LOGGER.debug("Empty byte data");
		}
		this.mutable = mutable;
		this.bytes = bytes;
	}
	

	public void setBytes(byte[] bytes)
	{
		if (!mutable)
		{
			throw new ImmutableDataException();
		}
		this.bytes = bytes;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
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
			LOGGER.error("Very weird problem writing bytes: " + e.toString(), e);
		}
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#debugString()
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


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.ModifiableData#isMutable()
	 */
	@Override
	public boolean isMutable()
	{
		return mutable;
	}




//	/* (non-Javadoc)
//	 * @see com.grendelscan.commons.http.dataHandling.data.Data#clone(com.grendelscan.commons.http.transactions.StandardHttpTransaction)
//	 */
//	@Override
//	public ByteData clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new ByteData(parentClone, Arrays.clone(bytes), getReference().clone(), transaction.getTransactionId());
//	}




}
