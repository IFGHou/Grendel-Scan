package com.grendelscan.GUI2.http.transactionDisplay.parsedEntityComposites;

import org.apache.http.NameValuePair;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import com.grendelscan.GUI.customControls.basic.GComposite;
import com.grendelscan.GUI.customControls.basic.GTableItem;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.GUI.http.transactionDisplay.NamePairComposite;
import com.grendelscan.logging.Log;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatType;
import com.grendelscan.utils.dataFormating.DataFormatUtils;
import com.grendelscan.utils.dataFormating.encoding.UrlEncodingUtils;

public class UrlEncodedViewComposite extends NamePairComposite implements ParsedEntityComposite
{


	public UrlEncodedViewComposite(GComposite parent, int style, int initialHeight, boolean editable)
    {
	    super(parent, style, initialHeight, editable);
    }
	
	@Override
	public void updateData(byte[] data)
	{
		String urlEncoded = new String(data, StringUtils.getDefaultCharset());
		pairsTable.removeAll();
		for (NameValuePair pair: URIStringUtils.getQueryParametersFromQuery(urlEncoded))
		{
			addPair(UrlEncodingUtils.decodeUrl(pair.getName()), UrlEncodingUtils.decodeUrl(pair.getValue()));
		}
	}

	public String getURLEquivalent()
	{
		String text = "";
		boolean first = true;
		for (GTableItem item: pairsTable.getItems())
		{
			if (!first)
			{
				text += "&";
			}
			first = false;
			try
			{
				text +=
				        new String(DataFormatUtils.encodeData(item.getText(0).getBytes(), DataFormatType.URL_BASIC_ENCODED, null)) + "="
				                + new String(DataFormatUtils.encodeData(item.getText(1).getBytes(), DataFormatType.URL_BASIC_ENCODED, null));
			}
			catch (DataFormatException e)
			{
				Log.error("Weird problem formatting url (" + item.getText(0) + "=" + item.getText(1) + "): " + e.toString(), e);
			}
		}
		return text;
	}
	
	static int[] widths;
	@Override protected void initGUI()
	{
		super.initGUI();
		addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent arg0)
			{
				widths = GuiUtils.getColumnWidths(pairsTable);
			}
		});
		if (widths == null)
		{
			widths = new int[] {150, 500};
		}
		GuiUtils.restoreColumnWidths(pairsTable, widths);
	}

	@Override
	public byte[] getBytes()
    {
	    return getURLEquivalent().getBytes(StringUtils.getDefaultCharset());
    }

	@Override
	public Widget getWidget()
    {
	    return this;
    }

}
