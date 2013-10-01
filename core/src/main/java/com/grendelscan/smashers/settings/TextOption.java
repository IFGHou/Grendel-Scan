package com.grendelscan.smashers.settings;


public class TextOption extends ConfigurationOption
{
    private boolean multiLine;

    private String value;

    public TextOption(final String name, final String defaultValue, final String helpText, final Boolean multiLine, final ConfigChangeHandler changeHandler)
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

    public void setMultiLine(final boolean multiLine)
    {
        this.multiLine = multiLine;
    }

    public void setValue(final String value)
    {
        if (!value.equals(this.value))
        {
            this.value = value;
            changed();
        }
    }

}
