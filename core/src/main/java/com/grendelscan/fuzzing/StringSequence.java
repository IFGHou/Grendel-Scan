package com.grendelscan.fuzzing;

import java.util.Arrays;

public class StringSequence implements FuzzVector
{
    private final int minDigits, maxDigits;
    private final char[] characters;
    private int[] current;
    private boolean done;

    public StringSequence(final char[] characters, final int maxDigits, final int minDigits)
    {
        this.characters = characters;
        Arrays.sort(this.characters);
        this.maxDigits = maxDigits;
        this.minDigits = minDigits;
        reset();
    }

    private String arrayToString(final int[] values)
    {
        String value = "";
        for (int i = values.length - 1; i >= 0; i--)
        {
            if (values[i] >= 0)
            {
                value += characters[values[i]];
            }
        }
        return value;
    }

    @Override
    public boolean done()
    {
        return done;
    }

    public char[] getCharacters()
    {
        return characters;
    }

    public int getMaxDigits()
    {
        return maxDigits;
    }

    public int getMinDigits()
    {
        return minDigits;
    }

    @Override
    public String getNextValue()
    {
        String value = arrayToString(current);
        increment(current);
        return value;
    }

    private void increment(final int[] values)
    {
        int index = 0;
        if (max(values))
        {
            done = true;
        }
        else
        {
            while (true && index < values.length)
            {
                if (++values[index] >= characters.length)
                {
                    values[index] = 0;
                    index++;
                }
                else
                {
                    break;
                }
            }
        }
    }

    private boolean max(final int[] values)
    {
        for (int value : values)
        {
            if (value < characters.length)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset()
    {
        done = false;
        current = new int[maxDigits];
        for (int i = 0; i < current.length; i++)
        {
            if (i < minDigits)
            {
                current[i] = 0;
            }
            else
            {
                current[i] = -1;
            }
        }
    }

}
