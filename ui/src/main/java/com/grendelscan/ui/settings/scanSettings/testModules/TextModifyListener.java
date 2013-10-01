package com.grendelscan.ui.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.grendelscan.smashers.settings.TextOption;
import com.grendelscan.ui.customControls.basic.GText;

public class TextModifyListener implements ModifyListener
{
    TextOption innerOption;
    GText t;

    public TextModifyListener(final TextOption innerOption, final GText text)
    {
        this.innerOption = innerOption;
        t = text;
    }

    @Override
    public void modifyText(final ModifyEvent e)
    {
        String value = t.getText();
        if (value == null)
        {
            value = "";
        }
        innerOption.setValue(value);
    }
}
