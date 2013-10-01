package com.grendelscan.commons.http.factories;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class NonScanClientFactory
{
    public static HttpClient getClient()
    {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.USER_AGENT, Scan.getScanSettings().getUserAgentString());
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        if (Scan.getScanSettings().getUseUpstreamProxy())
        {
            HttpHost proxy = new HttpHost(Scan.getScanSettings().getUpstreamProxyAddress(), Scan.getScanSettings().getUpstreamProxyPort());
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        HttpClient client = new DefaultHttpClient();

        return client;
    }
}
