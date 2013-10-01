package com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.NamedAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfAssociativeArrayData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.AmfGenericObject;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.output.AmfOutputStreamRegistry;
import com.grendelscan.utils.AmfUtils;

import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.TraitsInfo;

public class AmfASObject extends AmfAssociativeArrayData implements AmfGenericObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// The namedType property is not a AmfPrimitive data. It is the "value" of
	// the object
	private String namedType; // basically the class name
	
	@Override public void setClassName(String className)
    {
    	this.namedType = className;
    }

	public AmfASObject(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, parent, transactionId);
		setType(AmfDataType.kASObject);
	}
	
	public AmfASObject(String name, ASObject object, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, object, parent, transactionId);
		setType(AmfDataType.kASObject);
		namedType = object.getType();
	}
	
	@Override public String getClassName()
	{
		return namedType;
	}
	
	@Override
    public void writeBytes(OutputStream out)
    {
		AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
		try
		{
			writeCodeToStream(outputStream);
		}
		catch (IOException e)
		{
			Log.error("Problem writing AMF: " + e.toString(), e);
		}
		boolean externalizable = false;
		boolean dynamic = false;
//        AmfUtils.writeUInt29(outputStream, 3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
        
        List<String> propertyNames = null;
        if (!externalizable)
        {
            propertyNames = new ArrayList<String>(properties.size());
            for(byte[] name: properties.getSortedKeys())
            {
            	propertyNames.add(new String(name));
            }
        }
        TraitsInfo ti = new TraitsInfo(namedType, dynamic, externalizable, propertyNames);

        try
		{
			writeObjectTraits(outputStream, ti);
		}
		catch (IOException e)
		{
			Log.error("Problem writing AMF: " + e.toString(), e);
		}

		for (NamedAmfDataContainer container: properties.getSortedValues())
		{
			container.getValueData().writeBytes(outputStream);
		}
    }

    protected void writeObjectTraits(DataOutputStream outputStream, TraitsInfo ti) throws IOException
    {
        String className = ti.getClassName();


        int count = 0;
        List propertyNames = null;
        boolean externalizable = ti.isExternalizable();

        if (!externalizable)
        {
            propertyNames = ti.getProperties();
            if (propertyNames != null)
                count = propertyNames.size();
        }

        boolean dynamic = ti.isDynamic();

        AmfUtils.writeUInt29(outputStream, 3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | (count << 4));
        AmfUtils.writeAMFUTF(outputStream, false, className, useAmf3Code);

        if (!externalizable && propertyNames != null)
        {
            for (int i = 0; i < count; i++)
            {
                String propName = ti.getProperty(i);
                AmfUtils.writeAMFUTF(outputStream, false, propName, useAmf3Code);
            }
        }
    }

    
	@Override
    public boolean isValueLocked()
	{
		return true;
	}


}
