/**
 * 
 */
package com.grendelscan.tests.libraries.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.requester.http.transactions.NonScanHttpTransaction;

/**
 * @author david
 *
 */
public class Yahoo implements SearchEngine
{
	private Pattern	yahooFindResultPattern	= Pattern.compile("\r?\n[^\t]*\t([^\t]+)");

	@Override
	public Pattern getFindResultPattern()
	{
		return yahooFindResultPattern;
	}

	@Override
	public int getQueryIncrement()
	{
		return 1000;
	}

	@Override
	public String getQueryURI(String host, int startLocation)
	{
		return "https://siteexplorer.search.yahoo.com/advtsv?p=http%3A%2F%2F" + host;
	}

	// Yahoo will only return 1000 results
	@Override
	public boolean isMoreResults(String body)
	{
		return false;
	}

	@Override
	public void updateTransaction(NonScanHttpTransaction queryTransaction)
	{
	}

}