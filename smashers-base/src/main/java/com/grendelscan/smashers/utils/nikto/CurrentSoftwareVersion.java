package com.grendelscan.smashers.utils.nikto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.commons.StringUtils;

public class CurrentSoftwareVersion
{
    private static Pattern versionSectionPattern = Pattern.compile("((?:\\d++)|(?:[a-z]++))", Pattern.CASE_INSENSITIVE);
    private final String currentVersion;
    private double currentVersionArray[];
    private final String message;
    private final String softwareName;
    private String versionSeparator;

    /**
     * 
     * @param line
     * @throws ParseException
     */
    public CurrentSoftwareVersion(final String[] line) throws ParseException
    {
        if (line.length != 3)
        {
            throw new ParseException("Incorrect number of elements in line.", line.length);
        }
        softwareName = line[0];
        currentVersion = line[1];
        message = line[2];

        versionSeparator = softwareName.substring(softwareName.length() - 1);

        // Some versions don't have a separator at the end of the name.
        if (versionSeparator.matches("[a-zA-Z0-9]"))
        {
            versionSeparator = "/";
        }

        // Some version lines have the product name and some don't
        String temp[] = currentVersion.split(Pattern.quote(versionSeparator));
        if (temp.length > 1)
        {
            currentVersionArray = parseVersion(temp[1]);
        }
        else
        {
            currentVersionArray = parseVersion(temp[0]);
        }
    }

    /**
     * returns 0 if they are equal, less than 0 if a is older, more than 0 if b is older
     * 
     * @param a
     * @param b
     * @return
     */
    private int compareVersions(final double a[], final double b[])
    {
        int length = a.length > b.length ? a.length : b.length;
        for (int index = 0; index < length; index++)
        {
            // if "a" is out of sections, then it is oldest
            if (a.length <= index)
            {
                return -1;
            }
            if (b.length <= index)
            {
                return 1;
            }

            // compare the numbers directly if they both exist
            int compare = Double.valueOf(a[index]).compareTo(b[index]);
            if (compare != 0)
            {
                return compare;
            }

            // if they still are equal, keep going
        }

        // never found an inequality
        return 0;
    }

    public boolean doesSoftwareNameMatch(final String software)
    {
        return software.toUpperCase().startsWith(softwareName.toUpperCase());
    }

    public String extractVersionComponent(final String softwareString)
    {
        String temp[] = softwareString.split(Pattern.quote(versionSeparator));
        return temp[1];
    }

    public String getFriendlySoftwareName()
    {
        return softwareName.replaceFirst("\\W+$", "");
    }

    public String getWarningMessage(final String runningVersion)
    {
        return message.replace("@RUNNING_VER", runningVersion).replace("@CURRENT_VER", currentVersion);
    }

    /**
     * Will return false if the software names don't appear to match
     * 
     * @param softwareString
     * @return
     */
    public boolean isOutdated(final String softwareString)
    {
        boolean outdated = false;

        if (doesSoftwareNameMatch(softwareString))
        {
            double runningVersion[] = parseVersion(extractVersionComponent(softwareString));
            if (compareVersions(runningVersion, currentVersionArray) < 0)
            {
                outdated = true;
            }
        }

        return outdated;
    }

    private double[] parseVersion(final String version)
    {
        ArrayList<Double> components = new ArrayList<Double>();
        Matcher m = versionSectionPattern.matcher(version);

        while (m.find())
        {
            components.add(StringUtils.getNumericValue(m.group(1)));
        }

        double fullVersion[] = new double[components.size()];
        int index = 0;
        for (double num : components)
        {
            fullVersion[index++] = num;
        }

        return fullVersion;
    }

}

// "AbyssLib/","1.0.7","@RUNNING_VER appears to be outdated (current is at least
// @CURRENT_VER)"
