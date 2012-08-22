/**
 * 
 */
package com.grendelscan.tests.libraries.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.requester.http.transactions.NonScanHttpTransaction;


public class Live implements SearchEngine
{
	private Pattern	liveFindResultPattern	= Pattern.compile("<li><h3><a href=\"([^\"]+)",
													Pattern.CASE_INSENSITIVE);
	private Pattern	liveMoreResultsPattern	= Pattern.compile("FORM=PORE\">Next</a></li></ul>",
													Pattern.CASE_INSENSITIVE);

	@Override
	public Pattern getFindResultPattern()
	{
		return liveFindResultPattern;
	}

	@Override
	public int getQueryIncrement()
	{
		return 200;
	}

	@Override
	public String getQueryURI(String host, int startLocation)
	{
		return "http://search.live.com/results.aspx?q=site%3a" + host + "&first=" + startLocation;
	}

	@Override
	public boolean isMoreResults(String body)
	{
		return liveMoreResultsPattern.matcher(body).find();
	}

	@Override
	public void updateTransaction(NonScanHttpTransaction queryTransaction)
	{
		queryTransaction.addRequestHeader("Cookie", "SRCHHPGUSR=NRSLT=200&NRSPH=0");
	}

}

