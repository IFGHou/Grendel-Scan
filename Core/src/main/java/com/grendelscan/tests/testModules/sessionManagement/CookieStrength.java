package com.grendelscan.tests.testModules.sessionManagement;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.cookie.Cookie;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.SessionIDTesting.SessionID;
import com.grendelscan.tests.libraries.SessionIDTesting.SessionIDLocation;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.BySetCookieTest;
import com.grendelscan.tests.testTypes.InitialAuthenticationTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.collections.CollectionUtils;

public class CookieStrength extends TestModule implements BySetCookieTest, InitialAuthenticationTest
{
	private int	maxCookieRequests;
	private int	minAcceptableBitLength;

	public CookieStrength()
	{
		maxCookieRequests = SessionID.getInstance().getMaxSessionIDRequests();
		minAcceptableBitLength = SessionID.getInstance().getMinAcceptableBitLength();
	}
	
	@Override
	public String getDescription()
	{
		return "Checks cookies for weak session keys based on net entropy, "
				+ "repeated values and overall key length.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SESSION_MANAGEMENT;
	}


	@Override
	public String getName()
	{
		return "Cookie strength ";
	}

	public String[] getResponseHeaders()
	{
		String headers[] = { "Set-Cookie" };
		return headers;
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}

	@Override
	public void testBySetCookie(int transactionID, Cookie cookie, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		testTransaction(transaction, cookie, testJobId);
	}

	@Override
	public void testInitialAuthentication(int transactionID, SessionIDLocation sessionIDLocation, int testJobId) throws InterruptedScanException
	{
		if (sessionIDLocation == SessionIDLocation.COOKIE_SESSION_ID)
		{
			StandardHttpTransaction transaction =
					Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
			for (Cookie cookie : HttpUtils.getSetCookies(transaction))
			{
				testTransaction(transaction, cookie, testJobId);
			}
		}
	}

	private void checkCookieEntropy(StandardHttpTransaction originalTransaction, String cookieName,
			String[] testCookiesValues, StandardHttpTransaction[] testTransactions)
	{

		int bitChanges;
		if ((bitChanges = SessionID.getInstance().getSessionIDBitEntropy(testCookiesValues)) >= 0)
		{

			String longDescription =
					"The cookie named " + cookieName + " appears to be used to track session state. "
							+ "Approximately " + String.valueOf(bitChanges)
							+ " bits of random data were observed. A minimum of " + minAcceptableBitLength
							+ " is generally recommended.<br><br>";
			longDescription += getTransactionLinks(testTransactions);

			String recomendations =
					"Assure that session ID contain at least " + minAcceptableBitLength + " bits of random data.";
			String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Session_Management");
			report(originalTransaction, testCookiesValues, "Similar session IDs detected", longDescription,
					recomendations, references, FindingSeverity.MEDIUM);
		}
	}

	private void findDuplicateCookies(StandardHttpTransaction originalTransaction, String cookieName,
			String[] testCookiesValues, StandardHttpTransaction[] testTransactions)
	{

		if (SessionID.getInstance().findDuplicateSessionIDs(testCookiesValues))
		{
			String longDescription =
					"The cookie named " + cookieName + " appears to be used to track session state. "
							+ "However, some duplicate cookie values were detected. If " + cookieName + " is used"
							+ "to track session state, all values supplied by the web server should be unique. "
							+ "Detection of duplicate values implies that the generation algorithm outputs "
							+ "predictably named cookieJar. <br><br>";
			longDescription += getTransactionLinks(testTransactions);
			String recomendations =
					"Assure that cookieJar contain at least " + minAcceptableBitLength + " bits of random data.";
			String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Session_Management");
			report(originalTransaction, testCookiesValues, "Duplicate session IDs detected", longDescription,
					recomendations, references, FindingSeverity.MEDIUM);
		}
	}

	private String[] getCookieValues(String cookieName, StandardHttpTransaction[] testTransactions) throws InterruptedScanException
	{
		String[] testCookiesValues = new String[maxCookieRequests + 1];
		int index = 0;
		for (Cookie cookie : SessionID.getInstance().getSetCookiesByName(testTransactions, cookieName))
		{
			if (cookie == null)
			{
				Log.error("Cookie not found where one expected.", new Throwable());
				continue;
			}
			testCookiesValues[index++] = cookie.getValue();
		}

		return testCookiesValues;
	}

	private String getTransactionLinks(StandardHttpTransaction[] testTransactions)
	{
		String links = "The following transactions were used for the test:<br>";
		for (StandardHttpTransaction transaction : testTransactions)
		{
			transaction.writeToDisk();
			links += HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId())) + "<br>\n";
		}

		return links;
	}

	private void report(StandardHttpTransaction originalTransaction, String[] testCookiesValues, String title,
			String longDescription, String recomendations, String references, FindingSeverity severity)
	{
		longDescription += "<br><br>The following cookies were received during testing:<br>\n";
		String impact =
				"If the session ID is session ID is generated in a predictable manner, an attacker could hijack legitimate sessions "
						+ "by guessing the session IDs of authenticated users.";
		for (String value : testCookiesValues)
		{
			longDescription += "<tab>" + value + "<br/>\n";
		}
		Finding event =
				new Finding(null, getName(), severity, originalTransaction.getRequestWrapper().getURI(), title,
						"A problem was discovered with cookie values.", longDescription, impact, recomendations,
						references);
		Scan.getInstance().getFindings().addFinding(event);
	}

	private void testCookies(StandardHttpTransaction originalTransaction, String cookieName,
			StandardHttpTransaction[] testTransactions) throws InterruptedScanException
	{
		String[] testCookiesValues = getCookieValues(cookieName, testTransactions);
		findDuplicateCookies(originalTransaction, cookieName, testCookiesValues, testTransactions);
		checkCookieEntropy(originalTransaction, cookieName, testCookiesValues, testTransactions);
	}

	private void testTransaction(StandardHttpTransaction transaction, Cookie cookie, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction[] testTransactions = null;
		
		Set<String> oldCookies = new HashSet<String>(transaction.getUsedCookieNames());
		for (NameValuePairDataContainer parameter : DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
		{
			oldCookies.add(new String(DataUtils.getBytes(parameter.getNameData())));
		}

		handlePause_isRunning();
		/*
		 * If this cookie was supplied by the initial transaction in a
		 * header, or as a URL parameter, or if it's already been tested in
		 * another transaction, don't test it.
		 */
		synchronized (SessionID.getInstance().getTestedSessionIDs())
		{
			if (CollectionUtils.containsStringIgnoreCase(oldCookies, cookie.getName())
					|| SessionID.getInstance().getTestedSessionIDs().contains(cookie.getName()))
			{
				return;
			}

			SessionID.getInstance().getTestedSessionIDs().add(cookie.getName());
		}

		testTransactions =
				SessionID.getInstance().getSampleCookieTransactions(transaction, cookie, getName(), testJobId);
		if (testTransactions == null)
		{
			Log.debug(cookie.getName() + " doesn't seem to be a session ID");
			return;
		}

		Log.debug(getName() + " is testing the cookie \"" + cookie.getName() + "\"");

		handlePause_isRunning();
		if (SessionID.getInstance().isKnownSessionID(cookie.getName()))
		{
			testCookies(transaction, cookie.getName(), testTransactions);
		}
	}

}
