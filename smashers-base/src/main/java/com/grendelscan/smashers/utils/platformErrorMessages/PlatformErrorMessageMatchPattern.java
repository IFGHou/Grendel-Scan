package com.grendelscan.smashers.utils.platformErrorMessages;

import java.util.ArrayList;
import java.util.List;

public class PlatformErrorMessageMatchPattern
{
    private final List<String> textPatterns;
    private final List<String> titlePatterns;

    public PlatformErrorMessageMatchPattern()
    {
        textPatterns = new ArrayList<String>(1);
        titlePatterns = new ArrayList<String>(1);
    }

    public void addTextPattern(final String textPattern)
    {
        textPatterns.add(normalizePattern(textPattern));
    }

    public void addTitlePattern(final String titlePattern)
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

    private String isMatch(final String text, final List<String> patterns)
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

    public String isTextMatch(final String text)
    {
        return isMatch(text, textPatterns);
    }

    public String isTitleMatch(final String text)
    {
        return isMatch(text, titlePatterns);
    }

    private String normalizePattern(final String pattern)
    {
        return pattern.toLowerCase().replaceAll("\\s+", " ");
    }

}
