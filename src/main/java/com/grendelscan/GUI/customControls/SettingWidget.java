package com.grendelscan.GUI.customControls;

import org.eclipse.swt.widgets.Control;

import com.cloudgarden.resource.SWTResourceManager;

public abstract class SettingWidget
{
	private Control control;
	
	public void flagChange()
	{
		control.setForeground(SWTResourceManager.getColor(255, 0, 0));
	}
	
	public void unflagChange()
	{
		control.setForeground(SWTResourceManager.getColor(0, 0, 0));
	}
	
}
