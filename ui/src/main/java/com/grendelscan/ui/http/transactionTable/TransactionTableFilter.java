/**
 * 
 */
package com.grendelscan.ui.http.transactionTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.grendelscan.scan.TransactionSource;

/**
 * @author david
 * 
 */
public class TransactionTableFilter extends ViewerFilter
{

	private String					hostPattern;

	private Pattern					hostRegex;
	private String					pathPattern;
	private Pattern					pathRegex;
	private String					queryPattern;
	private Pattern					queryRegex;
	private String					responseCodePattern;

	private Pattern					responseCodeRegex;
	private List<TransactionSource>	sources;
	private List<String>			stringSources;

	public TransactionTableFilter()
	{
		setHostPattern("");
		setPathPattern("");
		setQueryPattern("");
		setResponseCodePattern("");
		sources = new ArrayList<TransactionSource>(1);
		stringSources = new ArrayList<String>(1);
		sources.addAll(TransactionSource.allSources());
	}

	public void addSource(TransactionSource source)
	{
		sources.add(source);
		stringSources.add(source.getText());
	}

	public void clearSources()
	{
		sources.clear();
		stringSources.clear();
	}

	public boolean containsSource(TransactionSource source)
	{
		return sources.contains(source);
	}

	public String getHostPattern()
	{
		return hostPattern;
	}

	public Pattern getHostRegex()
	{
		return hostRegex;
	}

	public String getPathPattern()
	{
		return pathPattern;
	}

	public Pattern getPathRegex()
	{
		return pathRegex;
	}

	public String getQueryPattern()
	{
		return queryPattern;
	}

	public Pattern getQueryRegex()
	{
		return queryRegex;
	}

	public String getResponseCodePattern()
	{
		return responseCodePattern;
	}

	public Pattern getResponseCodeRegex()
	{
		return responseCodeRegex;
	}

	public TransactionSource[] getSources()
	{
		return sources.toArray(new TransactionSource[0]);
	}

	public List<String> getStringSources()
	{
		return stringSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer arg0, Object arg1, Object transaction)
	{
		TransactionSummary transactionSummary = (TransactionSummary) transaction;
		boolean good = true;

		if (!(stringSources.contains(transactionSummary.getSource())
				&& ((getHostRegex() == null) || getHostRegex().matcher(transactionSummary.getHost()).find())
				&& ((getPathRegex() == null) || getPathRegex().matcher(transactionSummary.getPath()).find())
				&& ((getQueryRegex() == null) || getQueryRegex().matcher(transactionSummary.getQuery()).find()) && ((getResponseCodeRegex() == null) || getResponseCodeRegex().matcher(String.valueOf(transactionSummary.getResponseCode())).find())))
		{
			good = false;
		}

		return good;
	}

	public void setHostPattern(String hostPattern)
	{
		this.hostPattern = hostPattern;
		hostRegex = Pattern.compile(hostPattern, Pattern.CASE_INSENSITIVE);
	}

	public void setPathPattern(String pathPattern)
	{
		this.pathPattern = pathPattern;
		pathRegex = Pattern.compile(pathPattern, Pattern.CASE_INSENSITIVE);
	}

	public void setQueryPattern(String queryPattern)
	{
		this.queryPattern = queryPattern;
		queryRegex = Pattern.compile(queryPattern, Pattern.CASE_INSENSITIVE);
	}

	public void setResponseCodePattern(String responseCodePattern)
	{
		this.responseCodePattern = responseCodePattern;
		responseCodeRegex = Pattern.compile(responseCodePattern, Pattern.CASE_INSENSITIVE);
	}
}
