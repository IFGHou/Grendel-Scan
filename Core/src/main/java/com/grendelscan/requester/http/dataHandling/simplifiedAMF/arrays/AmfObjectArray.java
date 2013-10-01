package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;


import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.ArbitraryUnnamedChildren;
import com.grendelscan.utils.StringUtils;

public class AmfObjectArray extends AmfPrimitiveArray implements ArbitraryUnnamedChildren
{
	private static final long	serialVersionUID	= 1L;

	public AmfObjectArray(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kObjectArray, parent, transactionId);
	}

	public AmfObjectArray(String name, AmfDataType type, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, type, parent, transactionId);
	}

	@Override
    public AmfDataType[] getChildTypes()
	{
		return AmfDataType.getCreatableTypes();
	}

}
