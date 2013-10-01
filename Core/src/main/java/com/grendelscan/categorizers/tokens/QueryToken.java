/**
 * 
 */
package com.grendelscan.categorizers.tokens;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;

import com.grendelscan.scan.Scan;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * @author david
 *
 */
public class QueryToken implements Token
{

	private final List<NameValuePair> parameters;
	private final String uri;
	private final static ParameterSorter sorter = new ParameterSorter();
	
	private static class ParameterSorter implements Comparator<NameValuePair>
	{
		public ParameterSorter()
		{
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(NameValuePair o1, NameValuePair o2)
		{
			return o1.toString().compareTo(o2.toString());
		}
		
	}
	
	public QueryToken(String uri) throws URISyntaxException
	{
		parameters = URIStringUtils.getQueryParametersFromUri(uri);
		Collections.sort(parameters, sorter);
		this.uri = uri;
	}
	
	private String getQuerySummary(boolean removeForbiddenParameters)
	{
		String query = "";
		for (NameValuePair param : parameters)
		{
			if (removeForbiddenParameters && Scan.getInstance().isQueryParameterForbidden(param.getName()))
			{
				continue;
			}
			query += param.getName() + "&";

		}
		return query;
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
	 */
	@Override
	public String getTokenHash()
	{
		return StringUtils.md5Hash(uri + "?" + getQuerySummary(true));
	}

}
