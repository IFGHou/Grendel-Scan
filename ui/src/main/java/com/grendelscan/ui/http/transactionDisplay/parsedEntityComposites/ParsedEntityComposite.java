package com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites;

import org.eclipse.swt.widgets.Widget;

public interface ParsedEntityComposite 
{
	
	public byte[] getBytes();
	public void setVisible(boolean visible);
	public Widget getWidget();
	void updateData(byte[] data);
}
