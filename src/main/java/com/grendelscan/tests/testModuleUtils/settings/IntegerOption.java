package com.grendelscan.tests.testModuleUtils.settings;

public class IntegerOption extends ConfigurationOption
{

	private int	value;

	public IntegerOption(String name, int defaultValue, String helpText, ConfigChangeHandler changeHandler)
	{
		super(name, helpText, changeHandler);
		value = defaultValue;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		if (value != this.value)
		{
			this.value = value;
			changed();
		}
	}
}
