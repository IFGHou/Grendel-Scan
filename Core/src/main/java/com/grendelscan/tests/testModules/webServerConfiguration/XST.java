package com.grendelscan.tests.testModules.webServerConfiguration;

import java.net.URISyntaxException;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHostTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.URIStringUtils;

public class XST extends TestModule implements ByHostTest
{
	@Override
	public String getDescription()
	{
		return "Tests for cross-site tracing (XST), which is an attack that can steal cookies using the " +
				"TRACE or TRACK debug method.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.WEB_SERVER_CONFIGURATION;
	}


	@Override
	public String getName()
	{
		return "XST";
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
			if (testMethod(transaction, "TRACE", testJobId) || testMethod(transaction, "TRACK", testJobId))
			{
				logFinding(transaction);
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.error(getName() + " request unrequestable (" + e.toString() + ")", e);
		}
	}

	private void logFinding(StandardHttpTransaction transaction)
	{
		String title = "HTTP debug method enabled";
		String briefDescription = "The TRACE/TRACK method was enabled.";
		String longDescription = "The TRACE/TRACK method was enabled on the servers listed below:<br><br>";

		String firstHost = "";
		transaction.writeToDisk();
		try
		{
			firstHost = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString());
			longDescription +=
				"&nbsp;&nbsp;&nbsp;&nbsp;"
						+ HtmlUtils.makeLink(transaction.getSavedUrl(),
								URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString())) + "<br>";
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}

		String impact = "An attack known as Cross Site Tracing (XST) leverages this method to steal cookies. " +
				"Once a session key in a cookie is obtained by an attacker, he can hijack a legitimate user’s session.";
		String recomendations =
				"Disable HTTP TRACE/TRACK on all web servers: <ul>"
						+
						"<li>IIS: http://www.microsoft.com/technet/prodtechnol/WindowsServer2003/Library/IIS/d779ee4e-5cd1-4159-b098-66c10c5a3314.mspx?mfr=true</li>"
						+
						"<li>Apache: http://httpd.apache.org/docs/2.0/mod/core.html#traceenable</li></ul>";
		String references = "http://www.owasp.org/index.php/Testing_for_HTTP_Methods_and_XST";

		Finding event = new Finding(null, getName(), FindingSeverity.LOW, firstHost,
				title, briefDescription, longDescription, impact, recomendations, references);
		Scan.getInstance().getFindings().addFinding(event);
	}

	private boolean testMethod(StandardHttpTransaction transaction, String method, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		StandardHttpTransaction test = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		test.getRequestWrapper().setMethod(method);
		String tokenName = TokenTesting.getInstance().generateToken();
		String tokenValue = TokenTesting.getInstance().generateToken();

		test.getRequestWrapper().getHeaders().addHeader(tokenName, tokenValue);
		test.setRequestOptions(requestOptions);
		test.execute();
		if ((new String(test.getResponseWrapper().getBody())).contains(tokenName + ": " + tokenValue))
		{
			test.writeToDisk();
			return true;
		}
		return false;
	}

}
