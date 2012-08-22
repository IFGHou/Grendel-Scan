package com.grendelscan.tests.libraries.platformErrorMessages;

import java.util.ArrayList;
import java.util.List;

public class PlatformErrorMessageMatchPattern
{
	private List<String>	textPatterns;
	private List<String>	titlePatterns;

	public PlatformErrorMessageMatchPattern()
	{
		textPatterns = new ArrayList<String>(1);
		titlePatterns = new ArrayList<String>(1);
	}

	public void addTextPattern(String textPattern)
	{
		textPatterns.add(normalizePattern(textPattern));
	}

	public void addTitlePattern(String titlePattern)
	{
		titlePatterns.add(normalizePattern(titlePattern));
	}

	public List<String> getTextPatterns()
	{
		return textPatterns;
	}

	public List<String> getTitlePatterns()
	{
		return titlePatterns;
	}

	public String isTextMatch(String text)
	{
		return isMatch(text, textPatterns);
	}

	public String isTitleMatch(String text)
	{
		return isMatch(text, titlePatterns);
	}

	private String isMatch(String text, List<String> patterns)
	{
		String fuzzPattern = "";
		for (String pattern : patterns)
		{
			if (!text.contains(pattern))
			{
				fuzzPattern = "";
				break;
			}
			if (pattern.contains(" "))
			{
				fuzzPattern += "\"" + pattern + "\" ";
			}
			else
			{
				fuzzPattern += pattern + " ";
			}
		}

		return fuzzPattern;
	}

	private String normalizePattern(String pattern)
	{
		return pattern.toLowerCase().replaceAll("\\s+", " ");
	}

}
