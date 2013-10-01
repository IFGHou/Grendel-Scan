package com.grendelscan.tests.testModules.xss;

import java.util.Collection;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextTypeUtils;
import com.grendelscan.tests.libraries.XSS.SuccessfullXSS;
import com.grendelscan.tests.libraries.XSS.XSS;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testModules.hidden.TokenSubmitter;
import com.grendelscan.tests.testTypes.ByRepeatableOutputContextTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.URIStringUtils;

public class QueryXSS extends TestModule implements ByRepeatableOutputContextTest
{

	public QueryXSS()
	{
		addConfigurationOption(XSS.getAgressionOptions());
	}

	@Override
	public String getDescription()
	{
		return "Tests for XSS from HTTP query parameters. Both POST and GET " +
				"transactions are tested, but only parameters known to be " +
				"used in output are tested. The base attack strings are " +
				"defined in conf/xss.conf, but the actual attack is based " +
				"on the HTML context that the output is observed in. The " +
				"response is run through an HTML parser and JavaScript " +
				"engine to identify successful attacks. Note that using " +
				"the \"high\" aggression setting can be particularly noisy " +
				"and take a while to run due to all of the parsing. " +
				"Some particularly exotic attacks (http://ha.ckers.org/xss.html) " +
				"may not be discovered. This should be improved in future releases.";
	}

	@Override
	public TokenContextType[] getDesiredRepeatableContexts()
	{
		return TokenContextTypeUtils.getHtmlContexts();
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.XSS;
	}

	@Override
	public String getName()
	{
		return "XSS - Query";
	}

	@Override
	public boolean isExperimental()
	{
		return false;
	}


	@Override
	public void testByRepeatableOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException
	{
		SuccessfullXSS result = XSS.testHtmlContexts(contexts, getName(), testJobId);
		if (result != null)
		{
			recordFinding(result);
			return;
		}
	}

	private void recordFinding(SuccessfullXSS result)
	{
		result.transaction.writeToDisk();
		String longDescription =
				"The query parameters listed below appear vulnerable to cross site scripting (XSS) attacks. When the "
						+
						"attack string was supplied as the parameter value, it appears to have been placed in the results in a "
						+
						"way that allows arbitrary JavaScript to be executed. A unique token was used for tracking purposes "
						+
						"during testing. <br>\n<br>\n" +
						"Note that some test attacks use a fictional JavaScript function ("
						+ XSS.getJavascriptMethod()
						+ ") for testing. "
						+ "This is because some anti-XSS filters will block common JavaScript functions (e.g. \"alert\") by name. This is "
						+ "not a sufficient security control; the fictional function allows the test to proceed more rapidly than testing "
						+ "multiple real functions. Other test attacks use a fictional hostname ("
						+ XSS.getFakeHostname()
						+ ") or a "
						+
						" fictional IP address ("
						+ XSS.getFakeIPAddress()
						+ ") for testing. To perform "
						+ "an actual XSS attack, replace it with the name of a host that you control.<br>\n<br>\n"
						+
						"URL: "
						+ URIStringUtils.getFileUri(result.transaction.getRequestWrapper().getURI())
						+ "<br>\n"
						+
						"Parameter: "
						+ result.context.getRequestDatum().getReferenceChain().toString()
						+ "<br>\n"
						+
						"Method: "
						+ result.transaction.getRequestWrapper().getMethod().toUpperCase()
						+ "<br>\n"
						+
						"Attack string: "
						+ HtmlUtils.escapeHTML(result.attackString)
						+ "<br>\n"
						+
						"Token: "
						+ result.token
						+ "<br>\n"
						+
						"Transaction: "
						+ HtmlUtils.makeLink(result.transaction.getSavedUrl(), String.valueOf(result.transaction.getId())) + "<br>\n";


		if (result.transaction.getRequestWrapper().getMethod().equalsIgnoreCase("GET"))
		{
			String url = result.transaction.getRequestWrapper().getURI();
			longDescription += "The attack can be duplicated following " + HtmlUtils.makeLink(url, "this link") + ".";
		}

		Finding event =
				new Finding(null, getName(), FindingSeverity.MEDIUM, "See description",
						"Cross-Site Scripting (XSS)", "Possible cross-site scripting (XSS) discovered.",
						longDescription, XSS.getXSSImpact(), XSS.getXSSRecomendations(), XSS.getXSSReferences());
		Scan.getInstance().getFindings().addFinding(event);

	}

	@Override
	public Class<? extends TestModule>[] getPrerequisites()
	{
		return new Class[]{TokenSubmitter.class};
	}


}
