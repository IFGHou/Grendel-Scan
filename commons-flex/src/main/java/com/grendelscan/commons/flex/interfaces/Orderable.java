package com.grendelscan.commons.flex.interfaces;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

public interface Orderable
{
	public void addChild(AbstractAmfData element);
	
	public void addChild(int index, AbstractAmfData element);
	
	public AmfDataType[] getChildTypes();
	
	public void removeChild(int index);
}
