package com.grendelscan.fuzzing;

public class NumericSequence implements FuzzVector
{
    private final double begin, end, increment;
    private double current;
    private final boolean ascending;
    private final int decimalLocations;

    public NumericSequence(final boolean ascending, final double increment, final double begin, final double end, final int decimalLocations)
    {
        super();
        this.ascending = ascending;
        this.increment = increment;
        this.begin = begin;
        this.end = end;
        // truncateValue = (int) Math.pow(10, decimalLocations);
        this.decimalLocations = decimalLocations;
        reset();
    }

    @Override
    public boolean done()
    {
        return !(ascending && current <= end || !ascending && current >= end);
    }

    public double getBegin()
    {
        return begin;
    }

    public int getDecimalLocations()
    {
        return decimalLocations;
    }

    public double getEnd()
    {
        return end;
    }

    public double getIncrement()
    {
        return increment;
    }

    @Override
    public String getNextValue()
    {
        String value = null;
        if (!done())
        {
            // if (decimalLocations > 0)
            // {
            // // value = String.valueOf((double)Math.floor(current * truncateValue) / (double)truncateValue);
            // }
            // else
            // {
            // value = String.valueOf((int) Math.floor(current));
            // }
            if (decimalLocations > 0)
            {
                value = String.format("%0" + decimalLocations + "f", current);
            }
            else
            {
                value = String.format("%d", (int) current);
            }

            if (ascending)
            {
                current += increment;
            }
            else
            {
                current -= increment;
            }
        }

        return value;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    @Override
    public void reset()
    {
        current = begin;
    }

}
