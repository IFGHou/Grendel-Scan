package com.grendelscan.testing.modules.settings;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurationOption
{
    protected String name;
    protected String helpText;
    protected List<ConfigChangeHandler> changeHandlers;

    public ConfigurationOption(final String name, final String helpText, final ConfigChangeHandler changeHandler)
    {
        changeHandlers = new ArrayList<ConfigChangeHandler>(1);
        if (changeHandler != null)
        {
            changeHandlers.add(changeHandler);
        }
        this.name = name;
        this.helpText = helpText;
    }

    public void addChangeHandler(final ConfigChangeHandler changeHandler)
    {
        if (!changeHandlers.contains(changeHandler))
        {
            changeHandlers.add(changeHandler);
        }
    }

    protected void addChangeHandlers(final List<ConfigChangeHandler> newHandlers)
    {
        for (ConfigChangeHandler changeHandler : newHandlers)
        {
            changeHandlers.add(changeHandler);
        }
    }

    protected void changed()
    {
        for (ConfigChangeHandler changeHandler : changeHandlers)
        {
            changeHandler.handleChange();
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

}
