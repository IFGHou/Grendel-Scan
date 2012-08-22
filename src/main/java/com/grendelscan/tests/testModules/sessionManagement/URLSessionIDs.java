package com.grendelscan.tests.testModules.sessionManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.SessionIDTesting.SessionID;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByQueryNamedDataTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.collections.CollectionUtils;

public class URLSessionIDs extends TestModule implements ByQueryNamedDataTest
{

	// private Set<String> badUrls;
	private DatabaseBackedList<String>	discoveredURLSessionIDs;
	private int							maxSessionIDRequests;
	private int							minAcceptableBitLength;

	public URLSessionIDs()
	{
		discoveredURLSessionIDs = new DatabaseBackedList<String>("discovered-url-session-ids");
		maxSessionIDRequests = SessionID.getInstance().getMaxSessionIDRequests();
		minAcceptableBitLength = SessionID.getInstance().getMinAcceptableBitLength();
	}
	
	@Override
	public String getDescription()
	{
		return "Searches responses for session IDs placed in URLs (which is bad). "
				+ "If one is found, it is tested for key strength based on net entropy, repeated values "
				+ "and overall key length.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SESSION_MANAGEMENT;
	}

	@Override
	public String getName()
	{
		return "URL Session IDs";
	}

	@Override
	public boolean isExperimental()
	{
		return false;
	}

