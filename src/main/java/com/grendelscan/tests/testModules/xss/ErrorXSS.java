//package com.grendelscan.tests.testModules.xss;
//
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Pattern;
//
//import com.grendelscan.data.database.collections.DatabaseBackedList;
//import com.grendelscan.data.findings.Finding;
//import com.grendelscan.data.findings.FindingSeverity;
//import com.grendelscan.logging.Log;
//import com.grendelscan.requester.TransactionSource;
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
//import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
//import com.grendelscan.scan.ConfigurationManager;
//import com.grendelscan.scan.InterruptedScanException;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
//import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
//import com.grendelscan.tests.libraries.XSS.SuccessfullXSS;
//import com.grendelscan.tests.libraries.XSS.XSS;
//import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
//import com.grendelscan.tests.testModules.TestModule;
//import com.grendelscan.tests.testTypes.ByBaseUriTest;
//import com.grendelscan.tests.testTypes.ByHostTest;
//import com.grendelscan.utils.HtmlUtils;
//import com.grendelscan.utils.StringUtils;
//import com.grendelscan.utils.URIStringUtils;
//
//public class ErrorXSS extends TestModule implements ByHostTest, ByBaseUriTest
//{
//
//	
//
//
//	List<String>								extensions;
//	DatabaseBackedList<String> testedBaseUris; 
//
//	public ErrorXSS()
//	{
//
//		addConfigurationOption(XSS.getAgressionOptions());
//		extensions = ConfigurationManager.getList("file_enumeration.common_framework_extensions");
//		testedBaseUris = new DatabaseBackedList<String>("error_xss_tested_base_uris");
//		
//		requestOptions.followRedirects = true;
//	}
//
//
//	@Override
//	public String getDescription()
//	{
//		return "Tests server/framework file not found messages for XSS "
//				+ "vulnerabilities. Some 404 messages may parse the "
//				+ "filename as HTML. Since the platform may have "
//				+ "different error messages for different file types, each "
//				+ "file extension listed under \"common_framework_extensions\" "
//				+ "in conf/file_enumeration is tested separately.";
//	}
//
//	@Override
//	public TestModuleGUIPath getGUIDisplayPath()
//	{
//		return TestModuleGUIPath.XSS;
//	}
//
//
//	@Override
//	public String getName()
//	{
//		return "Error XSS";
//	}
//
//
//	@Override
//	public boolean isExperimental()
//	{
//		return false;
//	}
//
//	@Override
//	public void testByServer(int transactionID, int testJobId) throws InterruptedScanException
//	{
//		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
//		try
//		{
//			testBaseUri(URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString()), testJobId);
//		}
//		catch (URISyntaxException e)
//		{
//			Log.error("Very weird URI parsing problem: " + e.toString(), e);
//		}
//	}
//	
//	private void testBaseUri(String baseUri, int testJobId) throws InterruptedScanException
//	{
//		synchronized(testedBaseUris)
//		{
//			if (testedBaseUris.contains(baseUri))
//			{
//				Log.info(baseUri + " already tested by ErrorXSS");
//				return;
//			}
//			testedBaseUris.add(baseUri);
//		}
//		ArrayList<String> uniqueServerExtensions = new ArrayList<String>();
//		uniqueServerExtensions.addAll(extensions);
//		identifyUniqueErrorPages(baseUri, uniqueServerExtensions, testJobId);
//		HashSet<String> responseBodies = new HashSet<String>();
//		for (String extension : uniqueServerExtensions)
//		{
//			handlePause_isRunning();
//			testExtension(baseUri, extension, responseBodies, testJobId);
//		}
//	}
//
//	private void identifyUniqueErrorPages(String baseUri, List<String> serverExtensions, int testJobId) throws InterruptedScanException
//	{
//
//		String filename = StringUtils.generateRandomString(StringUtils.FORMAT_UPPER_CASE_ALPHA, 8);
//		HashSet<String> responseBodies = new HashSet<String>();
//		List<String> tempExtensions = new ArrayList<String>(serverExtensions);
//		for (String extension : tempExtensions)
//		{
//			handlePause_isRunning();
//			try
//			{
//				String uri = baseUri + filename + "." + extension;
//				if (Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
//				{
//					StandardHttpTransaction testRequest = new StandardHttpTransaction(TransactionSource.MISC_TEST, testJobId);
//					testRequest.getRequestWrapper().setURI(uri, true);
//					testRequest.setRequestOptions(requestOptions);
//					testRequest.execute();
//					boolean unique = false;
//					if (testRequest.isSuccessfullExecution())
//					{
//						String adjustedResponseBody = new String(testRequest.getResponseWrapper().getBody());
//						Pattern filenamePattern = Pattern.compile(filename + "\\." + extension, Pattern.CASE_INSENSITIVE);
//						adjustedResponseBody = filenamePattern.matcher(adjustedResponseBody).replaceAll("");
//						if (!isRepeatedErrorPage(adjustedResponseBody, responseBodies, 90))
//						{
//							// Only categorize it if the response is unique
////							Scan.getInstance().getCategorizerQueue().addTransaction(testRequest);
//							responseBodies.add(adjustedResponseBody);
//							unique = true;
//						}
//					}
//					if (!unique)
//					{
//						serverExtensions.remove(extension);
//					}
//				}
//				else
//				{
//					Log.debug("Error XSS can't request " + uri);
//				}
//			}
//			catch (UnrequestableTransaction e)
//			{
//				Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
//			}
//		}
//	}
//
//	private boolean isRepeatedErrorPage(String response, HashSet<String> responseBodies, int threshold)
//	{
//		boolean same = false;
//		for (String responseB : responseBodies)
//		{
//			if (StringUtils.scoreStringDifference(response, responseB, 100) > threshold)
//			{
//				same = true;
//				break;
//			}
//		}
//		return same;
//	}
//
//	private void report(SuccessfullXSS result, String baseUri)
//	{
//		String url = result.transaction.getRequestWrapper().getAbsoluteUriString();
//		String longDescription =
//				"The file not found message on "
//						+ baseUri
//						+ " appears to be vulnerable to cross site scripting (XSS) attacks. When the attack string \""
//						+ HtmlUtils.escapeHTML(result.attackString)
//						+ "\" was supplied as the file name, it appears to have been placed in the results in a way "
//						+ "that allows arbitrary JavaScript to be executed.<br>"
//						+ "The token \""
//						+ result.token
//						+ "\" was used for tracking purposes during testing. Depending on the attack string, it may need to be "
//						+ "modified to perform an actual XSS attack. This attack can be duplicated by visiting the "
//						+ "following URL: " + HtmlUtils.makeLink(url)
//						+ "The transaction used for testing can be viewed "
//						+ HtmlUtils.makeLink(result.transaction.getSavedUrl(), "here") + ".";
//
//		if (result.attackString.contains(XSS.getJavascriptMethod()))
//		{
//			longDescription +=
//					"Note that \""
//							+ XSS.getJavascriptMethod()
//							+ "\" is a fictional JavaScript function used for testing. "
//							+ "This is because some anti-XSS filters will block common JavaScript functions (e.g. \"alert\") by name. This is "
//							+ "not a sufficient security control; the fictional function allows the test to proceed more rapidly than testing "
//							+ "multiple real functions. ";
//		}
//		if (result.attackString.contains(XSS.getFakeHostname()))
//		{
//			longDescription +=
//					"The hostname \"" + XSS.getFakeHostname()
//							+ "\" is a fictional hostname used for testing. To perform "
//							+ "an actual XSS attack, replace it with the name of a host that you control. ";
//		}
//
//		if (result.attackString.contains(XSS.getFakeIPAddress()))
//		{
//			longDescription +=
//					"The IP address \"" + XSS.getFakeIPAddress()
//							+ "\" is a fictional IP address used for testing. To perform "
//							+ "an actual XSS attack, replace it with the address of a host that you control. ";
//		}
//
//		Finding event =
//				new Finding(null, getName(), FindingSeverity.MEDIUM, url,
//						"Cross Site Scripting (XSS)", "Possible cross site scripting (XSS) discovered.",
//						longDescription, XSS.getXSSImpact(), XSS.getXSSRecomendations(), XSS.getXSSReferences());
//		Scan.getInstance().getFindings().addFinding(event);
//	}
//
//
//	private void testExtension(String baseUri, String extension, Set<String> responseBodies, int testJobId) throws InterruptedScanException
//	{
//		List<TokenContext> contexts;
//
//		String token = TokenTesting.getInstance().generateToken();
//		String uri = baseUri + token + "." + extension;
//		try
//		{
//			if (Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
//			{
//				StandardHttpTransaction initialTokenTestTransaction = new StandardHttpTransaction(TransactionSource.MISC_TEST, testJobId);
//				initialTokenTestTransaction.getRequestWrapper().setURI(uri, true);
//				initialTokenTestTransaction.setRequestOptions(requestOptions);
//				initialTokenTestTransaction.execute();
//				TokenTesting.getInstance().recordTokenTest(token,
//						new FilenameComponent(new FilenamePayload(initialTokenTestTransaction.getId())));
//				contexts = TokenTesting.getInstance().findTokenContexts(initialTokenTestTransaction, token).getAllContexts();
//	
//				SuccessfullXSS result = XSS.testHtmlContexts(contexts, getName(), testJobId);
//				if (result != null)
//				{
//					report(result, baseUri);
//				}
//			}
//			else
//			{
//				Log.debug("ErrorXSS can't request " + uri);
//			}
//		}
//		catch (UnrequestableTransaction e)
//		{
//			Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.grendelscan.tests.testTypes.ByBaseUriTest#testByBaseUri(java.lang.String)
//	 */
//	@Override
//	public void testByBaseUri(String baseUri, int testJobId) throws InterruptedScanException
//	{
//		try
//		{
//			testBaseUri(URIStringUtils.getDirectoryUri(baseUri), testJobId);
//		}
//		catch (URISyntaxException e)
//		{
//			Log.error("Very weird problem with URI parsing: " + e.toString(), e);
//		}
//	}
//
//}
