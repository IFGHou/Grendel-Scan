/**
 * 
 */
package com.grendelscan.testing.utils.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.commons.http.transactions.NonScanHttpTransaction;

public class Google implements SearchEngine
{
    private final Pattern googleFindResultPattern = Pattern.compile("<div class=g><a href=\"([^\"]+)", Pattern.CASE_INSENSITIVE);
    private final Pattern googleMoreResultsPattern = Pattern.compile("<span>Next</span>", Pattern.CASE_INSENSITIVE);

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
    public String getQueryURI(final String host, final int startLocation)
    {
        return "http://www.google.com/search?filter=0&num=100&as_sitesearch=" + host + "&start=" + startLocation;
    }

    @Override
    public boolean isMoreResults(final String body)
    {
        return googleMoreResultsPattern.matcher(body).find();
    }

    // Not needed for Google
    @Override
    public void updateTransaction(final NonScanHttpTransaction queryTransaction)
    {
    }

}
