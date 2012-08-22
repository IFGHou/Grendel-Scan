package com.grendelscan.GUI.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.tests.testModuleUtils.settings.TextOption;

public class TextModifyListener implements ModifyListener
{
	TextOption innerOption;
	Text t;

	public TextModifyListener(TextOption innerOption, Text text)
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
