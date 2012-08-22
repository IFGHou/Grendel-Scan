package com.grendelscan.GUI.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.tests.testModuleUtils.settings.IntegerOption;

public class IntegerModifyListener implements ModifyListener
{
	IntegerOption innerOption;
	Text t;

	public IntegerModifyListener(IntegerOption innerOption, Text text)
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
			value = "0";
		}
		innerOption.setValue(Integer.valueOf(value));
    }
}
