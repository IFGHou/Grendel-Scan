package com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites;

import org.apache.http.NameValuePair;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.formatting.DataFormatType;
import com.grendelscan.commons.formatting.DataFormatUtils;
import com.grendelscan.commons.formatting.encoding.UrlEncodingUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GTableItem;
import com.grendelscan.ui.http.transactionDisplay.NamePairComposite;

public class UrlEncodedViewComposite extends NamePairComposite implements ParsedEntityComposite
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlEncodedViewComposite.class);

    static int[] widths;

    public UrlEncodedViewComposite(final GComposite parent, final int style, final int initialHeight, final boolean editable)
    {
        super(parent, style, initialHeight, editable);
    }

    @Override
    public byte[] getBytes()
    {
        return getURLEquivalent().getBytes(StringUtils.getDefaultCharset());
    }

    public String getURLEquivalent()
    {
        String text = "";
        boolean first = true;
        for (GTableItem item : pairsTable.getItems())
        {
            if (!first)
            {
                text += "&";
            }
            first = false;
            try
            {
                text += new String(DataFormatUtils.encodeData(item.getText(0).getBytes(), DataFormatType.URL_BASIC_ENCODED, null)) + "=" + new String(DataFormatUtils.encodeData(item.getText(1).getBytes(), DataFormatType.URL_BASIC_ENCODED, null));
            }
            catch (DataFormatException e)
            {
                LOGGER.error("Weird problem formatting url (" + item.getText(0) + "=" + item.getText(1) + "): " + e.toString(), e);
            }
        }
        return text;
    }

    @Override
    public Widget getWidget()
    {
        return this;
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

    @Override
    public void updateData(final byte[] data)
    {
        String urlEncoded = new String(data, StringUtils.getDefaultCharset());
        pairsTable.removeAll();
        for (NameValuePair pair : URIStringUtils.getQueryParametersFromQuery(urlEncoded))
        {
            addPair(UrlEncodingUtils.decodeUrl(pair.getName()), UrlEncodingUtils.decodeUrl(pair.getValue()));
        }
    }

}