	@Override
	public void testByQueryNamedData(int transactionId, NamedDataContainer datum, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);
		if (datum.isDataAncestor(transaction.getTransactionContainer().getUrlQueryDataContainer()))
		{
			if (SessionID.getInstance().stringContainsURLSessionID(new String(DataUtils.getBytes(datum.getNameData()))) && (transaction.getRefererId() > 0))
			{
				testTransaction(Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRefererId()), testJobId);
			}
		}

	}

	private void checkSessionIDEntropy(StandardHttpTransaction transactions[], String sessionIDName,
			List<String> testSessionIDValues)
	{
		int bitChanges;
		if ((bitChanges = SessionID.getInstance().getSessionIDBitEntropy(testSessionIDValues)) >= 0)
		{

			String longDescription =
					"The parameter named " + sessionIDName + " appears to be used to track session state. "
							+ "Approximately " + String.valueOf(bitChanges)
							+ " bits of random data were observed. A minimum of " + minAcceptableBitLength
							+ " is generally recomended. <br><br>" +
							getTransactionLinks(transactions);

			String recomendations =
					"Assure that session IDs contain at least " + minAcceptableBitLength + " bits of random data.";
			reportWeakSessionID(transactions[0], testSessionIDValues, "Similar session IDs detected",
					longDescription, recomendations, FindingSeverity.MEDIUM);
		}
	}

	private void findDuplicateCookies(StandardHttpTransaction transactions[], String sessionIDName,
			List<String> testCookiesValues)
	{
		if (SessionID.getInstance().findDuplicateSessionIDs(testCookiesValues))
		{
			String longDescription =
					"The parameter named " + sessionIDName + " appears to be used to track session state. "
							+ "However, some duplicate values were detected. If " + sessionIDName + " is used"
							+ "to track session state, all values supplied by the web server should be unique. "
							+ "Detection of duplicate values implies that the generation algorithm outputs "
							+ "predictably named session IDs. <br><br>" +
							getTransactionLinks(transactions);

			String recomendations =
					"Assure that sessionIDs contain at least " + minAcceptableBitLength + " bits of random data.";
			reportWeakSessionID(transactions[0], testCookiesValues, "Duplicate session IDs detected",
					longDescription, recomendations, FindingSeverity.MEDIUM);
		}
	}

	private StandardHttpTransaction[] getMoreTransactions(StandardHttpTransaction transaction, int testJobId)
			throws UnrequestableTransaction, InterruptedScanException
	{
		StandardHttpTransaction[] transactions = new StandardHttpTransaction[maxSessionIDRequests];
		transactions[0] = transaction;
		for (int index = 1; index <= maxSessionIDRequests; index++)
		{
			StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
			testTransaction.setRequestOptions(requestOptions);
			testTransaction.execute();
			transactions[index] = testTransaction;
			if (index == 1)
			{
				try
				{
					/*
					 * Pauses to let the second counter increment on the web
					 * server. This is because a poorly written app may generate
					 * identical session ids in a short time period. We'll pause
					 * for the first request, but subsequent requests will be
					 * executed quickly to try and catch a duplicate value.
					 */
					Thread.sleep(1500);
				}
				catch (InterruptedException e)
				{
					break;
					// Probably due to a stop, which will be handled elsewhere
				}
			}
		}
		return transactions;
	}

	private String getReferences()
	{
		return HtmlUtils.makeLink("http://www.owasp.org/index.php/Session_Management");
	}

	private Map<String, List<String>> getSessionIDs(StandardHttpTransaction transactions[])
	{

		Map<String, List<String>> sessionIDValues = new HashMap<String, List<String>>();
		for (StandardHttpTransaction transaction : transactions)
		{
			SessionID.getInstance().findUrlSessionIDsAndValues(new String(transaction.getResponseWrapper().getBody()),
					sessionIDValues);
		}

		return sessionIDValues;
	}

	private String getTransactionLinks(StandardHttpTransaction[] testTransactions)
	{
		String links = "The following transactions were used for the test:<br>";
		for (StandardHttpTransaction transaction : testTransactions)
		{
			transaction.writeToDisk();
			links +=
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId()))
							+ "<br>\n";
		}

		return links;
	}

	private void reportUrlSessionID(StandardHttpTransaction originalTransaction, String sessionIDName)
	{
		String title = "Session ID stored in HTTP query parameter";
		String longDescription =
				"The parameter named \""
						+ sessionIDName
						+ "\" appears to be used to track session state. "
						+ "However, if the session ID is stored in an HTTP query, it can be obtained from proxy server access logs, "
						+ "browser history records, and other locations that may not be properly secured for session ID storage. <br>The "
						+ "parameter was set in the body of " + originalTransaction.getRequestWrapper().getURI();
		String impact = "Session hijacking using secondary vulnerability. ";
		String recomendations = "Use cookieJar to track session IDs instead of query parameters";
		String shortDescription = "The session ID may be stored stored in an HTTP query parameter.";
		Finding event =
				new Finding(null, getName(), FindingSeverity.MEDIUM, originalTransaction.getRequestWrapper().getURI(),
						title,
						shortDescription, longDescription, impact, recomendations, getReferences());
		Scan.getInstance().getFindings().addFinding(event);
	}

	private void reportWeakSessionID(StandardHttpTransaction originalTransaction, List<String> testCookiesValues,
			String title, String longDescription, String recomendations, FindingSeverity severity)
	{
		longDescription += "<br><br>The following sessionIDs were received during testing:<br>";
		String impact =
				"If the session ID is generated in a predictable manner, an attacker could hijack legitimate sessions "
						+ "by guessing the session IDs of authenticated users.";
		for (String value : testCookiesValues)
		{
			longDescription += "<tab>" + value + "<br>";
		}
		Finding event =
				new Finding(null, getName(), severity, originalTransaction.getRequestWrapper().getURI(), title,
						"A problem was discovered with session ID values.", longDescription, impact, recomendations,
						getReferences());
		Scan.getInstance().getFindings().addFinding(event);
	}

	/**
	 * The idea is that we don't need to test the sessionID for strength if it
	 * wasn't newly assigned
	 * 
	 * @param transaction
	 * @param sessionID
	 * @return
	 */
	private boolean requestContainsID(StandardHttpTransaction transaction, String sessionID)
	{

		return SessionID.getInstance().transactionRequestContainsCookie(transaction, sessionID)
				|| CollectionUtils
						.containsStringIgnoreCase(HttpUtils.getAllQueryParameterNames(transaction), sessionID);
	}

	private void testSessionIDs(StandardHttpTransaction transactions[])
	{
		Map<String, List<String>> sessionIDValues = getSessionIDs(transactions);
		for (String sessionIDName : sessionIDValues.keySet())
		{
			if (SessionID.getInstance().getTestedSessionIDs().contains(sessionIDName)
					|| requestContainsID(transactions[0], sessionIDName))
//					|| Scan.getInstance().isQueryParameterIgnored(sessionIDName))
			{
				continue;
			}
			SessionID.getInstance().getTestedSessionIDs().add(sessionIDName);
			findDuplicateCookies(transactions, sessionIDName, sessionIDValues.get(sessionIDName));
			checkSessionIDEntropy(transactions, sessionIDName, sessionIDValues.get(sessionIDName));
		}
	}

	private void testTransaction(StandardHttpTransaction transaction, int testJobId) throws InterruptedScanException
	{
		// badUrls.add(transaction.getRequestWrapper().getURI());
		StandardHttpTransaction transactions[];
		try
		{
			transactions = getMoreTransactions(transaction, testJobId);
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn("URL session ID request unrequestable: " + e.toString());
			return;
		}
		testSessionIDs(transactions);

		for (String sessionIDName : SessionID.getInstance().findUrlSessionIDs(new String(transaction.getResponseWrapper().getBody())))
		{
			handlePause_isRunning();
			if (discoveredURLSessionIDs.contains(sessionIDName))
			{
				continue;
			}
			discoveredURLSessionIDs.add(sessionIDName);
			reportUrlSessionID(transaction, sessionIDName);
		}
	}



}
