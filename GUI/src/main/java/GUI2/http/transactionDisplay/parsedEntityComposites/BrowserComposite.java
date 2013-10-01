package com.grendelscan.GUI2.http.transactionDisplay.parsedEntityComposites;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.customControls.SwingBrowserComposite;
import com.grendelscan.utils.StringUtils;

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
