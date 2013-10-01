package com.grendelscan.requester.cobraIntegration;


import java.net.URL;
import java.security.Policy;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.cookie.Cookie;
import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.UserAgentContext;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;

/**
 * This is used by HttpTransaction and should not be used by
 * itself unless you really know what you are doing. It may
 * go away in the future.
 * 
 * @author David Byrne
 */
public class CobraUserAgent implements UserAgentContext
{
	private boolean enableScripting;
	private int transactionId;
	
	public CobraUserAgent(int transactionID, boolean enableScripting)
	{
		this.transactionId = transactionID;
		this.enableScripting = enableScripting;
	}

	@Override
	public HttpRequest createHttpRequest(int referingTransactionId)
	{
		HttpRequest request = new CobraHttpRequest(referingTransactionId, -1);
		return request;
	}

	@Override
	public String getAppCodeName()
	{
		return "Grendel-Scan";
	}

	@Override
	public String getAppMinorVersion()
	{
		return "0";
	}

	@Override
	public String getAppName()
	{
		return "Grendel-Scan";
	}

	@Override
	public String getAppVersion()
	{
		return "1";
	}

	@Override
	public String getBrowserLanguage()
	{
		return "English";
	}

	@Override
	public String getCookie(URL url)
	{
		List<Cookie> cookies = Scan.getInstance().getTransactionRecord().getTransaction(transactionId).getCookieJar().getMatchingCookies(url);
		StringBuilder sb = new StringBuilder();
		for(Cookie cookie: cookies)
		{
			sb.append(cookie.getName());
			sb.append("=");
			sb.append(cookie.getValue());
			sb.append("; ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public String getPlatform()
	{
		return System.getProperty("os.name") + " " + System.getProperty("os.version");
	}

	@Override
	public int getScriptingOptimizationLevel()
	{
		return 0;
	}

	@Override
	public Policy getSecurityPolicy()
	{
		return Policy.getPolicy();
	}

	@Override
	public String getUserAgent()
	{
		return Scan.getScanSettings().getUserAgentString();
	}

	@Override
	public boolean isCookieEnabled()
	{
		return true;
	}

	@Override
	public boolean isScriptingEnabled()
	{
		return enableScripting;
	}

	@Override
	public void setCookie(URL arg0, String arg1)
	{

	}

}
