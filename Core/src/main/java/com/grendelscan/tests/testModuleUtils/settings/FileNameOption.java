package com.grendelscan.tests.testModuleUtils.settings;

public class FileNameOption extends TextOption
{
	private boolean	directory;

	public FileNameOption(String name, String defaultValue, String helpText, boolean directory, ConfigChangeHandler changeHandler)
	{
		super(name, defaultValue, helpText, false, changeHandler);
		this.directory = directory;
	}

	public boolean isDirectory()
	{
		return directory;
	}

	@Override
	public boolean isMultiLine()
	{
		return false;
	}

}
