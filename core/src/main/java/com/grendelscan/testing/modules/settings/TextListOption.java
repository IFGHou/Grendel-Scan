/**
 * 
 */
package com.grendelscan.testing.modules.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david
 * 
 */
public class TextListOption extends ConfigurationOption
{

    private final List<String> data;

    /**
     * @param name
     * @param helpText
     */
    public TextListOption(final String name, final String helpText, final ConfigChangeHandler changeHandler)
    {
        super(name, helpText, changeHandler);
        data = new ArrayList<String>(1);
    }

    public boolean add(final String e)
    {
        if (data.contains(e))
        {
            return false;
        }
        data.add(e);
        changed();
        return true;
    }

    public void clear()
    {
        data.clear();
        changed();
    }

    public List<String> getReadOnlyData()
    {
        return new ArrayList<String>(data);
    }

    public boolean remove(final Object o)
    {
        if (!data.contains(o))
        {
            return false;
        }
        data.remove(o);
        changed();
        return true;
    }

}
