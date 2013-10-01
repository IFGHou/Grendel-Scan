package com.grendelscan.GUI.settings;

public interface GrendelSettingsControl
{
	public void updateFromSettings();
	
	/**
	 * 
	 * @return A message text in case of errors
	 */
	public String updateToSettings();
}
