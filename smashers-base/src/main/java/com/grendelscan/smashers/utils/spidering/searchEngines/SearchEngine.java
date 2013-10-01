/**
 * 
 */
package com.grendelscan.smashers.utils.spidering.searchEngines;

import java.util.regex.Pattern;

import com.grendelscan.commons.http.transactions.NonScanHttpTransaction;

/**
 * @author david
 *
 */
public interface SearchEngine
{
	public Pattern getFindResultPattern();

	public int getQueryIncrement();

	public String getQueryURI(String host, int startLocation);

	public boolean isMoreResults(String body);

	public void updateTransaction(NonScanHttpTransaction queryTransaction);
}
