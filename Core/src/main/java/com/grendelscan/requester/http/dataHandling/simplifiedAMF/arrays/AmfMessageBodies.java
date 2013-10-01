package com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

public class AmfMessageBodies extends AmfObjectArray
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AmfMessageBodies(AbstractAmfDataContainer<?> parent, int transactionId, AmfActionMessageRoot amfRoot)
	{
		super("Message bodies", AmfDataType.kAmfMessageBodies, parent, transactionId);
	}
	
	@Override
	public boolean isDeletable()
	{
		return false;
	}
	
	@Override
	public boolean isNameLocked()
	{
		return true;
	}
	
	@Override
	public boolean isTypeLocked()
	{
		return true;
	}

}
