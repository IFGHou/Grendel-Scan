package com.grendelscan.testing.modules.settings;


public class SelectableOption extends ConfigurationOption
{
    private boolean selected;

    public SelectableOption(final String name, final boolean defaultValue, final String helpText, final ConfigChangeHandler changeHandler)
    {
        super(name, helpText, changeHandler);
        selected = defaultValue;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(final boolean selected)
    {
        if (selected != this.selected)
        {
            this.selected = selected;
            changed();
        }
    }

}
