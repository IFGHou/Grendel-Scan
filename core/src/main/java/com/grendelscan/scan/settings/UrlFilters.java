/*
 * AllowedHosts.java
 * 
 * Created on September 16, 2007, 9:39 AM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.scan.settings;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.scan.Scan;

/**
 * 
 * @author Administrator
 */
public class UrlFilters
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlFilters.class);
    private final List<Pattern> blacklists;
    private final List<Pattern> whitelists;
    private final Map<String, Pattern> baseUriWhitelists;

    /** Creates a new instance of AllowedHosts */
    public UrlFilters()
    {
        whitelists = Collections.synchronizedList(new ArrayList<Pattern>(1));
        blacklists = Collections.synchronizedList(new ArrayList<Pattern>(1));
        baseUriWhitelists = Collections.synchronizedMap(new HashMap<String, Pattern>(1));
    }

    public void addBaseUriToPatterns(final String baseUri) throws URISyntaxException
    {
        URIStringUtils.assertAbsoluteHttpAndValid(baseUri);
        baseUriWhitelists.put(baseUri, baseUriToPattern(baseUri));
    }

    /**
     * Intentionally default visibility
     * 
     * @param expression
     */
    void addLoadedUrlBlacklist(final String expression)
    {
        Pattern r = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        blacklists.add(r);
    }

    /**
     * Intentionally default visibility
     * 
     * @param expression
     */
    void addLoadedUrlWhitelist(final String expression)
    {
        Pattern r = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        whitelists.add(r);
    }

    public void addUrlBlacklist(final Pattern expression)
    {
        blacklists.add(expression);
        Scan.getScanSettings().updateSettingsFile();
    }

    public void addUrlWhitelist(final Pattern expression)
    {
        whitelists.add(expression);
        Scan.getScanSettings().updateSettingsFile();
    }

    private Pattern baseUriToPattern(final String baseUri) throws URISyntaxException
    {
        return Pattern.compile("^" + Pattern.quote(URIStringUtils.getDirectoryUri(baseUri)) + ".*$");
    }

    public void clearUrlBlacklists()
    {
        blacklists.clear();
        Scan.getScanSettings().updateSettingsFile();
    }

    public void clearUrlWhitelists()
    {
        whitelists.clear();
        Scan.getScanSettings().updateSettingsFile();
    }

    public List<String> getBlacklistsAsString()
    {
        List<String> expressions = new ArrayList<String>();
        for (Pattern pattern : blacklists)
        {
            expressions.add(pattern.pattern());
        }
        return expressions;
    }

    public List<String> getWhitelistsAsString()
    {
        List<String> expressions = new ArrayList<String>(whitelists.size());
        for (Pattern pattern : whitelists)
        {
            expressions.add(pattern.pattern());
        }
        return expressions;
    }

    public boolean isUriAllowed(final String uri)
    {
        boolean isOkay = false;
        Matcher matcher;
        for (Pattern pattern : baseUriWhitelists.values())
        {
            matcher = pattern.matcher(uri);
            if (matcher.find())
            {
                isOkay = true;
                break;
            }
        }

        if (!isOkay)
        {
            for (Pattern pattern : whitelists)
            {
                matcher = pattern.matcher(uri);
                if (matcher.find())
                {
                    isOkay = true;
                    break;
                }
            }
        }

        if (isOkay)
        {
            for (Pattern pattern : blacklists)
            {
                matcher = pattern.matcher(uri);
                if (matcher.find())
                {
                    isOkay = false;
                    break;
                }
            }
        }

        if (!isOkay)
        {
            LOGGER.trace(uri + " is not requestable");
        }

        return isOkay;
    }

    public void removeBaseUriToPatterns(final String baseUri)
    {
        baseUriWhitelists.remove(baseUri);
    }

    public void removeUrlBlacklist(final Pattern expression)
    {
        blacklists.remove(expression);
        Scan.getScanSettings().updateSettingsFile();
    }

    public void removeUrlWhitelist(final Pattern expression)
    {
        whitelists.remove(expression);
        Scan.getScanSettings().updateSettingsFile();
    }
}
