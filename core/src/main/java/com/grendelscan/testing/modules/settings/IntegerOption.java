package com.grendelscan.testing.modules.settings;


public class IntegerOption extends ConfigurationOption
{

    private int value;

    public IntegerOption(final String name, final int defaultValue, final String helpText, final ConfigChangeHandler changeHandler)
    {
        super(name, helpText, changeHandler);
        value = defaultValue;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(final int value)
    {
        if (value != this.value)
        {
            this.value = value;
            changed();
        }
    }
}
