package com.grendelscan.ui.customControls;

import org.eclipse.swt.widgets.Control;

import com.grendelscan.ui.GuiUtils;

public abstract class SettingWidget
{
	private Control control;
	
	public void flagChange()
	{
		control.setForeground(GuiUtils.getColor(255, 0, 0));
	}
	
	public void unflagChange()
	{
		control.setForeground(GuiUtils.getColor(0, 0, 0));
	}
	
}
