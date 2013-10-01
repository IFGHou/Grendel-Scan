package com.grendelscan.ui.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.grendelscan.smashers.settings.IntegerOption;
import com.grendelscan.ui.customControls.basic.GText;

public class IntegerModifyListener implements ModifyListener
{
    IntegerOption innerOption;
    GText t;

    public IntegerModifyListener(final IntegerOption innerOption, final GText text)
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
            value = "0";
        }
        innerOption.setValue(Integer.valueOf(value));
    }
}
