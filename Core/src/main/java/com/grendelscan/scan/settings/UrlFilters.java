/*
 * AllowedHosts.java
 * 
 * Created on September 16, 2007, 9:39 AM
 * 
 * To change this template, choose Tools | Template Manager and open the
 * template in the editor.
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

import com.grendelscan.logging.Log;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.URIStringUtils;



/**
 * 
 * @author Administrator
 */
public class UrlFilters
{
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

	private Pattern baseUriToPattern(String baseUri) throws URISyntaxException 
	{
		return Pattern.compile("^" + Pattern.quote(URIStringUtils.getDirectoryUri(baseUri)) + ".*$");
	}
	
	public void addBaseUriToPatterns(String baseUri) throws URISyntaxException
	{
		URIStringUtils.assertAbsoluteHttpAndValid(baseUri);
		baseUriWhitelists.put(baseUri, baseUriToPattern(baseUri));
	}

	public void removeBaseUriToPatterns(String baseUri)
	{
		baseUriWhitelists.remove(baseUri);
	}
	
	public void addUrlBlacklist(Pattern expression)
	{
		blacklists.add(expression);
		Scan.getScanSettings().updateSettingsFile();
	}

	public void removeUrlBlacklist(Pattern expression)
	{
		blacklists.remove(expression);
		Scan.getScanSettings().updateSettingsFile();
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
	
	/**
	 * Intentionally default visibility
	 * @param expression
	 */
	void addLoadedUrlBlacklist(String expression)
	{
		Pattern r = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		blacklists.add(r);
	}
	
	
	public void addUrlWhitelist(Pattern expression)
	{
		whitelists.add(expression);
		Scan.getScanSettings().updateSettingsFile();
	}
	
	public void removeUrlWhitelist(Pattern expression)
	{
		whitelists.remove(expression);
		Scan.getScanSettings().updateSettingsFile();
	}
	
	
	/**
	 * Intentionally default visibility
	 * @param expression
	 */
	void addLoadedUrlWhitelist(String expression)
	{
		Pattern r = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		whitelists.add(r);
	}
	
	
	public List<String> getBlacklistsAsString()
	{
		List<String> expressions = new ArrayList<String>();
		for (Pattern pattern: blacklists)
		{
			expressions.add(pattern.pattern());
		}
		return expressions;
	}
	
	
	public List<String> getWhitelistsAsString()
	{
		List<String> expressions = new ArrayList<String>(whitelists.size());
		for (Pattern pattern: whitelists)
		{
			expressions.add(pattern.pattern());
		}
		return expressions;
	}
	
	public boolean isUriAllowed(String uri)
	{
		boolean isOkay = false;
		Matcher matcher;
		for (Pattern pattern: baseUriWhitelists.values())
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
			for (Pattern pattern: whitelists)
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
			for (Pattern pattern: blacklists)
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
			Log.trace(uri + " is not requestable");
		}
		
		return isOkay;
	}
}
