/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.jgroups.util.ExposedByteArrayOutputStream;

import com.grendelscan.requester.http.dataHandling.DataParser;
import com.grendelscan.requester.http.dataHandling.data.AbstractData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.SingleChildReference;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.dataFormating.DataEncodingStream;
import com.grendelscan.utils.dataFormating.DataFormat;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * @author david
 *
 */
public class EncodedDataContainer extends AbstractData implements DataContainer<SingleChildReference>
{

	private static final long	serialVersionUID	= 1L;
	private final DataFormat format;
	private Data child;
	/**
	 * @param transactionId
	 * @throws DataFormatException 
	 */
	public EncodedDataContainer(DataContainer<?> parent, byte[] encodedData, DataFormat format, DataReference reference, int transactionId) throws DataFormatException
	{
		super(parent, reference, transactionId);
		this.format = format;
		byte[] decodedData = DataFormatUtils.decodeData(encodedData, format.formatType);
		child = DataParser.parseRawData(decodedData, this, (DataFormat) null, SingleChildReference.getInstance(), getTransactionId());
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
		return child;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(@SuppressWarnings("unused") SingleChildReference reference, Data child)
	{
		this.child = child;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return child;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		DataEncodingStream des = new DataEncodingStream(out, format);
		child.writeBytes(des);
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("EncodedDataContainer\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append("\n\tFormat: ");
		sb.append(format.formatType);
		sb.append("\n\tRaw Data:\n");
		sb.append(StringUtils.indentLines(child.debugString(), 2));

		ExposedByteArrayOutputStream out = new ExposedByteArrayOutputStream();
		writeBytes(out);
		
		sb.append("\n\tEncoded Data:\n");
		sb.append(StringUtils.indentLines(out.toString(), 2));
		return sb.toString();
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[]{child};
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		throw new NotImplementedException("Doesn't make sense for an encoded data container");
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		// Not usually called directly
		return child.debugString();
	}
}
