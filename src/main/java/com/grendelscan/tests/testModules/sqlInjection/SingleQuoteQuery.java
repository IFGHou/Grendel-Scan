package com.grendelscan.tests.testModules.sqlInjection;

import org.apache.commons.lang.ArrayUtils;

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
import com.grendelscan.utils.URIStringUtils;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;

public class SingleQuoteQuery extends TestModule implements ByRequestDataTest
{

	@Override
	public String getDescription()
	{
		return "Appends a single quote onto a parameter and looks for a SQL " +
				"error message in the response. The SQL error patterns are " +
				"defined in conf/sql_injection.conf. Currently, errors from " +
				"IBM DB2, Microsoft Access, Microsoft SQL Server, MySQL, " +
				"Oracle, PostgreSQL, and generic ODBC & OLE/DB errors are " +
				"detected.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SQL_INJECTION;
	}

	@Override
	public String getName()
	{
		return "Error-based SQL Injection";
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.grendelscan.tests.testTypes.ByHttpQueryParameterTest#testByQueryParameter
	 * (com.grendelscan.requester.http.payloads.QueryParameter)
	 */
	@Override
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException
	{
		handlePause_isRunning();
		StandardHttpTransaction transaction =
				Scan.getInstance().getTransactionRecord()
						.getTransaction(transactionId);
		RequestOptions testRequestOptions = requestOptions.clone();
		testRequestOptions.followRedirects = transaction.getRedirectChildId() > 0;
		handlePause_isRunning();
		
		try
		{
			StandardHttpTransaction oneQuoteTransaction = runTestTransaction(transaction, datum, "'", testJobId);
			
			if (MimeUtils.isWebTextMimeType(oneQuoteTransaction.getResponseWrapper().getHeaders().getMimeType()))
			{
				String bodyText = oneQuoteTransaction.getResponseWrapper().getStrippedResponseText();
				bodyText = bodyText.replaceAll("\\s++", " ");
				String platform = SQLInjection.findSQLErrorMessages(bodyText);
				if (platform != null)
				{
					recordErrorMessageFinding(oneQuoteTransaction, datum.getReferenceChain().toString(), platform);
					return;
				}
			}
			
			StandardHttpTransaction twoQuoteTransaction = runTestTransaction(transaction, datum, "''", testJobId);
			int score = HttpResponseScoreUtils.scoreResponseMatch(oneQuoteTransaction, twoQuoteTransaction,
					100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects);
			
			if (score < 80)
			{
				recordQuoteFinding(oneQuoteTransaction, twoQuoteTransaction, datum.getReferenceChain().toString());
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.error(getName() + " request unrequestable (" + e.toString() + ")", e);
			return;
		}
	}
	
	private StandardHttpTransaction runTestTransaction(StandardHttpTransaction transaction, MutableData queryDatum, String attack, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		MutableData testTransactionQueryDatum = (MutableData) DataContainerUtils.resolveReferenceChain(
				testTransaction.getTransactionContainer(), queryDatum.getReferenceChain());
		
		testTransactionQueryDatum.setBytes(ArrayUtils.addAll(DataUtils.getBytes(queryDatum), attack.getBytes()));
		testTransaction.setRequestOptions(requestOptions);
		testTransaction.execute();
		return testTransaction;
	}

	private void recordErrorMessageFinding(StandardHttpTransaction transaction, String parameterName, String platform)
	{
		transaction.writeToDisk();
		String briefDescription = "A SQL error message was detected";
		String longDescription =
				"When a single quote (') was appended to the parameter listed below, a SQL " +
						"error message was returned. This could indicate a SQL injection vulnerability.<br>\n<br>\n" +
						"URL: " + URIStringUtils.getFileUri(transaction.getRequestWrapper().getURI()) + "<br>\n" +
						"Parameter name: " + parameterName + "<br>\n" +
						"Platform: " + platform + "<br>\n" +
						"Transaction: "
						+ HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId())) + "<br>\n"
						+
						"<br>\n";
		Finding event = new Finding(null, getName(), FindingSeverity.HIGH, "See description",
				"Possible SQL Injection", briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(),
				SQLInjection.getSQLInjectionRecomendations(), SQLInjection.getSQLInjectionReferences());
		Scan.getInstance().getFindings().addFinding(event);
	}

	private void recordQuoteFinding(StandardHttpTransaction oneQuoteTransaction, StandardHttpTransaction twoQuoteTransaction, String parameterName)
	{
		oneQuoteTransaction.writeToDisk();
		twoQuoteTransaction.writeToDisk();
		String briefDescription = "Possible SQL injection was detected";
		String longDescription =
				"A significant difference was noted between a single quote (') being appended to the parameter listed below, " +
				"and two single quotes being appended. This could indicate a SQL injection vulnerability.<br>\n<br>\n" +
						"URL: " + URIStringUtils.getFileUri(oneQuoteTransaction.getRequestWrapper().getURI()) + "<br>\n" +
						"Parameter name: " + parameterName + "<br>\n" +
						"Transactions: "
						+ HtmlUtils.makeLink(oneQuoteTransaction.getSavedUrl(), String.valueOf(oneQuoteTransaction.getId())) + "<br>\n"
						+ HtmlUtils.makeLink(twoQuoteTransaction.getSavedUrl(), String.valueOf(twoQuoteTransaction.getId())) + "<br>\n"
						+
						"<br>\n";
		Finding event = new Finding(null, getName(), FindingSeverity.HIGH, "See description",
				"Possible SQL Injection", briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(),
				SQLInjection.getSQLInjectionRecomendations(), SQLInjection.getSQLInjectionReferences());
		Scan.getInstance().getFindings().addFinding(event);
	}


}
