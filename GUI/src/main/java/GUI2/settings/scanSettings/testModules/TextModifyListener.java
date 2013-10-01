package com.grendelscan.GUI2.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.tests.testModuleUtils.settings.TextOption;

public class TextModifyListener implements ModifyListener
{
	TextOption innerOption;
	GText t;

	public TextModifyListener(TextOption innerOption, GText text)
    {
        this.innerOption = innerOption;
        this.t = text;
    }

    @Override
	public void modifyText(ModifyEvent e)
    {
		String value = t.getText();
		if (value == null)
		{
			value = "";
		}
		innerOption.setValue(value);
    }
}
