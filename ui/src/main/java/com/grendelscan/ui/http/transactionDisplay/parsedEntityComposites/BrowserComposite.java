package com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites;

import org.eclipse.swt.widgets.Widget;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.ui.customControls.SwingBrowserComposite;
import com.grendelscan.ui.customControls.basic.GComposite;

public class BrowserComposite extends SwingBrowserComposite implements ParsedEntityComposite
{
	private byte[] data;
	public BrowserComposite(GComposite parent, int style)
	{
		super(parent, style);
	}
	
	@Override
	public byte[] getBytes()
	{
		return data;
	}
	
	@Override
	public Widget getWidget()
	{
		return this;
	}
	
	@Override
	public void updateData(byte[] newData)
	{
		this.data = newData;
		setHtmlText(new String(newData, StringUtils.getDefaultCharset()));
	}
	
}
