package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

public class AmfDoubleArray extends AmfPrimitiveArray
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AmfDoubleArray(String name, AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super(name, AmfDataType.kDoubleArray, parent, transactionId);
	}
	
	@Override
    public AmfDataType[] getChildTypes()
	{
		return new AmfDataType[] { AmfDataType.kDouble };
	}

}
