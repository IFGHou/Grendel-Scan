package com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces;

import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

public interface Orderable
{
	public void addChild(AbstractAmfData element);
	
	public void addChild(int index, AbstractAmfData element);
	
	public AmfDataType[] getChildTypes();
	
	public void removeChild(int index);
}
