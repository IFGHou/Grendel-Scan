package com.grendelscan.tests.testModules.miscellaneous;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import com.grendelscan.data.database.DataNotFoundException;
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
import com.grendelscan.tests.libraries.platformErrorMessages.PlatformErrorMessages;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModuleUtils.settings.ConfigChangeHandler;
import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
import com.grendelscan.tests.testModuleUtils.settings.TextOption;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testModules.informationLeakage.PlatformErrors;
import com.grendelscan.tests.testTypes.ByRequestDataTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.URIStringUtils;

public class GenericFuzzer extends TestModule implements ByRequestDataTest
{
//	private List<String>				findings;
	private Finding finding;
	private static final String FINDING_NUMBER_NAME = "generic_fuzzer_finding_number"; 
	private TextOption					hexCharsOption;
	private SelectableOption			ignoreTransactionsWithErrorsOption;
	private Set<String>					patterns;
	private TextOption					plainTextCharsOption;
	private TextOption					stringsOption;

	public GenericFuzzer()
	{
		ignoreTransactionsWithErrorsOption =
				new SelectableOption(
						"Ignore original responses with platform error messages",
						true,
						"If enabled, transactions that have platform error messages in the original responses will not be fuzzed.", null);
		addConfigurationOption(ignoreTransactionsWithErrorsOption);

		ConfigChangeHandler patternChange = new ConfigChangeHandler()
		{
			@Override
			public void handleChange()
			{
				initializePatterns();
			}
		};
		
		plainTextCharsOption = new TextOption("Plain text fuzzing characters",
				ConfigurationManager.getString("fuzzer.default_plain_text_list"),
				"Plain text characters that are fuzzed one at a time.", true, patternChange);
		addConfigurationOption(plainTextCharsOption);

		hexCharsOption = new TextOption("Hex encoded fuzzing characters",
				ConfigurationManager.getString("fuzzer.default_hex_list"),
				"Hex encoded characters, separated by spaces.", true, patternChange);
		addConfigurationOption(hexCharsOption);

		stringsOption = new TextOption("Fuzzing strings",
				ConfigurationManager.getString("fuzzer.default_string_list"),
				"Fuzz strings, one on each line.", true, patternChange);
		addConfigurationOption(stringsOption);

		PlatformErrorMessages.getInstance();
		addConfigurationOption(PlatformErrorMessages.getRegexErrorPatternsOption());
		try
		{
			int findingNumber = Scan.getInstance().getTestData().getInt(FINDING_NUMBER_NAME);
			finding = Scan.getInstance().getFindings().get(findingNumber);
		}
		catch (DataNotFoundException e1)
		{
			// no problem
		}

		patterns = new HashSet<String>();

		initializePatterns();
	}


	@Override
	public String getDescription()
	{
		return "The fuzzing strings defined below are appended onto every query parameter and the "
				+
				"output is searched for standard platform error messages. To ignore transactions that "
				+
				"have platform error messages in the original response, enable the first option. Note "
				+
				"that this option will only be used if the \"Platform error messages\" module under "
				+
				"\"Information leakage\" is also enabled.\n"
				+
				"\n"
				+
				"The first text box contains characters that are individually tested. The second box "
				+
				"contains the hex values of non-printable characters to test, separated by whitespace. "
				+
				"The third box contains a fuzzing string on each line. The long line contains 1024 'a' "
				+
				"characters. The fourth box contains an optional list of regular expressions that "
				+
				"define platform error messages. The expression is matched against any part of the "
				+
				"response, so \"error\" will work (you don't "
				+
				"need \".*error.*\"). The patterns are case-insensitive and will be tested against both the raw response body, and the "
				+
				"combined text elements of the HTML. This option is useful if you know that there are unusual " +
				"messages in the targeted application. This setting is shared with the \"Platform error " +
				"messages\" module.";
	}

	@Override
	public String getExperimentalText()
	{
		return "The generic fuzzing module has great potential for discovering improper " +
				"input validation. However, some performance problems have been observed " +
				"with it. Specifically, test transactions generated by the fuzzer may " +
				"become increasingly slow to process as the scan progresses. This may be " +
				"alieviated by reducing the number of strings and characters used to " +
				"generate fuzzing requests. It may also be advisable to run a scan where " +
				"the fuzzing test module is the only test module enabled, other than the " +
				"spider.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
	}


	@Override
	public String getName()
	{
		return "Generic fuzzing";
	}

	@Override
	public Class<TestModule>[] getSoftPrerequisites()
	{
		return new Class[]{PlatformErrors.class};
	}
	
