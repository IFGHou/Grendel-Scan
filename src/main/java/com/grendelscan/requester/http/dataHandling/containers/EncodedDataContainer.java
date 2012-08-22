/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.OutputStream;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.DataParser;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.SingleChildReference;
import com.grendelscan.utils.dataFormating.DataEncodingStream;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * @author david
 *
 */
public class EncodedDataContainer extends AbstractDataContainer<SingleChildReference>
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final DataFormat format;
	/**
	 * @param transactionId
	 * @throws DataFormatException 
	 */
	public EncodedDataContainer(DataContainer<?> parent, byte[] encodedData, DataFormat format, DataReference reference, int transactionId) throws DataFormatException
	{
		super(parent, reference, transactionId);
		this.format = format;
		byte[] decodedData = DataFormatUtils.decodeData(encodedData, format.formatType);
		children.add(DataParser.parseRawData(decodedData, this, null, SingleChildReference.getInstance(), getTransactionId()));
	}
	
	
//	
//	/* (non-Javadoc)
//	 * @see com.grendelscan.requester.http.dataHandling.data.Data#clone(com.grendelscan.requester.http.dataHandling.containers.DataContainer)
//	 */
//	@Override
//	public Data clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		try
//		{
//			return new EncodedDataContainer(parentClone, getBytes(), format, getReference().clone(), transaction.getTransactionId());
//		}
//		catch (DataFormatException e)
//		{
//			Log.error("Problem cloning: " + e.toString(), e);
//			throw new RuntimeException("Very odd problem cloning", e);
//		}
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(@SuppressWarnings("unused") SingleChildReference reference)
	{
		return children.get(0);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(@SuppressWarnings("unused") SingleChildReference reference, Data child)
	{
		children.set(0, child);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((SingleChildReference) reference);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		DataEncodingStream des = new DataEncodingStream(out, format);
		children.get(0).writeBytes(des);
	}
}
