package com.grendelscan.GUI.proxy.interception;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableHttpHeader;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;
public class StandardInterceptFilter extends InterceptFilter
{
	private InterceptFilterLocation location;
	@Override
    public InterceptFilterLocation getLocation()
    {
    	return location;
    }

	@Override
    public String getDisplayText()
    {
    	return pattern.pattern();
    }


	private Pattern pattern;
	public StandardInterceptFilter(InterceptFilterLocation location, String patternString, boolean matches,
            boolean intercept)
    {
		super(matches, intercept);
	    this.location = location;
	    pattern = Pattern.compile(patternString, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    }
	
	@Override
    public boolean performAction(StandardHttpTransaction transaction)
	{
		return isHit(transaction) == matches;
	}
	
	@SuppressWarnings("incomplete-switch")
    private boolean isHit(StandardHttpTransaction transaction)
	{
		try
		{
			switch(location)
			{
				case METHOD: return simpleMatches(transaction.getRequestWrapper().getMethod());
				case HOST: return simpleMatches(transaction.getRequestWrapper().getHost());
				case SCHEME: return simpleMatches(URIStringUtils.getScheme(transaction.getRequestWrapper().getURI()));
				case PATH: return simpleMatches(transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI()));
				case QUERY: return simpleMatches(URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()));
				case URL: return simpleMatches(transaction.getRequestWrapper().getAbsoluteUriString());
				case REQUEST_BODY: return simpleMatches(new String(transaction.getRequestWrapper().getBody(), StringUtils.getDefaultCharset()));
				case RESPONSE_MIME_TYPE: return simpleMatches(transaction.getResponseWrapper().getHeaders().getMimeType());
				case RESPONSE_BODY: return simpleMatches(new String(transaction.getResponseWrapper().getBody()));
				case RESPONSE_CODE: return simpleMatches(String.valueOf(transaction.getResponseWrapper().getStatusLine().getStatusCode()));
				case COOKIE_HEADER: return simpleMatches(transaction.getResponseWrapper().getHeaders().getFirstHeader("Cookie").getValue());
				case REQUEST_HEADER_NAME: return checkHeaderName(transaction.getRequestWrapper().getHeaders().getReadOnlyHeaders()); 
				case REQUEST_HEADER_VALUE: return checkHeaderValue(transaction.getRequestWrapper().getHeaders().getReadOnlyHeaders());
				case RESPONSE_HEADER_NAME: return checkHeaderName(transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders()); 
				case RESPONSE_HEADER_VALUE: return checkHeaderValue(transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders());
			}
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		return false;
	}
	
	private boolean checkHeaderName(List<SerializableHttpHeader> headers)
	{
		for(Header header: headers)
		{
			if (simpleMatches(header.getName()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean checkHeaderValue(List<SerializableHttpHeader> headers)
	{
		for(Header header: headers)
		{
			if (simpleMatches(header.getValue()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean simpleMatches(String string)
	{
		return pattern.matcher(string).find();
	}


	public static boolean matchesFilters(List<InterceptFilter> filters, StandardHttpTransaction transaction)
	{
		boolean match = false;
		
		for(InterceptFilter filter: filters)
		{
			if (filter.performAction(transaction))
			{
				if (filter.isIntercept())
				{
					match = true;
				}
				else
				{
					match = false;
					break;
				}
			}
			else if (filter.isIntercept())
			{
				match = false;
				break;
			}
		}
		return match;	
	}

	
}
