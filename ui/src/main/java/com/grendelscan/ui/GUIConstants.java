package com.grendelscan.ui;

import org.eclipse.swt.graphics.Font;

import com.grendelscan.ui.Verifiers.EnforceIntegersOnly;

public class GUIConstants
{
    public static final EnforceIntegersOnly integersOnlyVerifyer = new EnforceIntegersOnly();

    public static final String fontName = "Tahoma";
    public static final int fontSize = 8;

    public static Font getFont(final int offset)
    {
        return SWTResourceManager.getFont(fontName, fontSize + offset, 0, true, false);
    }
}
