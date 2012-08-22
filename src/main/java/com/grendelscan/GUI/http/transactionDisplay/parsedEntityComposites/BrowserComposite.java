package com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.customControls.SwingBrowserComposite;
import com.grendelscan.utils.StringUtils;

public class BrowserComposite extends SwingBrowserComposite implements ParsedEntityComposite
{
	private byte[] data;
	public BrowserComposite(Composite parent, int style)
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
