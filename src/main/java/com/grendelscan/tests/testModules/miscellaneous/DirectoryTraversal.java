package com.grendelscan.tests.testModules.miscellaneous;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.RequestOptions;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataTest;
import com.grendelscan.utils.URIStringUtils;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;

public class DirectoryTraversal extends TestModule implements ByRequestDataTest
{
	private String	antiPattern;
	private int		antiPatternThreshold;
	private String	patterns[];
	private int		patternThreshold;

	public DirectoryTraversal()
	{
		antiPattern = ConfigurationManager.getString("directory_traversal.antipattern");
		patterns = ConfigurationManager.getStringArray("directory_traversal.patterns");
		antiPatternThreshold = ConfigurationManager.getInt("directory_traversal.antipattern_threshold");
		patternThreshold = ConfigurationManager.getInt("directory_traversal.pattern_threshold");
	}
	
	@Override
	public String getDescription()
	{
		return "Tests for directory traversal vulnerabilities in query parameters. This is done " +
				"by inserting relative path strings. For example, if the default parameter value " +
				"is 'asdf', then '.asdf' is request. If this causes a different result page, " +
				"then './asdf', '.\\asdf', etc are requested. If they result in the same response " +
				"as the original request, then a directory traversal attack may be possible.";
	}

	@Override
	public String getExperimentalText()
	{

		return "While this module can discover directory traversal vulnerabilities, more testing " +
				"is required to reduce false positives and false negatives.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
	}


	@Override
	public String getName()
	{
		return "Directory traversal";
	}


	@Override
	public boolean isExperimental()
	{
		return true;
	}

	@Override
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);
		String oldValue = new String(DataUtils.getBytes(datum));
		/**
		 * We don't check response types later because we assume that if
		 * the original response is good, the rest probably will be too.
		 * Nothing bad will happen if they aren't, it's just wasted time.
		 */
		if (oldValue.equals(""))
		{
			return;
		}

		StandardHttpTransaction antitestTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		String antiValue = antiPattern.replace("%%value%%", oldValue);
		MutableData antiTestData = (MutableData) DataContainerUtils.resolveReferenceChain(
				antitestTransaction.getTransactionContainer(), datum.getReferenceChain());
		antiTestData.setBytes(antiValue.getBytes());
		RequestOptions testRequestOptions = requestOptions.clone();
		testRequestOptions.followRedirects = originalTransaction.hasRedirectResponse();
		antitestTransaction.setRequestOptions(testRequestOptions);
		try
		{
			antitestTransaction.execute();
			if (HttpResponseScoreUtils.scoreResponseMatch(originalTransaction, antitestTransaction,
					100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < antiPatternThreshold)
			{
				for (String pattern : patterns)
				{
					StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
					MutableData testData = (MutableData) DataContainerUtils.resolveReferenceChain(
							testTransaction.getTransactionContainer(), datum.getReferenceChain());
					String testValue = pattern.replace("%%value%%", oldValue);
					testData.setBytes(testValue.getBytes());
					testTransaction.setRequestOptions(testRequestOptions);
					try
					{
						testTransaction.execute();
					}
					catch (UnrequestableTransaction e)
					{
						Log.warn("Dir traversal request unrequestable (" + testTransaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
						continue;
					}
					if ((HttpResponseScoreUtils.scoreResponseMatch(originalTransaction, testTransaction,
							100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < patternThreshold) &&
							(HttpResponseScoreUtils.scoreResponseMatch(antitestTransaction, testTransaction,
									100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < antiPatternThreshold))
					{
						report(originalTransaction, testTransaction, antitestTransaction, datum.getReferenceChain().toString(), oldValue, antiValue,
								testValue);
						break;
					}

				}
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
		}
	}

	private void report(StandardHttpTransaction originalTransaction, StandardHttpTransaction testTransaction,
			StandardHttpTransaction antitestTransaction,
			String parameterName, String originalValue, String antiValue, String testValue)
	{
		originalTransaction.writeToDisk();
		testTransaction.writeToDisk();
		String title = "Directory traversal vulnerability";
		String shortDescription = "A possible directory traversal vulnerability was detected.";
		String longDescription =
				"A possible directory traversal vulnerability was detected in the \"" + parameterName +
						"\" parameter of "
						+ URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI())
						+ ". The original value " +
						"of the parameter was \"" + originalValue + "\". When the value of \"" + antiValue +
						"\" was used instead (" + antitestTransaction.getSavedUrl()+ "), it appears that a different page was returned, perhaps an " +
						"error message. When a value of \"" + testValue + "\" was used, it appears that " +
						"the response matched the original request. This implies that the parameter is " +
						"vulnerable to directory traversal attacks. However, this test may be prone to " +
						"false positives, so further investigation is recommended. The original transaction " +
						"can be viewed <a href=\"" + originalTransaction.getSavedUrl() + "\">here</a> and " +
						"the transaction used for testing can be viewed " +
						"<a href=\"" + testTransaction.getSavedUrl() + "\">here</a>.";
		String impact =
				"Depending on the function of the page, directory traversal attacks might be " +
						"used to read or execute arbitrary files already on the server.";
		String recomendations =
				"Confirm that this is a directory traversal vulnerability. If it is, at the very " +
						"least, modify the application to sanitize input, blocking any non-alphanumeric " +
						"characters. Ideally, a user should never be able to directly reference a file " +
						"by name in a query parameter.";
		String references = "";
		Finding event =
				new Finding(null, getName(), FindingSeverity.HIGH,
						URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI()), title,
						shortDescription, longDescription,
						impact, recomendations, references);
		Scan.getInstance().getFindings().addFinding(event);
	}


}
