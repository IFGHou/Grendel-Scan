package com.grendelscan.testing.utils.nikto;

import java.text.ParseException;

public class WebServerNote
{
    String message;
    String regex;

    public WebServerNote(final String[] line) throws ParseException
    {
        if (line.length != 2)
        {
            throw new ParseException("Incorrect number of elements in line.", line.length);
        }

        regex = line[0];
        message = line[1];
    }

}
