package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

public class AmfIntArray extends AmfPrimitiveArray
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AmfIntArray(String name, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kIntArray, parent, reference, transactionId);
	}
	
	@Override
    public AmfDataType[] getChildTypes()
	{
		return new AmfDataType[] { AmfDataType.kInteger };
	}
}
