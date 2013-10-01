/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.dataHandling.data.AbstractData;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.FilenameComponentDataReference;
import com.grendelscan.commons.StringUtils;

/**
 * @author david
 *
 */
public class UrlFileNameDataContainer extends AbstractData implements DataContainer<FilenameComponentDataReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlFileNameDataContainer.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Data name;
	private Data extension;

	/**
	 * @param parent
	 * @param reference
	 * @param transactionId
	 */
	public UrlFileNameDataContainer(DataContainer<?> parent, int transactionId, String filename)
	{
		super(parent, transactionId);
		setData(filename);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChild(FilenameComponentDataReference reference)
	{
		if (reference.isName())
			return name;
		return extension;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference, com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void replaceChild(FilenameComponentDataReference reference, Data child)
	{
		if (reference.isName())
		{
			name = child;
		}
		else
		{
			extension = child;
		}
	}


	private void setData(String filename)
	{
		int dot = filename.lastIndexOf('.');
		if (dot < 0)
		{
			name = new ByteData(this, filename.getBytes(), getTransactionId(), true);
//			extension = new ByteData(this, new byte[0], new FilenameComponentDataReference(true), getTransactionId());
			extension = null;
		}
		else
		{
			name = new ByteData(this, filename.substring(0, dot).getBytes(), getTransactionId(), true);
			extension = new ByteData(this, filename.substring(dot + 1, dot).getBytes(), getTransactionId(), true);
		}

	}

//	/* (non-Javadoc)
//	 * @see com.grendelscan.commons.http.dataHandling.data.Data#clone(com.grendelscan.commons.http.transactions.StandardHttpTransaction)
//	 */
//	@Override
//	public UrlFileNameDataContainer clone(TransactionContainer transaction)
//	{
//		DataContainer<?> parentClone = (DataContainer<?>) DataContainerUtils.resolveReferenceChain(transaction, getParent().getReferenceChain());
//		return new UrlFileNameDataContainer(parentClone, getReference().clone(), transaction.getTransactionId(), new String(getBytes()));
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
	 */
	@Override
	public Data getChildUnsafeType(DataReference reference)
	{
		return getChild((FilenameComponentDataReference) reference);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#writeBytes(java.io.OutputStream)
	 */
	@Override
	public void writeBytes(OutputStream out)
	{
		try
		{
			name.writeBytes(out);
			
			if (extension != null)
			{
				out.write('.');
				extension.writeBytes(out);
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Problem writing filename: " + e.toString(), e);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
	 */
	@Override
	public Data[] getDataChildren()
	{
		if (extension != null)
		{
			return new Data[] {name, extension};
		}
		return new Data[] {name};
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public void removeChild(Data child)
	{
		throw new NotImplementedException("Remove child makes no sense for a file name container");
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.data.Data#debugString()
	 */
	@Override
	public String debugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UrlFileNameDataContainer\n");
		sb.append(StringUtils.indentLines(abstractDataDebugString(), 1));
		sb.append("\n");
		sb.append(StringUtils.indentLines(childrenDebugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
	 */
	@Override
	public String childrenDebugString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name:\n");
		sb.append(StringUtils.indentLines(name.debugString(), 1));
		sb.append("\nExtension:\n");
		sb.append(StringUtils.indentLines(extension.debugString(), 1));
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildsReference(com.grendelscan.commons.http.dataHandling.data.Data)
	 */
	@Override
	public FilenameComponentDataReference getChildsReference(Data child)
	{
		if (child == name)
		{
			return FilenameComponentDataReference.NAME_COMPONENT;
		}
		else if (child == extension)
		{
			return FilenameComponentDataReference.EXTENSION_COMPONENT;
		}
		throw new IllegalArgumentException("Passed data object is not a child of this collection");
	}

}
