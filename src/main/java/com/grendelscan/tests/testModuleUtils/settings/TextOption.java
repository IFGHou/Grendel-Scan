package com.grendelscan.tests.testModuleUtils.settings;

public class TextOption extends ConfigurationOption
{
	private boolean	multiLine;

	private String	value;

	public TextOption(String name, String defaultValue, String helpText, Boolean multiLine, ConfigChangeHandler changeHandler)
	{
		super(name, helpText, changeHandler);
		value = defaultValue;
		this.multiLine = multiLine;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isMultiLine()
	{
		return multiLine;
	}

	public void setMultiLine(boolean multiLine)
	{
		this.multiLine = multiLine;
	}

	public void setValue(String value)
	{
		if (!value.equals(this.value))
		{
			this.value = value;
			changed();
		}
	}

}
