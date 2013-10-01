package com.grendelscan.ui.settings;

public interface GrendelSettingsControl
{
	public void updateFromSettings();
	
	/**
	 * 
	 * @return A message text in case of errors
	 */
	public String updateToSettings();
}
