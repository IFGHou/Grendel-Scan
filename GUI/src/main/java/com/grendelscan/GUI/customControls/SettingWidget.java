package com.grendelscan.GUI.customControls;

import org.eclipse.swt.widgets.Control;

import com.grendelscan.GUI.GuiUtils;

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
