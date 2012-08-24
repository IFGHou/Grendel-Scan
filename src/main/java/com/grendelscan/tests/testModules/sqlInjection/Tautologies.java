package com.grendelscan.tests.testModules.sqlInjection;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.util.Arrays;

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
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.SQLInjection;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;

public class Tautologies extends TestModule implements ByRequestDataTest
{
	@Override
	public String getDescription()
	{
		return "Injects SQL tautologies (such as \"or 1=1 �\") and anti-tautologies " +
				"to test for SQL injection. If new pages are accessed through the " +
				"attack, they are processed for new testing and or spidering. The " +
				"test strings are defined in conf/sql_injection.conf.";
	}

	@Override
	public String getExperimentalText()
	{
		return "Although this module can find SQL injection vulnerabilities, the false-positive " +
				"rate can be high. More testing is required to tune the heuristics that identify " +
				"a vulnerable response.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SQL_INJECTION;
	}

	@Override
	public String getName()
	{
		return "SQL Tautologies";
	}


	@Override
	public boolean isExperimental()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.grendelscan.tests.testTypes.ByHttpQueryParameterTest#testByQueryParameter
	 * (int, com.grendelscan.requester.http.payloads.QueryParameter)
	 */
	@Override
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException
	{
		boolean binary = false;
		handlePause_isRunning();
		StandardHttpTransaction transaction =
				Scan.getInstance().getTransactionRecord()
						.getTransaction(transactionId);
		if (!MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			binary = true;
		}

		boolean hit = false;
		if ((new String(DataUtils.getBytes(datum))).matches("\\d+"))
		{
			try
			{
				hit = testTautologies(transaction, datum, SQLInjection.getNumericTautologies(), binary, testJobId);
				handlePause_isRunning();
				if (!hit)
				{
					hit = testTautologies(transaction, datum, SQLInjection.getStringTautologies(), binary, testJobId);
				}
			}
			catch (UnrequestableTransaction e)
			{
				Log.error(getName() + " request unrequestable (" + e.toString() + ")", e);
			}
		}

	}

	private void report(StandardHttpTransaction transaction, String parameterName,
			String tautology, StandardHttpTransaction tautologyTransaction,
			String antiTautology, StandardHttpTransaction antiTautologyTransaction)
	{
		tautologyTransaction.writeToDisk();
		antiTautologyTransaction.writeToDisk();
		String briefDescription = "SQL injection found with tautology";
		String longDescription =
				"A SQL tautology (something that is always true, such as 1=1) was injected into the \""
						+ parameterName
						+
						"\" parameter, and appears to have been successful. The tautology was (without the external quotes) \""
						+
						HtmlUtils.makeLink(tautologyTransaction.getSavedUrl(), tautology)
						+ "\" and the anti-tautology was \"" +
						HtmlUtils.makeLink(antiTautologyTransaction.getSavedUrl(), antiTautology) + "\".";

		Finding event =
				new Finding(null, getName(), FindingSeverity.HIGH, transaction.getRequestWrapper().getURI(),
						"SQL Injection Detected",
						briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(),
						SQLInjection.getSQLInjectionRecomendations(),
						SQLInjection.getSQLInjectionReferences());
		Scan.getInstance().getFindings().addFinding(event);
	}

	private boolean testTautologies(StandardHttpTransaction transaction, Data datum,
			Map<String, String> tautologies, boolean binary, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		boolean hit = false;
		for (String tautology : tautologies.keySet())
		{
			handlePause_isRunning();
			if (testTautologyPair(transaction, datum, tautology, tautologies.get(tautology), binary, testJobId))
			{
				hit = true;
				break;
			}
		}
		return hit;
	}

	private boolean testTautologyPair(StandardHttpTransaction transaction, Data datum, String tautology,
			String antiTautology, boolean binary, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		boolean hit = false;
		RequestOptions testRequestOptions = requestOptions.clone();
		testRequestOptions.followRedirects = transaction.getRedirectChildId() > 0;
		StandardHttpTransaction firstTautologyTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		MutableData firstTautologyData = (MutableData) DataContainerUtils.resolveReferenceChain(
				firstTautologyTransaction.getTransactionContainer(), datum.getReferenceChain());
		firstTautologyData.setBytes(ArrayUtils.addAll(DataUtils.getBytes(datum), tautology.getBytes()));
		firstTautologyTransaction.setRequestOptions(testRequestOptions);
		firstTautologyTransaction.execute();

		handlePause_isRunning();
		// Run an identical test to see if they differ (some page elements may
		// be random). Use the difference score as
		// the basis for the taut / anti-taut score
		StandardHttpTransaction secondTautologyTransaction = firstTautologyTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		secondTautologyTransaction.execute();

		handlePause_isRunning();
		
		// float tautThreshold =
		// StringUtils.scoreStringDifferenceIgnoreCase(firstTautologyTransaction.getStrippedResponseText(),
		// secondTautologyTransaction.getStrippedResponseText(), 100) *
		// SQLInjection.getTautologyThreshold();
		double tautThreshold =
				HttpResponseScoreUtils.scoreResponseMatch(firstTautologyTransaction, secondTautologyTransaction,
						100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) * SQLInjection.getTautologyThreshold();

		StandardHttpTransaction antiTautologyTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		
		Data antiTautologyData = DataContainerUtils.resolveReferenceChain(
				antiTautologyTransaction.getTransactionContainer(), datum.getReferenceChain());
		firstTautologyData.setBytes(ArrayUtils.addAll(DataUtils.getBytes(datum), antiTautology.getBytes()));

		antiTautologyTransaction.setRequestOptions(testRequestOptions);
		antiTautologyTransaction.execute();

		handlePause_isRunning();
		int score;
		if (binary)
		{
			if (Arrays.areEqual(firstTautologyTransaction.getResponseWrapper().getBody(),
					(antiTautologyTransaction.getResponseWrapper().getBody())))
			{
				score = 100;
			}
			else
			{
				score = 0;
			}
		}
		else
		{
			// score =
			// StringUtils.scoreStringDifferenceIgnoreCase(firstTautologyTransaction.getStrippedResponseText(),
			// antiTautologyTransaction.getStrippedResponseText(), 100);
			score = HttpResponseScoreUtils.scoreResponseMatch(firstTautologyTransaction, antiTautologyTransaction,
					100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects);
		}
		if (score < tautThreshold)
		{
			hit = true;
			// If the attack was successful, have it tested. It may lead to
			// other pages.
			Scan.getInstance().getCategorizerQueue().addTransaction(secondTautologyTransaction);
			report(transaction, datum.getReferenceChain().toString(), tautology, firstTautologyTransaction, antiTautology,
					antiTautologyTransaction);
		}

		return hit;
	}


}
