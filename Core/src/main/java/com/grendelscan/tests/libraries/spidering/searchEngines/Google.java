/**
 * 
 */
package com.grendelscan.tests.libraries.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.requester.http.transactions.NonScanHttpTransaction;

public class Google implements SearchEngine
{
	private Pattern	googleFindResultPattern		= Pattern.compile("<div class=g><a href=\"([^\"]+)",
														Pattern.CASE_INSENSITIVE);
	private Pattern	googleMoreResultsPattern	= Pattern.compile("<span>Next</span>", Pattern.CASE_INSENSITIVE);

	@Override
	public Pattern getFindResultPattern()
	{
		return googleFindResultPattern;
	}

	@Override
	public int getQueryIncrement()
	{
		return 100;
	}

	@Override
	public String getQueryURI(String host, int startLocation)
	{
		return "http://www.google.com/search?filter=0&num=100&as_sitesearch=" + host + "&start=" + startLocation;
	}

	@Override
	public boolean isMoreResults(String body)
	{
		return googleMoreResultsPattern.matcher(body).find();
	}

	// Not needed for Google
	@Override
	public void updateTransaction(NonScanHttpTransaction queryTransaction)
	{
	}

}
