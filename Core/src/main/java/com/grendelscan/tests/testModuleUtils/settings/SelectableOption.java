package com.grendelscan.tests.testModuleUtils.settings;

public class SelectableOption extends ConfigurationOption
{
	private boolean	selected;

	public SelectableOption(String name, boolean defaultValue, String helpText, ConfigChangeHandler changeHandler)
	{
		super(name, helpText, changeHandler);
		selected = defaultValue;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		if (selected != this.selected)
		{
			this.selected = selected;
			changed();
		}
	}

}
