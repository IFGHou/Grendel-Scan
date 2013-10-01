package com.grendelscan.ui.customControls;

public interface AddRemoveHandler
{
	public void addItem(String item) throws Throwable;
	public void removeItem(String item) throws Throwable;
	public void clear() throws Throwable;
}
