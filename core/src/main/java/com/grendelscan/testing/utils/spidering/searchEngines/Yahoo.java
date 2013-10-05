/**
 * 
 */
package com.grendelscan.testing.utils.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.commons.http.transactions.NonScanHttpTransaction;

/**
 * @author david
 * 
 */
public class Yahoo implements SearchEngine
{
    private final Pattern yahooFindResultPattern = Pattern.compile("\r?\n[^\t]*\t([^\t]+)");

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
    public String getQueryURI(final String host, final int startLocation)
    {
        return "https://siteexplorer.search.yahoo.com/advtsv?p=http%3A%2F%2F" + host;
    }

    // Yahoo will only return 1000 results
    @Override
    public boolean isMoreResults(final String body)
    {
        return false;
    }

    @Override
    public void updateTransaction(final NonScanHttpTransaction queryTransaction)
    {
    }

}
