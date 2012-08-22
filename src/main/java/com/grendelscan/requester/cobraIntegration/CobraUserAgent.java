package com.grendelscan.requester.cobraIntegration;


import java.net.URL;
import java.security.Policy;

import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.UserAgentContext;

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
	
	public CobraUserAgent(boolean enableScripting)
	{
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
		return "huh?";
	}

	@Override
	public String getAppMinorVersion()
	{
		return "1";
	}

	@Override
	public String getAppName()
	{
		return "This is Grendel";
	}

	@Override
	public String getAppVersion()
	{
		return "0";
	}

	@Override
	public String getBrowserLanguage()
	{
		return "English";
	}

	@Override
	public String getCookie(URL arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlatform()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getScriptingOptimizationLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Policy getSecurityPolicy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserAgent()
	{
		return Scan.getScanSettings().getUserAgentString();
	}

	@Override
	public boolean isCookieEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isScriptingEnabled()
	{
		// TODO Auto-generated method stub
		return enableScripting;
	}

	@Override
	public void setCookie(URL arg0, String arg1)
	{
		// TODO Auto-generated method stub

	}

}
