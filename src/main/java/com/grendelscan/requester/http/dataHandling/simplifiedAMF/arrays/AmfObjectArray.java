package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;


import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.ArbitraryUnnamedChildren;

public class AmfObjectArray extends AmfPrimitiveArray implements ArbitraryUnnamedChildren
{
	private static final long	serialVersionUID	= 1L;

	public AmfObjectArray(String name, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, AmfDataType.kObjectArray, parent, reference, transactionId);
	}

	public AmfObjectArray(String name, AmfDataType type, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, type, parent, reference, transactionId);
	}

	@Override
    public AmfDataType[] getChildTypes()
	{
		return AmfDataType.getCreatableTypes();
	}
}
