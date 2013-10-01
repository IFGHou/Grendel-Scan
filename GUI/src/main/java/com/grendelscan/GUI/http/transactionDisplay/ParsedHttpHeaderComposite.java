package com.grendelscan.GUI.http.transactionDisplay;

import org.apache.http.Header;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import com.grendelscan.GUI.customControls.basic.GComposite;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.utils.dataFormating.encoding.UrlEncodingUtils;

public class ParsedHttpHeaderComposite extends NamePairComposite
{

	public ParsedHttpHeaderComposite(GComposite parent, int style, int initialHeight, boolean editable, boolean colapsed)
    {
	    super(parent, style, initialHeight, editable);
    }
	
	
	public void updateHeaderEncodedData(Header[] headers)
	{
		pairsTable.removeAll();
//		Matcher m = headerPattern.matcher(headerEncoded);
		for(Header header: headers)
		{
			addPair(UrlEncodingUtils.decodeUrl(header.getName()), UrlEncodingUtils.decodeUrl(header.getValue()));
		}
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
}
