package com.grendelscan.tests.testModuleUtils.settings;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectOptionGroup extends ConfigurationOption implements OptionGroup
{
	List<SelectableOption>	options;

	public MultiSelectOptionGroup(String name, String helpText, ConfigChangeHandler changeHandler)
	{
		super(name, helpText, changeHandler);
		options = new ArrayList<SelectableOption>();
	}

	public void addOption(SelectableOption option)
	{
		options.add(option);
	}

	public void addOption(String name, boolean defaultValue, String helpText)
	{
		SelectableOption option = new SelectableOption(name, defaultValue, helpText, null);
		option.addChangeHandlers(changeHandlers);
		options.add(option);
	}

	@Override
	public List<SelectableOption> getAllOptions()
	{
		return options;
	}
}
