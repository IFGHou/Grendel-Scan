package com.grendelscan.fuzzing;

public class PredefinedList implements FuzzVector
{
    private final String[] strings;
    private int current;

    public PredefinedList(final String[] strings)
    {
        super();
        this.strings = strings;
        reset();
    }

    @Override
    public boolean done()
    {
        return current >= strings.length;
    }

    @Override
    public String getNextValue()
    {
        String value = null;
        if (!done())
        {
            value = strings[current++];
        }
        return value;
    }

    public String[] getStrings()
    {
        return strings;
    }

    @Override
    public void reset()
    {
        current = 0;
    }
}
