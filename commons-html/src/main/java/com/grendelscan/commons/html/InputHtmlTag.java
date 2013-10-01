package com.grendelscan.commons.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHtmlTag
{

    private static final Pattern AttributesPattern = Pattern.compile("(<input(.+)>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private static final Pattern baseInputPattern = Pattern.compile("(<input(.+)>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public InputHtmlTag(final String rawHtml)
    {
        String rawAttributes = getRawAttributes(rawHtml);
    }

    private String getRawAttributes(final String rawHtml)
    {
        Matcher baseMatcher = baseInputPattern.matcher(rawHtml);
        if (baseMatcher.find())
        {
            return baseMatcher.group(1);
        }
        return "";
    }
}
