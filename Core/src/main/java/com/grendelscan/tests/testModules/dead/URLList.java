package com.grendelscan.tests.testModules.dead;
//package com.grendelscan.tests.testModules.architecture;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import com.grendelscan.data.findings.Finding;
//import com.grendelscan.data.findings.FindingSeverity;
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
//import com.grendelscan.tests.testModuleUtils.settings.ConfigurationOption;
//import com.grendelscan.tests.testModuleUtils.settings.FileNameOption;
//import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
//import com.grendelscan.tests.testModules.TestModule;
//import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;
//import com.grendelscan.utils.FileUtils;
//import com.grendelscan.utils.HtmlUtils;
//import com.grendelscan.utils.MimeUtils;
//import com.grendelscan.utils.URIStringUtils;
//
//public class URLList extends TestModule implements ByHttpResponseCodeTest
//{
//	List<ConfigurationOption>	configOptions;
//	FileNameOption				outputFileOption;
//	SelectableOption			recordAllHTMLFilesOption;
//	SelectableOption			recordQueriesOption;
//	Set<String>					urls;
//
//	public URLList()
//	{
//		configOptions = new ArrayList<ConfigurationOption>(3);
//		outputFileOption =
//				new FileNameOption("Output file name", "web-site-map", "The output filename for the website map.",
//						false);
//		configOptions.add(outputFileOption);
//
//		recordQueriesOption =
//				new SelectableOption("Record URL queries", false,
//						"If selected, all unique URL query strings will also be recorded.");
//		configOptions.add(recordQueriesOption);
//
//		recordAllHTMLFilesOption =
//				new SelectableOption(
//						"Record all files",
//						false,
//						"If not selected, only the URLs to web files (HTML, XHTML, XML, JavaScript, CSS, and text) will be recorded. This will omit images, audio, vido, pdfs, etc");
//		configOptions.add(recordAllHTMLFilesOption);
//
//	}
//
//	@Override
//	public List<ConfigurationOption> getConfigurationOptions()
//	{
//		return configOptions;
//	}
//
//	@Override
//	public String getDescription()
//	{
//		return "Records a list of all URLs successfully requested. This does not include attack requests.";
//	}
//
//	@Override
//	public TestModuleGUIPath getGUIDisplayPath()
//	{
//		return TestModuleGUIPath.ARCHITECTURE;
//	}
//
//	@Override
//	public int getModuleNumber()
//	{
//		return 29;
//	}
//
//	@Override
//	public String getName()
//	{
//		return "Website map";
//	}
//
//	@Override
//	public String[] getResponseCodes()
//	{
//		return new String[] { "200", "301", "302", "303", "304", "307", "401", "403", "500", "504" };
//	}
//
//	@Override
//	public void initialize()
//	{
//		urls = new HashSet<String>(100);
//	}
//
//	@Override
//	public boolean isExperimental()
//	{
//		return false;
//	}
//
//	@Override
//	public void scanIsComplete()
//	{
//		String urlsArray[] = urls.toArray(new String[0]);
//		Arrays.sort(urlsArray);
//		String reportString = "";
//		for (String url : urlsArray)
//		{
//			reportString += url + "\n";
//		}
//		String filename;
//		if (outputFileOption.getValue().contains(File.separator))
//		{
//			filename = outputFileOption.getValue();
//		}
//		else
//		{
//			filename = Scan.getInstance().getOutputDirectory() + File.separator + outputFileOption.getValue();
//		}
//		if (!FileUtils.writeToFile(filename, reportString))
//		{
//			reportString = "Writing to the original file name failed\n\n" + reportString;
//			FileUtils.writeToFile(Scan.getInstance().getOutputDirectory() + File.separator + "web-site-map",
//					reportString);
//		}
//
//		genReport();
//	}
//
//	@Override
//	public void testByHttpResponseCode(int transactionID)
//	{
//		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
//		if (recordAllHTMLFilesOption.isSelected()
//				|| MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
//		{
//			if (recordQueriesOption.isSelected())
//			{
//				urls.add(transaction.getRequestWrapper().getAbsoluteUriString());
//			}
//			else
//			{
//				urls.add(URIStringUtils.getBaseUri(transaction.getRequestWrapper().getURI()));
//			}
//		}
//	}
//
//	private void genReport()
//	{
//
//		String title = "URL list";
//		String shortDesc = "A list of requested URLs was generated";
//		String longDesc =
//				"A list of all requested URLs was generated and saved to "
//						+ HtmlUtils.makeLink(outputFileOption.getValue()) +
//						". In total, " + urls.size() + " URLs were observed.";
//		Finding event = new Finding(null, "Module " + getModuleNumber(), FindingSeverity.INFO, "",
//				title, shortDesc, longDesc, "", "", "");
//		Scan.getInstance().getFindings().addFinding(event);
//	}
//
//}
