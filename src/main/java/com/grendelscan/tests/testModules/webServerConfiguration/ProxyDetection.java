package com.grendelscan.tests.testModules.webServerConfiguration;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHostTest;

public class ProxyDetection extends TestModule implements ByHostTest
{
	public ProxyDetection()
	{
		requestOptions.ignoreRestrictions = true;
	}
	

	@Override
	public String getDescription()
	{
		return "Attempts to identify web servers that will proxy requests to other hosts. " +
				"This is most accurate if the proxy has access to Internet sites.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.WEB_SERVER_CONFIGURATION;
	}

	@Override
	public String getName()
	{
		return "Proxy detection";
	}

	@Override
	public boolean isExperimental()
	{
		return false;
	}

	@Override
	public void testByServer(int transactionID, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		try
		{
			if (testConnect(transaction, testJobId) || testInternet(transaction, testJobId) || testIntranet(transaction, testJobId))
			{
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.error(getName() + " request unrequestable (" + e.toString() + ")", e);
		}
	}

	private void report(String hostAndPort, String detectionMethod, StandardHttpTransaction transaction)
	{
		transaction.writeToDisk();
		String title = "Proxy server detected";
		String briefDescription = "A web server is also acting as a proxy server.";
		String longDescription =
				"The web server at " + hostAndPort + " is also acting as a proxy server. " + detectionMethod +
						"The transaction used for testing can be viewed " +
						"<a href=\"" + transaction.getSavedUrl() + "\">here</a>.";

		String impact =
				"An attacker might be able to access web servers through the proxy server that would otherwise be blocked "
						+
						"by a firewall. If the CONNECT method is supported, any TCP protocol can be tunneled.";
		String recomendations = "Disable proxying capabilities.";
		String references = "";

		Finding event = new Finding(null, getName(), FindingSeverity.LOW, hostAndPort,
				title, briefDescription, longDescription, impact, recomendations, references);
		Scan.getInstance().getFindings().addFinding(event);
	}

	private boolean testConnect(StandardHttpTransaction transaction, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		boolean proxy = false;

		StandardHttpTransaction testTransaction = new StandardHttpTransaction(TransactionSource.MISC_TEST, testJobId);
		testTransaction.getRequestWrapper().setMethod("CONNECT");
		testTransaction.getRequestWrapper().setURI("https://www.grendel-scan.com:443", false);
		testTransaction.getRequestWrapper().setNetworkHost(transaction.getRequestWrapper().getHost());
		testTransaction.getRequestWrapper().setNetworkPort(transaction.getRequestWrapper().getNetworkPort());
		testTransaction.setRequestOptions(requestOptions);
		testTransaction.execute();
		String detectionMethod = "";
		if (testTransaction.getResponseWrapper() != null) //some servers will just kill the connection
		{
			if(testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 200)
			{
				proxy = true;
				detectionMethod =
						"A CONNECT command was issued for \"www.grendel-scan.com:443\". The response code was 200, indicating "
								+
								"a successful connection. Note that this test is not very sophisticated right now, and should be manually confirmed.";
			}
			else if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 504)
			{
				proxy = true;
				detectionMethod =
						"A CONNECT command was issued for \"www.grendel-scan.com:443\". The response code was 504, indicating "
								+
								"a gateway timeout. This implies that the proxy server tried to connect to the host, but was unsuccessful.";
			}
			else if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 407)
			{
				proxy = true;
				detectionMethod =
						"A CONNECT command was issued for \"www.grendel-scan.com:443\". The response code was 407, which is a "
								+
								"request for proxy authentication. This implies that the server accepts proxy requests, but only with authentication "
								+
								"credentials.";
			}
		}
		if (proxy)
		{
			report(transaction.getRequestWrapper().getHost() + ":" + transaction.getRequestWrapper().getNetworkPort(),
					detectionMethod, testTransaction);
		}
		return proxy;
	}

	private boolean testInternet(StandardHttpTransaction transaction, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		boolean proxy = false;

		StandardHttpTransaction testTransaction = new StandardHttpTransaction(TransactionSource.MISC_TEST, testJobId);
		testTransaction.getRequestWrapper().setMethod("GET");
		testTransaction.getRequestWrapper().setURI("http://www.grendel-scan.com/proxy.txt", true);
		testTransaction.getRequestWrapper().setNetworkHost(transaction.getRequestWrapper().getHost());
		testTransaction.getRequestWrapper().setNetworkPort(transaction.getRequestWrapper().getNetworkPort());
		testTransaction.setRequestOptions(requestOptions);
		testTransaction.execute();

		String detectionMethod = "";
		if (testTransaction.getResponseWrapper() != null)
		{
			if ((new String(testTransaction.getResponseWrapper().getBody())).contains("grendel-scan proxy test"))
			{
				proxy = true;
				detectionMethod =
						"A request for http://www.grendel-scan.com/proxy.txt was sent to the server. The response contained the text "
								+
								"\"grendel-scan proxy test\", demonstrating a successful request.";
			}
			else if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 504)
			{
				proxy = true;
				detectionMethod =
						"A request for http://www.grendel-scan.com/proxy.txt was sent to the server. The response code was 504, indicating "
								+
								"a gateway timeout. This implies that the proxy server tried to connect to the host, but was unsuccessful.";
			}
			else if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 407)
			{
				proxy = true;
				detectionMethod =
						"A request for http://www.grendel-scan.com/proxy.txt was sent to the server. The response code was 407, which is a "
								+
								"request for proxy authentication. This implies that the server accepts proxy requests, but only with authentication "
								+
								"credentials.";
			}
		}
		if (proxy)
		{
			report(transaction.getRequestWrapper().getHost() + ":" + transaction.getRequestWrapper().getNetworkPort(),
					detectionMethod, testTransaction);
		}
		return proxy;
	}

	private boolean testIntranet(StandardHttpTransaction transaction, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		boolean proxy = false;

		StandardHttpTransaction testTransaction = new StandardHttpTransaction(TransactionSource.MISC_TEST, testJobId);
		testTransaction.getRequestWrapper().setMethod("GET");
		testTransaction.getRequestWrapper().setURI("http://randomhost.tld/", true);
		testTransaction.getRequestWrapper().setNetworkHost(transaction.getRequestWrapper().getHost());
		testTransaction.getRequestWrapper().setNetworkPort(transaction.getRequestWrapper().getNetworkPort());
		testTransaction.setRequestOptions(requestOptions);
		testTransaction.execute();
		String detectionMethod = "";
		if (testTransaction.getResponseWrapper() != null)
		{
			if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 504)
			{
				proxy = true;
				detectionMethod =
						"A request for http://randomhost.tld/ was sent to the server. The response code was 504, indicating "
								+
								"a gateway timeout. This implies that the proxy server tried to connect to the fictional host, but was unsuccessful.";
			}
			else if (testTransaction.getResponseWrapper().getStatusLine().getStatusCode() == 407)
			{
				proxy = true;
				detectionMethod =
						"A request for http://randomhost.tld/ was sent to the server. The response code was 407, which is a "
								+
								"request for proxy authentication. This implies that the server accepts proxy requests, but only with authentication "
								+
								"credentials.";
			}
		}
		if (proxy)
		{
			report(transaction.getRequestWrapper().getHost() + ":" + transaction.getRequestWrapper().getNetworkPort(),
					detectionMethod, testTransaction);
		}

		return proxy;
	}

}
