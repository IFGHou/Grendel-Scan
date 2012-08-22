package com.grendelscan.tests.testModuleUtils.settings;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurationOption
{
	protected String	name;
	protected String				helpText;
	protected List<ConfigChangeHandler> changeHandlers;

	public ConfigurationOption(String name, String helpText, ConfigChangeHandler changeHandler)
	{
		changeHandlers = new ArrayList<ConfigChangeHandler>(1);
		if (changeHandler != null)
		{
			changeHandlers.add(changeHandler);
		}
		this.name = name;
		this.helpText = helpText;
	}
	
	public void addChangeHandler(ConfigChangeHandler changeHandler)
	{
		if (!changeHandlers.contains(changeHandler))
		{
			changeHandlers.add(changeHandler);
		}
	}

	protected void addChangeHandlers(List<ConfigChangeHandler> newHandlers)
	{
		for(ConfigChangeHandler changeHandler: newHandlers)
		{
			changeHandlers.add(changeHandler);
		}
	}

	public String getHelpText()
	{
		return helpText;
	}

	public String getName()
	{
		return name;
	}

	protected void changed()
	{
		for(ConfigChangeHandler changeHandler: changeHandlers)
		{
			changeHandler.handleChange();
		}
	}

}
