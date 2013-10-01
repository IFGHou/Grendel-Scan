package com.grendelscan.GUI.settings.scanSettings.testModules;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.tests.testModuleUtils.settings.IntegerOption;

public class IntegerModifyListener implements ModifyListener
{
	IntegerOption innerOption;
	GText t;

	public IntegerModifyListener(IntegerOption innerOption, GText text)
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
