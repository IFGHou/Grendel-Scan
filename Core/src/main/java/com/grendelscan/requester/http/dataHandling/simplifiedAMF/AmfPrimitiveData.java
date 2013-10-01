package com.grendelscan.requester.http.dataHandling.simplifiedAMF;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.SingleChildReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;


public class AmfPrimitiveData extends AbstractAmfData implements DataContainer<SingleChildReference>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Data data;
	
	public AmfPrimitiveData(String name, AmfDataType type, byte[] data, AbstractAmfDataContainer<?> parent, 
			int transactionId, boolean mutable)
	{
		super(name, type, parent, false, transactionId);
		this.data = new ByteData(parent, data, transactionId, mutable);
	}
	
	public AmfPrimitiveData(String name, boolean data, AbstractAmfDataContainer<?> parent, int transactionId, boolean mutable)
	{
		super(name, data ? AmfDataType.kTrue : AmfDataType.kFalse, parent, false, transactionId);
		this.data = new ByteData(parent, Boolean.toString(data).getBytes(), transactionId, mutable);
	}
	
/* TODO UCdetector: Remove unused code: 
	public AmfPrimitiveData(String name, byte[] data, AbstractAmfData parent)
	{
		super(name, AmfDataType.kByteArray, parent);
		this.data = new String(data, StringUtils.getDefaultCharset());
	}
*/
	
	public AmfPrimitiveData(String name, double data, AbstractAmfDataContainer<?> parent, int transactionId, boolean mutable)
	{
		super(name, AmfDataType.kDouble, parent, false, transactionId);
		this.data = new ByteData(parent, Double.toString(data).getBytes(), transactionId, mutable);
	}
	
	public AmfPrimitiveData(String name, int data, AbstractAmfDataContainer<?> parent, int transactionId, boolean mutable)
	{
		super(name, AmfDataType.kInteger, parent, false, transactionId);
		this.data = new ByteData(parent, Integer.toString(data).getBytes(), transactionId, mutable);
	}
	
	public AmfPrimitiveData(String name, byte[] data, AbstractAmfDataContainer<?> parent, int transactionId, boolean mutable)
	{
		super(name, AmfDataType.kString, parent, false, transactionId);
		this.data = new ByteData(parent, data, transactionId, mutable);
	}
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		return null;
	}
	
	// int kUndefinedType = 0;
	// int kNullType = 1;
	// int kFalseType = 2;
	// int kTrueType = 3;
	// int kXMLType = 7;
	// int kDateType = 8;
	// int kArrayType = 9;
	// int kObjectType = 10;
	// int kAvmPlusXmlType = 11;
	// int kByteArrayType = 12;
	
	public String getValue()
	{
		return toString();
	}
	
	@Override
	public boolean isValueLocked()
	{
		return getType().equals(AmfDataType.kNull) ? true : super.isValueLocked();
	}
	
	public void setValue(String data)
	{
		this.data = new ByteData(this, data.getBytes(), this.data.getTransactionId(), true);
	}
	
	@Override
	public String toString()
	{
		return data.toString();
	}
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
				
		if (data == null)
		{
			setType(AmfDataType.kNull);
		}
		try
		{
			// null, true and false are only data types, no real data
			switch (getType())
			{
				case kDouble:
					writeCodeToStream(outputStream);
					outputStream.writeDouble(Double.valueOf(toString()));
					break;
				case kInteger:
					AmfUtils.writeAMFInt(outputStream, Integer.valueOf(toString()), useAmf3Code);
					break;
				case kString:
				case kXML:
				case kAvmPlusXml:
	//				if (data.equals(""))
	//				{
	//					AmfDataType.kNull.writeCode(outputStream, useAmf3Code);
	//				}
	//				else
	//				{
						writeCodeToStream(outputStream);
						AmfUtils.writeAMFUTF(outputStream, false, data.toString(), useAmf3Code);
	//				}
					break;
				case kTrue:
				case kFalse:
				case kBoolean:
					AmfUtils.writeBoolean(outputStream, Boolean.valueOf(toString()), useAmf3Code);
					break;
				// for null
				default:
					writeCodeToStream(outputStream);
			}
		}
		catch (IOException e) 
		{
			Log.error("Problem writing AMF: " + e.toString(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		return new Data[] {data};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		throw new IllegalArgumentException("Remove child doesn't make sense for a primitive data type");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(SingleChildReference reference)
	{
		return data;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.requester.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return data;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.requester.http.dataHandling.references.DataReference, com.grendelscan.requester.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(SingleChildReference reference, Data child)
	{
		data = child;
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
	public SingleChildReference getChildsReference(Data child)
	{
		if (child == data)
		{
			return SingleChildReference.getInstance();
		}
		throw new IllegalArgumentException("The passed data object is not a child of this container");
	}
}
