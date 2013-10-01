package com.grendelscan.ui.proxy.interception;

import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableHttpHeader;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

public class StandardInterceptFilter extends InterceptFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardInterceptFilter.class);

    public static boolean matchesFilters(final List<InterceptFilter> filters, final StandardHttpTransaction transaction)
    {
        boolean match = false;

        for (InterceptFilter filter : filters)
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

    private final InterceptFilterLocation location;

    private final Pattern pattern;

    public StandardInterceptFilter(final InterceptFilterLocation location, final String patternString, final boolean matches, final boolean intercept)
    {
        super(matches, intercept);
        this.location = location;
        pattern = Pattern.compile(patternString, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    }

    private boolean checkHeaderName(final List<SerializableHttpHeader> headers)
    {
        for (Header header : headers)
        {
            if (simpleMatches(header.getName()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkHeaderValue(final List<SerializableHttpHeader> headers)
    {
        for (Header header : headers)
        {
            if (simpleMatches(header.getValue()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayText()
    {
        return pattern.pattern();
    }

    @Override
    public InterceptFilterLocation getLocation()
    {
        return location;
    }

    @SuppressWarnings("incomplete-switch")
    private boolean isHit(final StandardHttpTransaction transaction)
    {
        try
        {
            switch (location)
            {
                case METHOD:
                    return simpleMatches(transaction.getRequestWrapper().getMethod());
                case HOST:
                    return simpleMatches(transaction.getRequestWrapper().getHost());
                case SCHEME:
                    return simpleMatches(URIStringUtils.getScheme(transaction.getRequestWrapper().getURI()));
                case PATH:
                    return simpleMatches(transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI()));
                case QUERY:
                    return simpleMatches(URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()));
                case URL:
                    return simpleMatches(transaction.getRequestWrapper().getAbsoluteUriString());
                case REQUEST_BODY:
                    return simpleMatches(new String(transaction.getRequestWrapper().getBody(), StringUtils.getDefaultCharset()));
                case RESPONSE_MIME_TYPE:
                    return simpleMatches(transaction.getResponseWrapper().getHeaders().getMimeType());
                case RESPONSE_BODY:
                    return simpleMatches(new String(transaction.getResponseWrapper().getBody()));
                case RESPONSE_CODE:
                    return simpleMatches(String.valueOf(transaction.getResponseWrapper().getStatusLine().getStatusCode()));
                case COOKIE_HEADER:
                    return simpleMatches(transaction.getResponseWrapper().getHeaders().getFirstHeader("Cookie").getValue());
                case REQUEST_HEADER_NAME:
                    return checkHeaderName(transaction.getRequestWrapper().getHeaders().getReadOnlyHeaders());
                case REQUEST_HEADER_VALUE:
                    return checkHeaderValue(transaction.getRequestWrapper().getHeaders().getReadOnlyHeaders());
                case RESPONSE_HEADER_NAME:
                    return checkHeaderName(transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders());
                case RESPONSE_HEADER_VALUE:
                    return checkHeaderValue(transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders());
            }
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }
        return false;
    }

    @Override
    public boolean performAction(final StandardHttpTransaction transaction)
    {
        return isHit(transaction) == matches;
    }

    private boolean simpleMatches(final String string)
    {
        return pattern.matcher(string).find();
    }

}
