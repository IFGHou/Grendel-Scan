package com.grendelscan.commons.flex.complexTypes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AbstractAmfNamedDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.arrays.AmfAssociativeArrayData;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.interfaces.AmfGenericObject;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.commons.http.dataHandling.references.SingleChildReference;
import com.grendelscan.commons.flex.AmfUtils;


public class AmfServerSideObject extends AbstractAmfNamedDataContainer implements AmfGenericObject, Externalizable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfServerSideObject.class);
	@SuppressWarnings("hiding") private AmfAssociativeArrayData properties;
	
	// The className property is not a AmfPrimitive data. It is the "value" of
	// the object
	private String className;

	public AmfServerSideObject(String className)
	{
		super(className, AmfDataType.kServerSideObject, null, true, -2);
		init(className);
	}
	
	public void initialize(AbstractAmfDataContainer<?> parent, int transactionId)
	{
		setParent(parent);
//		setReference(reference);
		setTransactionId(transactionId);
	}
	
	public AmfServerSideObject(Object o, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(o.getClass().getCanonicalName(), AmfDataType.kServerSideObject, parent, false, transactionId);
		Class<? extends Object> clazz = o.getClass();
		init(clazz.getCanonicalName());
		
		for (Field field: clazz.getFields())
		{
			field.setAccessible(true);
			try
			{
				Object f = field.get(o);
				AbstractAmfData data = AmfUtils.parseAmfData(f, this, -2, true);
				data.setNameLocked(true);
				data.setTypeLocked(true);
				properties.putChild(field.getName(), data);
			}
			catch (IllegalArgumentException e)
			{
				LOGGER.error("Problem with parsing field " + field.getName() + " for object for AMF (" + o.toString() + ": " + e.toString(), e);
			}
			catch (IllegalAccessException e)
			{
				LOGGER.error("Problem with parsing field " + field.getName() + " for object for AMF (" + o.toString() + ": " + e.toString(), e);
			}
		}
		
	}
	
	
	
	private void init(String className)
	{
		this.className = className;
		properties = new AmfAssociativeArrayData("Properties", this, getTransactionId());
		properties.setNameLocked(true);
		properties.setTypeLocked(true);
		properties.setValueLocked(true);
	}
	
	@Override
	public ArrayList<AbstractAmfData> getChildren()
	{
		ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(1);
		children.add(properties);
		return children;
	}
	
	@Override public String getClassName()
	{
		return className;
	}
	
	@Override public void setClassName(String className)
    {
    	this.className = className;
    }


	public Object getProperty(String name)
	{
		return properties.getChild(name);
	}
	
	public List<String> getPropertyNames()
	{
		List<String> names = new ArrayList<String>(properties.getSize());
		for(byte[] name: properties.getChildNames())
		{
			names.add(new String(name));
		}
		return names;
	}
	
	public void setProperty(String name, AbstractAmfData value)
	{
		value.setName(name);
		value.setDeletable(true);
		properties.putChild(name, value);
	}
	
	@Override
	public void writeBytes(OutputStream out)
	{
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			writeCodeToStream(outputStream);
			boolean externalizable = false;
			boolean dynamic = false;
			int count = properties.getSize();
			AmfUtils.writeUInt29(outputStream, 3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
			AmfUtils.writeAMFUTF(outputStream, false, className, useAmf3Code);
			
			// Both of these must be ordered
			for (String propName: getPropertyNames())
			{
				AmfUtils.writeAMFUTF(outputStream, false, propName, useAmf3Code);
			}
			
			for (String propName: getPropertyNames())
			{
				properties.getChild(propName).writeBytes(out);
//				properties.getChild(propName).writeBytesToStream(outputStream);
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Weird problem writing AMF: " + e.toString(), e);
		}
	}

	@Override public boolean isValueLocked()
	{
		return true;
	}

	@Override public void readExternal(ObjectInput in) throws IOException
    {
//		byte[] bytes = new byte[1000];
//		int pos = 0;
//		String s;
//		while (true)
//		{
//			bytes[pos++] = in.readByte();
//			s = new String(bytes);
//			Debug.debug(s);
//		}
//	    Debug.debug("test");
    }

	@Override public void writeExternal(ObjectOutput out) throws IOException
    {
	    LOGGER.debug("test");
    }

	/* (non-Javadoc)
	 * @see flex.messaging.io.simplifiedAMF.interfaces.ArbitraryNamedChildren#getChild(java.lang.String)
	 */
	@Override
	public AbstractAmfData getChild(String name)
	{
		return properties.getChild(name);
	}

	/* (non-Javadoc)
	 * @see flex.messaging.io.simplifiedAMF.interfaces.ArbitraryNamedChildren#putChild(java.lang.String, flex.messaging.io.simplifiedAMF.AbstractAmfData)
	 */
	@Override
	public Data putChild(String name, AbstractAmfData child)
	{
		return properties.putChild(name, child);
	}

	/* (non-Javadoc)
	 * @see flex.messaging.io.simplifiedAMF.interfaces.ArbitraryNamedChildren#removeChild(java.lang.String)
	 */
	@Override
	public AbstractAmfData removeChild(String name)
	{
		return properties.removeChild(name);
	}

	/* (non-Javadoc)
	 * @see flex.messaging.io.simplifiedAMF.interfaces.ArbitraryChildren#getSize()
	 */
	@Override
	public int getSize()
	{
		return properties.getSize();
	}

}
