package com.grendelscan.tests.libraries.nikto;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;

public class NiktoTest
{
	private String	categories;
	private String	data;
	private String	headers;
	private String	matchAndPattern;
	private String	matchAntiPattern1;
	private String	matchAntiPattern2;
	private String	matchOrPattern;
	private String	matchPattern;
	private String	message;
	private String	method;
	private int		osvdbNumber;
	private int		testNumber;
	private String	testUrl;
	

	/*
	 * 0 "000001", - test number 1 "0", - osvdb 2 "b", - test categories 3
	 * "/TiVoConnect?Command=QueryServer", - url 4 "GET", - method 5 "Calypso
	 * Server", - match pattern 6 "", - match or 7 "", - match and 8 "", - anti
	 * match pattern 1 9 "", - anti match pattern 1 10 "The Tivo Calypso server
	 * is running. This page will display the version and platform it is running
	 * on. Other URLs may allow download of media.", 11 "", - data 12 "" -
	 * headers
	 */

	public NiktoTest(String[] line) throws ParseException
	{
		if (line.length != 13)
		{
			throw new ParseException("Incorrect number of elements in line.", line.length);
		}
		try
		{
			testNumber = Integer.valueOf(line[0]);
			osvdbNumber = Integer.valueOf(line[1]);
		}
		catch (NumberFormatException e)
		{
			throw new ParseException("Not a number where expected: " + e.toString(), 0);
		}

		categories = line[2];
		testUrl = fixUrlBugs(StringUtils.unescapePerlStrings(line[3]));

		method = line[4];
		matchPattern = StringUtils.unescapePerlStrings(line[5]);
		matchOrPattern = StringUtils.unescapePerlStrings(line[6]);
		matchAndPattern = StringUtils.unescapePerlStrings(line[7]);
		matchAntiPattern1 = StringUtils.unescapePerlStrings(line[8]);
		matchAntiPattern2 = StringUtils.unescapePerlStrings(line[9]);
		message = line[10];
		data = line[11];
		headers = line[12];
	}

	public Set<String> constructTestUris(String baseUri, int testJobId) throws InterruptedScanException
	{
		String uriPrefix;
		if (baseUri.endsWith("/"))
		{
			uriPrefix = baseUri.substring(0, baseUri.length() - 1);
		}
		else
		{
			uriPrefix = baseUri;
		}
		Set<String> uris = new HashSet<String>();

		uris.add(uriPrefix + testUrl);
		if (testUrl.contains("@"))
		{
			for (String variableName : Nikto.getInstance().getNiktoVariables().keySet())
			{
				if (testUrl.toUpperCase().contains(variableName.toUpperCase()))
				{
					Set<String> tempStrings = new HashSet<String>(uris);
					for (String tempUrl : tempStrings)
					{
						uris.remove(tempUrl);
						Pattern p = Pattern.compile(Pattern.quote(variableName), Pattern.CASE_INSENSITIVE);
						Matcher m = p.matcher(tempUrl);
						List<String> values;
						if (variableName.equals("@CGIDIRS"))
						{
							values = Nikto.getInstance().getCgiDirs(baseUri, testJobId);
						}
						else
						{
							values = Nikto.getInstance().getNiktoVariables().get(variableName);
						}
						
						if (values == null)
						{
							return uris;
						}

						for (String value : values)
						{
							String newUrl = URIStringUtils.removeDoubleSlashesFromDir(m.replaceAll(value));
							uris.add(newUrl);
						}
					}
				}
			}
		}

		return uris;
	}

	public String getCategories()
	{
		return categories;
	}

	public String getData()
	{
		return data;
	}

	public String getHeaders()
	{
		return headers;
	}

	public String getMatchAndPattern()
	{
		return matchAndPattern;
	}

	public String getMatchAntiPattern1()
	{
		return matchAntiPattern1;
	}

	public String getMatchAntiPattern2()
	{
		return matchAntiPattern2;
	}

	public String getMatchOrPattern()
	{
		return matchOrPattern;
	}

	public String getMatchPattern()
	{
		return matchPattern;
	}

	public String getMessage()
	{
		return message;
	}

	public String getMethod()
	{
		return method;
	}

	public int getOsvdbNumber()
	{
		return osvdbNumber;
	}

	public int getTestNumber()
	{
		return testNumber;
	}

	public String getUrl()
	{
		return testUrl;
	}

	public boolean matches(StandardHttpTransaction transaction, String baseUri, int testJobId) throws NumberFormatException, InterruptedScanException
	{
		boolean matches = false;
		if ((transaction.getLogicalResponseCodeWithoutProfileGeneration() != 404)
				&& (matchesPattern(transaction, matchPattern) || (!matchOrPattern.equals("") && matchesPattern(
						transaction, matchOrPattern)))
				&& matchesPattern(transaction, matchAndPattern)
				&& !( // Check the anti patterns if they exist
				(!matchAntiPattern1.equals("") && matchesPattern(transaction, matchAntiPattern1)) || (!matchAntiPattern2
						.equals("") && matchesPattern(transaction, matchAntiPattern2))) && checkIndexPHP(transaction, baseUri, testJobId))
		{
			matches = true;
		}
		
		return matches;
	}

	/**
	 * Checks with index.php have some problems with false positives. This
	 * emulates Nikto's logic for dealing with it.
	 * 
	 * @param transaction
	 * @return
	 * @throws InterruptedScanException 
	 */
	private boolean checkIndexPHP(StandardHttpTransaction transaction, String baseUri, int testJobId) throws InterruptedScanException
	{
		boolean okay = true;
		if (testUrl.toUpperCase().startsWith("/INDEX.PHP?"))
		{
			okay = !Nikto.getInstance().isNormalIndexPHP(transaction, baseUri, testJobId);
		}
		return okay;
	}

	/**
	 * There are several url bugs in the Nikto test database. This will try to
	 * fix them.
	 * 
	 * @param url
	 * @return
	 */
	private String fixUrlBugs(String url)
	{
		String newUrl = url;

		/*
		 * Not all test urls start with a forward slash
		 */

		if (!newUrl.substring(0, 1).equals("/"))
		{
			newUrl = "/" + newUrl;
		}

		/*
		 * Assume that if there is one misplaced percent, they all are.
		 */
		if (newUrl.matches(".*%(?![0-9a-fA-F]{2}).*"))
		{
			newUrl = newUrl.replace("%", "%25");
		}

		/*
		 * There are several tests that have two forward slashes by mistake.
		 * That won't cause a problem for most web servers, but it does cause a
		 * problem for Java.net.URI.
		 * 
		 * Until I create a completly independent HTTP handler for
		 * StandardHttpTransaction, this needs to be fixed.
		 */
		if (newUrl.startsWith("//"))
		{
			newUrl = newUrl.substring(1);
		}
		
		newUrl = newUrl.replaceAll(";", "%3B"); // semicolons in the path causes problems

		return newUrl;
	}

	private boolean matchesPattern(StandardHttpTransaction transaction, String pattern) throws NumberFormatException, InterruptedScanException
	{
		boolean matches = false;
		if (pattern.matches("\\d{3}") && (transaction.getLogicalResponseCodeWithoutProfileGeneration() == Integer.valueOf(pattern)))
		{
			matches = true;
		}
		else
		{
			if ((new String(transaction.getResponseWrapper().getBody())).toUpperCase().contains(pattern.toUpperCase()))
			{
				matches = true;
			}
		}
		return matches;
	}
}
