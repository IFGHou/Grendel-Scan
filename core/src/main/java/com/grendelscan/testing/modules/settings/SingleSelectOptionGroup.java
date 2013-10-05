package com.grendelscan.testing.modules.settings;

import java.util.ArrayList;
import java.util.List;

public class SingleSelectOptionGroup extends ConfigurationOption implements OptionGroup
{
    List<SelectableOption> options;
    SelectableOption selectedOption;

    public SingleSelectOptionGroup(final String name, final String helpText, final ConfigChangeHandler changeHandler)
    {
        super(name, helpText, changeHandler);
        options = new ArrayList<SelectableOption>();

    }

    public void addOption(final SelectableOption option)
    {
        options.add(option);
    }

    public void addOption(final String name, final String helpText)
    {
        SelectableOption option = new SelectableOption(name, false, helpText, null);
        option.addChangeHandlers(changeHandlers);
        options.add(option);
    }

    @Override
    public List<SelectableOption> getAllOptions()
    {
        return options;
    }

    public SelectableOption getSelectedOption()
    {
        return selectedOption;
    }

    public void setSelectedOption(final SelectableOption selectedOption)
    {
        if (options.contains(selectedOption) && !selectedOption.equals(this.selectedOption))
        {
            this.selectedOption = selectedOption;
            changed();
        }
    }
}
