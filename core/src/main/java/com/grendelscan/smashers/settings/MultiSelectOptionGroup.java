package com.grendelscan.smashers.settings;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectOptionGroup extends ConfigurationOption implements OptionGroup
{
    List<SelectableOption> options;

    public MultiSelectOptionGroup(final String name, final String helpText, final ConfigChangeHandler changeHandler)
    {
        super(name, helpText, changeHandler);
        options = new ArrayList<SelectableOption>();
    }

    public void addOption(final SelectableOption option)
    {
        options.add(option);
    }

    public void addOption(final String name, final boolean defaultValue, final String helpText)
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
