package com.grendelscan.tests.testModules.miscellaneous;

import java.util.Collection;
import java.util.List;

import org.apache.http.Header;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableHttpHeader;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.HttpHeaderContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextTypeUtils;
import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRepeatableOutputContextTest;
import com.grendelscan.utils.HtmlUtils;

public class CRLFInjection extends TestModule implements ByRepeatableOutputContextTest
{
	@Override
	public String getDescription()
	{
		return "Tests for CRLF injection into HTTP headers from HTTP query "
				+ "parameters. If the destination is a \"Location\" header "
				+ "(which is most common), redirection to an arbitrary site " + "will also be tested.";
	}

	@Override
	public TokenContextType[] getDesiredRepeatableContexts()
	{
		return TokenContextTypeUtils.getHttpHeaderContexts();
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
	}


	@Override
	public String getName()
	{
		return "CRLF Injection";
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}

	@Override
	public void testByRepeatableOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException
	{
		handlePause_isRunning();
		try
		{
			HttpHeaderContext headerContext = (HttpHeaderContext) contexts.toArray(new TokenContext[0])[0];
			if (testCRLFInjection(headerContext, testJobId))
			{
				return;
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
		}


		for (TokenContext context: contexts)
		{
			HttpHeaderContext headerContext = (HttpHeaderContext) context;
			if (headerContext.getContextHeader().getName().equalsIgnoreCase("location"))
			{
				testLocation(headerContext, testJobId);
				return;
			}
		}
	}

	/**
	 * 
	 * @param transactions
	 * @return 0 for no match, 1 for a found header name, 2 for a found header
	 *         name and value
	 */
	private int checkSuccess(StandardHttpTransaction transactions[], String headerName, String headerValue)
	{
		List<SerializableHttpHeader> headers = transactions[0].getResponseWrapper().getHeaders().getReadOnlyHeaders();
		int match = 0;
		for (Header header : headers)
		{
			if (header.getName().equalsIgnoreCase(headerName))
			{
				if (header.getValue().toUpperCase().startsWith(headerValue.toUpperCase()))
				{
					match = 2;
					break;
				}
				match = 1;
			}
		}

		return match;
	}

	private void crlfReport(StandardHttpTransaction transaction, HttpHeaderContext context,
			String attackString, String headerName, String headerValue, StandardHttpTransaction attackTransaction)
	{
		attackTransaction.writeToDisk();
		String longDescription =
				"The query parameter named " + context.getRequestDatum().getReferenceChain().toString() + " on "
						+ transaction.getRequestWrapper().getURI() + " appears to be vulnerable to carriage " +
						" return / line feed (CRLF) injection attacks. An attacker can use this " +
						"to craft arbitrary response headers, and, in the right circumstance, " +
						"craft arbitrary response bodies.<br>" +
						"<br>" +
						"The attack was performed using an HTTP "
						+ transaction.getRequestWrapper().getMethod().toUpperCase()
						+ " command. When the attack string \"" + HtmlUtils.escapeHTML(attackString)
						+ "\" was supplied as the parameter value, it was placed in the \"" +
						context.getContextHeader().getName() + "\" header. The CRLF allowed a new " +
						"header named \"" + headerName + "\" to be inserted with the value " +
						"\"" + headerValue + "\".<br><br>The transaction used for testing can be viewed " +
						HtmlUtils.makeLink(attackTransaction.getSavedUrl(), "here") + ".";
		if (transaction.getRequestWrapper().getMethod().equalsIgnoreCase("GET"))
		{
			String url = transaction.getRequestWrapper().getURI();
			longDescription +=
					"<br><br>This attack can be duplicated by visiting the following URL: " + HtmlUtils.makeLink(url);
		}

		String impact = "";

		String recomendation = "Insert the user input into the header without unescaping the " +
								"values (e.g. %0d -> \\x0d)";
		if (context.getContextHeader().getName().equalsIgnoreCase("location"))
		{
			recomendation += "";
		}

		Finding event =
				new Finding(null, getName(), FindingSeverity.MEDIUM, transaction.getRequestWrapper().getURI(),
						"CRLF Injection", "CRLF injection found.",
						longDescription, impact, recomendation, "");
		Scan.getInstance().getFindings().addFinding(event);
	}

	private void locationReport(StandardHttpTransaction transaction, HttpHeaderContext context,
			String attackString)
	{
		String longDescription =
				"The query parameter named " + context.getRequestDatum().getReferenceChain().toString() + " on "
						+ transaction.getRequestWrapper().getURI() + " allows for arbitrary location headers to be " +
						"defined in the result.\n" +
						"\n" +
						"The attack was performed using an HTTP "
						+ transaction.getRequestWrapper().getMethod().toUpperCase()
						+ " command. When the attack string \"" + attackString
						+ "\" was supplied as the parameter value, it was placed in the \"location\" " +
						"header.\n";
		if (transaction.getRequestWrapper().getMethod().equalsIgnoreCase("GET"))
		{
			String url = transaction.getRequestWrapper().getURI();
			longDescription +=
					"This attack can be duplicated by visiting the following URL: " + HtmlUtils.makeLink(url);
		}

		String impact = "location blah blah blah.";

		String recomendation = "filter blah blah blah";

		Finding event =
				new Finding(null, getName(), FindingSeverity.LOW, transaction.getRequestWrapper().getURI(),
						"Location Header Override", "location header...",
						longDescription, impact, recomendation, "");
		Scan.getInstance().getFindings().addFinding(event);
	}

	private boolean testCRLFInjection(HttpHeaderContext headerContext, int testJobId) throws UnrequestableTransaction, InterruptedScanException
	{
		handlePause_isRunning();
		String headerName = TokenTesting.getInstance().generateToken();
		String headerValue = TokenTesting.getInstance().generateToken();

		String attackString = "blah\r\n" + headerName + ":" + headerValue;
		StandardHttpTransaction transactions[] =
				TokenTesting.getInstance().duplicateTokenTest(headerContext, attackString, getName(),
						TransactionSource.MISC_TEST, testJobId);
		handlePause_isRunning();
		int match = checkSuccess(transactions, headerName, headerValue);
		if (match > 0)
		{
			crlfReport(transactions[0], headerContext, attackString, headerName, headerValue, transactions[0]);
		}

		return match == 2 ? true : false;
	}

	private void testLocation(HttpHeaderContext headerContext, int testJobId) throws InterruptedScanException
	{
		String location = "http://www." + TokenTesting.getInstance().generateToken() + ".com";
		StandardHttpTransaction transactions[];
		try
		{
			transactions =
					TokenTesting.getInstance().duplicateTokenTest(headerContext, location,
							getName(), TransactionSource.MISC_TEST, testJobId);
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
			return;
		}

		if (checkSuccess(transactions, "Location", location) == 2)
		{
			locationReport(transactions[0], headerContext, location);
		}
	}

}
