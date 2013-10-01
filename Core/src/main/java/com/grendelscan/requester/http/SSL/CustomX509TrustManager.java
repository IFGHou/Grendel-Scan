package com.grendelscan.requester.http.SSL;


import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomX509TrustManager implements X509TrustManager
{
//	private TrustManager[] trustManagers;

	public CustomX509TrustManager()
	{
//		this.scan = scan;
//		trustManagers = createTrustManagers(keystore);

	}

//	private TrustManager[] createTrustManagers(final KeyStore keystore) 
//	{
//		if (keystore == null)
//		{
//			throw new IllegalArgumentException("Keystore may not be null");
//		}
//		TrustManagerFactory tmfactory = null;
//		try
//		{
//			tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//			tmfactory.init(keystore);
//		}
//		catch (GeneralSecurityException e) 
//		{
//			Debug.errDebug("Fatal problem with SSL: " + e.toString(), e);
//			System.exit(1);
//		}
//		return tmfactory.getTrustManagers();
//	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
	{
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
	{
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
//		for (TrustManager tm: trustManagers)
//		{
//			if (tm instanceof CustomX509TrustManager)
//			{
//				CustomX509TrustManager xtm = (CustomX509TrustManager) tm;
//				return xtm.getAcceptedIssuers();
//			}
//		}
		return null;
	}
}