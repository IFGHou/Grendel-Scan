package com.grendelscan.ui.http.transactionDisplay;

import org.apache.http.Header;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import com.grendelscan.commons.formatting.encoding.UrlEncodingUtils;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.customControls.basic.GComposite;

public class ParsedHttpHeaderComposite extends NamePairComposite
{

    static int[] widths;

    public ParsedHttpHeaderComposite(final GComposite parent, final int style, final int initialHeight, final boolean editable, final boolean colapsed)
    {
        super(parent, style, initialHeight, editable);
    }

    @Override
    protected void initGUI()
    {
        super.initGUI();
        addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(final DisposeEvent arg0)
            {
                widths = GuiUtils.getColumnWidths(pairsTable);
            }
        });
        if (widths == null)
        {
            widths = new int[] { 150, 500 };
        }
        GuiUtils.restoreColumnWidths(pairsTable, widths);
    }

    public void updateHeaderEncodedData(final Header[] headers)
    {
        pairsTable.removeAll();
        // Matcher m = headerPattern.matcher(headerEncoded);
        for (Header header : headers)
        {
            addPair(UrlEncodingUtils.decodeUrl(header.getName()), UrlEncodingUtils.decodeUrl(header.getValue()));
        }
    }
}
