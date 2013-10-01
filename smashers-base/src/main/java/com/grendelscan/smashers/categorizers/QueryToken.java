/**
 * 
 */
package com.grendelscan.smashers.categorizers;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.scan.Scan;

/**
 * @author david
 * 
 */
public class QueryToken implements Token
{

    private static class ParameterSorter implements Comparator<NameValuePair>
    {
        public ParameterSorter()
        {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final NameValuePair o1, final NameValuePair o2)
        {
            return o1.toString().compareTo(o2.toString());
        }

    }

    private final List<NameValuePair> parameters;
    private final String uri;

    private final static ParameterSorter sorter = new ParameterSorter();

    public QueryToken(final String uri) throws URISyntaxException
    {
        parameters = URIStringUtils.getQueryParametersFromUri(uri);
        Collections.sort(parameters, sorter);
        this.uri = uri;
    }

    private String getQuerySummary(final boolean removeForbiddenParameters)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
     */
    @Override
    public String getTokenHash()
    {
        return StringUtils.md5Hash(uri + "?" + getQuerySummary(true));
    }

}
