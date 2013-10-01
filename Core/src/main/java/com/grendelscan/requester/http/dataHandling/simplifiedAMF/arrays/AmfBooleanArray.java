package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

public class AmfBooleanArray extends AmfPrimitiveArray
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AmfBooleanArray(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kBooleanArray, parent, transactionId);
	}
	
	@Override
    public AmfDataType[] getChildTypes()
	{
		return new AmfDataType[] { AmfDataType.kBoolean };
	}
}
