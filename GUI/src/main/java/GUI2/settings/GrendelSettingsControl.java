package com.grendelscan.GUI2.settings;

public interface GrendelSettingsControl
{
	public void updateFromSettings();
	
	/**
	 * 
	 * @return A message text in case of errors
	 */
	public String updateToSettings();
}