	// Intentionally given default visibility
	void initializePatterns()
	{
		synchronized(patterns)
		{
			patterns.clear();
			for (String pattern : stringsOption.getValue().split("[\r\n]+"))
			{
				patterns.add(pattern);
			}
	
			final String value = plainTextCharsOption.getValue();
			for (int index = 0; index < value.length(); index++)
			{
				patterns.add(String.valueOf(value.charAt(index)));
			}
	
			for (String pattern : hexCharsOption.getValue().split("\\s+"))
			{
				try
				{
					patterns.add(String.valueOf(Character.toChars(Integer.parseInt(pattern, 16))[0]));
				}
				catch (Exception e)
				{
					Log.error("Bad hex pattern (" + pattern + "): " + e.toString(), e);
				}
			}
		}
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
	 * (com.grendelscan.requester.http.payloads.QueryParameter)
	 */
	@Override
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException
	{
		if (ignoreTransactionsWithErrorsOption.isSelected()
				&& PlatformErrorMessages.getInstance().getReadOnlyMessageHits()
						.containsKey(datum.getTransactionId()))
		{
			return;
		}

		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);
		String originalValue = new String(DataUtils.getBytes(datum));

		Set<String> tmpPatterns;
		synchronized(patterns)
		{
			tmpPatterns = new HashSet<String>(patterns);
		}
		for (String fuzzString : tmpPatterns)
		{
			handlePause_isRunning();
			StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
			MutableData testData = (MutableData) DataContainerUtils.resolveReferenceCousin(testTransaction, datum);
			testData.setBytes((originalValue + fuzzString).getBytes());
			RequestOptions testRequestOptions = requestOptions.clone();
			testRequestOptions.followRedirects = transaction.getRedirectChildId() > 0;
			testTransaction.setRequestOptions(testRequestOptions);
			try
			{
				testTransaction.execute();
				String matchPattern = PlatformErrorMessages.getInstance().isErrorMatch(testTransaction, false);
				if (matchPattern.equals("") && (testTransaction.getLogicalResponseCode() >= 500))
				{
					matchPattern = "500 response code";
				}

				if (!matchPattern.equals(""))
				{
					recordFinding(testTransaction, datum.getReferenceChain().toString(), originalValue, fuzzString, matchPattern);
				}
			}
			catch (UnrequestableTransaction e)
			{
				Log.error(getName() + " request unrequestable ("
						+ testTransaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
			}
		}
	}

	private synchronized void recordFinding(StandardHttpTransaction testTransaction, String parameterName, String originalValue,
			String fuzzString, String matchPattern)
	{
		testTransaction.writeToDisk();
		String goodFuzzString = "";
		if (fuzzString.matches(".*[\\x00-\\x1f\\x7f-\\xff].*") || fuzzString.equals(" "))
		{
			for (int i = 0; i < fuzzString.length(); i++)
			{
				goodFuzzString += String.format("0x%02X ", Integer.valueOf(fuzzString.charAt(i)));
			}
		}
		else
		{
			goodFuzzString = fuzzString;
		}

		String findingDescription = null;
		try
		{
			findingDescription = "<tr>" +
					"<td class=\"fuzzheader\">Test transaction:</td>" +
					"<td>"
					+ HtmlUtils.makeLink(testTransaction.getSavedUrl(),
							URIStringUtils.getHostUri(testTransaction.getRequestWrapper().getAbsoluteUriString())) + "</td>" +
					"</tr><tr>" +
					"<td class=\"fuzzheader\">Parameter name:</td>" +
					"<td>" + parameterName + "</td>" +
					"</tr><tr>" +
					"<td class=\"fuzzheader\">Fuzz string:</td>" +
					"<td>" + goodFuzzString + "</td>" +
					"</tr><tr>" +
					"<td class=\"fuzzheader\">Original value:</td>" +
					"<td>" + originalValue + "</td>" +
					"</tr><tr>" +
					"<td class=\"fuzzheader\">Match pattern:</td>" +
					"<td>" + HtmlUtils.escapeHTML(matchPattern) + "</td>" +
					"</tr><tr><td>&nbsp</td></tr>\n";
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		
		if (finding == null)
		{
			String title = "Successful fuzzing tests";
			String shortDesc = "Some generic fuzzing tests appear to be successful";
			String longDesc = "Some generic fuzzing tests appear to have been successful at generating an " +
					"infrastructure error. The results are listed in the table below:<br><br>" +
					"<style>.fuzzheader{font-weight: bold;vertical-align: top;text-align: left;}</style>\n<table>";
				longDesc += findingDescription;

			String impact = "The impact will vary depending on the nature of the error.";
			String recomendations = "Each result should be individually investigated. The error message " +
					"may provide a hint as to how the user-supplied data is being used.";
			finding = new Finding(null, getName(), FindingSeverity.INFO,
					"Multiple", title, shortDesc, longDesc, impact, recomendations, "");
			finding.setLongDescriptionFooter("</table>\n");
			Scan.getInstance().getFindings().addFinding(finding);
			Scan.getInstance().getTestData().setInt(FINDING_NUMBER_NAME, finding.getId());
		}
		else
		{
			finding.setLongDescription(finding.getLongDescription() + findingDescription);
		}
		
		
	}


}
