/**
 * CustomSSLVerifier.java Created by: David Byrne Created on: Jan 30, 2008
 */
package com.grendelscan.commons.http.ssl;


import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.Scan;

public class CustomSSLVerifier implements X509HostnameVerifier
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSSLVerifier.class);
	private BrowserCompatHostnameVerifier verifier;

	public CustomSSLVerifier()
	{
		verifier = new BrowserCompatHostnameVerifier();
	}

	@Override
	public final boolean verify(String host, SSLSession session)
	{
		if (!verifier.verify(host, session))
		{
			String briefDescription =
			        "A problem was found with the SSL certificate for " + host + ":" + session.getPeerPort();
			String longDescription =
			        "The SSL certificate for " + host + ":" + session.getPeerPort() + " had an unknown error";
			String impact =
			        "Problems with SSL certificates can lead to man-in-the-middle attacks, browser errors and more.";
			String recomendation = "Use current certificates from recognized Certificate Authorities.";
			String references = "";
			Finding event =
			        new Finding(null, "SSL Certificate check", FindingSeverity.INFO, "https://" + host + ":"
			                + session.getPeerPort(), "SSL Certificate Error", briefDescription, longDescription,
			                impact, recomendation, references);
			Scan.getInstance().getFindings().addFinding(event);
		}

		return true;
	}

	@Override
	public final void verify(String host, SSLSocket ssl) 
	{
		try
		{
	        SSLSession session = ssl.getSession();
		}
		catch (Exception e)
		{
			int port = ssl.getPort();
			String name = "SSL-error-" + host + ":" + port;
			if (!Scan.getInstance().getTestData().containsItem(name))
			{
				String briefDescription = "A problem was found with the SSL certificate for " + host + ":" + port;
				String longDescription = "The SSL certificate for " + host + ":" + port + " had an error: " + e.toString();
				String impact =
				        "Problems with SSL certificates can lead to man-in-the-middle attacks, browser errors and more.";
				String recomendation = "Use current certificates from recognized Certificate Authorities.";
				String references = "";
				Finding event =
				        new Finding(null, "SSL Certificate check", FindingSeverity.INFO, "https://" + host + ":"
				                + port, "SSL Certificate Error", briefDescription, longDescription, impact, recomendation,
				                references);
				Scan.getInstance().getFindings().addFinding(event);
				Scan.getInstance().getTestData().setBoolean(name, true);
			}
		}
	}

	@Override
	public final void verify(String host, String[] cns, String[] subjectAlts)
	{
		try
		{
			verifier.verify(host, cns, subjectAlts);
		}
		catch (SSLException e)
		{
			String briefDescription = "A problem was found with the SSL certificate for " + host;
			String longDescription = "The SSL certificate for " + host + " had an error: " + e.toString();
			String impact =
			        "Problems with SSL certificates can lead to man-in-the-middle attacks, browser errors and more.";
			String recomendation = "Use current certificates from recognized Certificate Authorities.";
			String references = "";
			Finding event =
			        new Finding(null, "SSL Certificate check", FindingSeverity.INFO, "https://" + host,
			                "SSL Certificate Error", briefDescription, longDescription, impact, recomendation,
			                references);
			Scan.getInstance().getFindings().addFinding(event);
		}

	}

	public final void verify(String host, String[] cns, String[] subjectAlts, boolean strictWithSubDomains)
	   
	{
		try
		{
			verifier.verify(host, cns, subjectAlts, strictWithSubDomains);
		}
		catch (SSLException e)
		{
			String briefDescription = "A problem was found with the SSL certificate for " + host;
			String longDescription = "The SSL certificate for " + host + " had an error: " + e.toString();
			String impact =
			        "Problems with SSL certificates can lead to man-in-the-middle attacks, browser errors and more.";
			String recomendation = "Use current certificates from recognized Certificate Authorities.";
			String references = "";
			Finding event =
			        new Finding(null, "SSL Certificate check", FindingSeverity.INFO, "https://" + host,
			                "SSL Certificate Error", briefDescription, longDescription, impact, recomendation,
			                references);
			Scan.getInstance().getFindings().addFinding(event);
		}

	}

	@Override
	public final void verify(String host, X509Certificate cert) 
	{
		try
		{
			verifier.verify(host, cert);
		}
		catch (SSLException e)
		{
			String briefDescription = "A problem was found with the SSL certificate for " + host;
			String longDescription = "The SSL certificate for " + host + " had an error: " + e.toString();
			String impact =
			        "Problems with SSL certificates can lead to man-in-the-middle attacks, browser errors and more.";
			String recomendation = "Use current certificates from recognized Certificate Authorities.";
			String references = "";
			Finding event =
			        new Finding(null, "SSL Certificate check", FindingSeverity.INFO, "https://" + host,
			                "SSL Certificate Error", briefDescription, longDescription, impact, recomendation,
			                references);
			Scan.getInstance().getFindings().addFinding(event);
		}

	}

}
