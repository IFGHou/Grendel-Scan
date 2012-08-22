package com.grendelscan.tests.testModules.informationLeakage;

import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.platformErrorMessages.PlatformErrorMessages;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.AllTransactionsTest;
import com.grendelscan.utils.HtmlUtils;

public class PlatformErrors extends TestModule implements AllTransactionsTest
{
	private Finding finding;
	private final static String FINDING_NAME = "platform_error_finding_number";
	public PlatformErrors()
	{
		PlatformErrorMessages.initialize();
		addConfigurationOption(PlatformErrorMessages.getInstance().getRegexErrorPatternsOption());
	}


	@Override
	public String getDescription()
	{
		return "Searches all responses for platform error messages (.Net, PHP, Cold Fusion, MySQL, Oracle, etc). "
				+
				"Only the first error message in each response will be reported, so you may want to manually "
				+
				"review the entire response.\n"
				+
				"\n"
				+
				"An optional list of regular expressions that define platform error messages can be provided. "
				+
				"The expression is matched against any part of the response, so \"error\" will work (you don't "
				+
				"need \".*error.*\"). The patterns are case insensitive and will be tested against both the raw response body, and the "
				+
				"combined text elements of the HTML. This option is useful if you know that there are unusual " +
				"messages in the targeted application. This setting is shared with the \"Platform error " +
				"messages\" module.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.INFORMATION_LEAKAGE;
	}

	@Override
	public String getName()
	{
		return "Platform error messages";
	}

	@Override
	public boolean isExperimental()
	{
		return false;
	}

	public synchronized void logFinding(StandardHttpTransaction transaction)
	{
		try
		{
			if (finding == null)
			{
				finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME)); 
			}
		}
		catch (DataNotFoundException e)
		{
			String title = "Platform error messages found";
			String shortDesc = "Some platform error messages were found in a response";
			String longDesc = "Some platform error messages were found in a response. The transactions" +
					" are listed in the table below:<br><br>" +
					"\n<table><tr style=\"font-weight: bold\"><td>Transaction</td><td>Match pattern</td></tr>\n";

			String impact = "The impact will vary depending on the nature of the error.";
			String recomendations = "Each result should be individually investigated. The error message " +
					"may provide a hint about the internal structure of the application, or attacks that " +
					"can be launched.";
			finding = new Finding(null, getName(), FindingSeverity.INFO,
					"Multiple", title, shortDesc, longDesc, impact, recomendations, "");
			finding.setLongDescriptionFooter("</table>\n");
			Scan.getInstance().getFindings().addFinding(finding);
			Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
		}

		String desc = finding.getLongDescription();

		desc += "<tr><td>"
				+ HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId()))
				+ "</td><td>"
				+
				HtmlUtils.escapeHTML(PlatformErrorMessages.getInstance().getReadOnlyMessageHits()
						.get(transaction.getId())) + "</td></tr>\n";

		finding.setLongDescription(desc);
	}

	@Override
	public void testAllTransactions(int transactionID, int testJobId)
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);

		String matchPattern = PlatformErrorMessages.getInstance().isErrorMatch(transaction, true);
		if (!matchPattern.equals(""))
		{
			logFinding(transaction);
			transaction.writeToDisk();
		}
	}

}
