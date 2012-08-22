package com.grendelscan.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.grendelscan.logging.Log;

public class RegexUtils
{
	public static boolean validateRegex(String regex)
	{
		boolean good = true;
		try
		{
			Pattern.compile(regex);
		}
		catch (java.util.regex.PatternSyntaxException e)
		{
			good = false;
		}
		
		return good;
	}

	/**
     * Takes a string where each line is a separate regular expression
     * and returns an array of corresponding Pattern objects. Improperly
     * formated regexs are ignored.
     * @param list
     * @return
     */
    public static Pattern[] stringToPatterns(String list)
    {
    	String aList[] = list.split("\n");
    	return stringArrayToPatterns(aList);
    }

	/**
     * Takes a string where each line is a separate regular expression
     * and returns an array of corresponding Pattern objects. Improperly
     * formated regexs are ignored.
     * @param list
     * @return
     */
    public static Pattern[] stringArrayToPatterns(String[] list)
    {
    	List<Pattern> tempPatterns = new ArrayList<Pattern>(1);
    	for (String regex: list)
    	{
    		try
    		{
    			tempPatterns.add(Pattern.compile(regex));
    		}
    		catch (PatternSyntaxException e)
    		{
    			Log.error("Bad regex format: " + e.toString(), e);
    			continue;
    		}
    	}
    	
    	return tempPatterns.toArray(new Pattern[0]);
    }

    public static boolean matchAnyPattern(Collection<Pattern> patterns, String string)
    {
    	for (Pattern pattern: patterns)
    	{
    		if (pattern.matcher(string).matches())
    		{
    			return true;
    		}
    	}
    	return false;
    }
}
