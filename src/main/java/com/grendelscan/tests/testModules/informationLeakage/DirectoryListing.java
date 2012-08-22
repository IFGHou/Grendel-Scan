package com.grendelscan.tests.testModules.informationLeakage;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByDirectoryTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.URIStringUtils;

public class DirectoryListing extends TestModule implements ByDirectoryTest
{
	private static Pattern	titlePattern	= Pattern.compile("<title(.+?)</title>", Pattern.CASE_INSENSITIVE);
	private String[]		basicPatterns;
	private String[]		titlePatterns;
	private Finding finding;
	private static final String FINDING_NAME = "directory_listing_finding_number";

	public DirectoryListing()
	{
		basicPatterns = ConfigurationManager.getStringArray("directory_indexing_patterns.basic");
		titlePatterns = ConfigurationManager.getStringArray("directory_indexing_patterns.title");
		requestOptions.followRedirects = true;
		requestOptions.testRedirectTransactions = true;
	}
	
	@Override
	public String getDescription()
	{
		return "Checks for directory listing in all directories discovered by Grendel-Scan.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.INFORMATION_LEAKAGE;
	}


	@Override
	public String getName()
	{
		return "Directory listings";
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}


	@Override
	public void testByDirectory(int transactionID, String directory, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction originalTransaction =
				Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		try
		{
			StandardHttpTransaction testTransaction;
			if (directory.equals(originalTransaction.getRequestWrapper().getURI()))
			{
				testTransaction = originalTransaction;
			}
			else
			{
				testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
				testTransaction.getRequestWrapper().setMethod("GET");
				testTransaction.getRequestWrapper().setURI(directory, true);
				testTransaction.setRequestOptions(requestOptions);
				testTransaction.execute();
			}
			if (isDirectoryListing(testTransaction))
			{		
				logFinding(testTransaction.getRequestWrapper().getAbsoluteUriString());
			}
		}
		catch (UnrequestableTransaction e)
		{
			Log.debug(getName() + " request unrequestable (" + e.toString() + ")", e);
		}
	}

	private boolean isDirectoryListing(StandardHttpTransaction testTransaction)
	{
		boolean directoryListing = false;
		if (MimeUtils.isWebTextMimeType(testTransaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			String escapedDirectory = null;
			try
			{
				escapedDirectory = Pattern.quote(URIStringUtils.getDirectory(testTransaction.getRequestWrapper().getURI()));
			}
			catch (URISyntaxException e)
			{
				IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
				Log.error(e.toString(), e);
				throw ise;
			}
			String content = new String(testTransaction.getResponseWrapper().getBody());
			for (String rawPattern : basicPatterns)
			{
				Pattern pattern =
						Pattern.compile(rawPattern.replace("%%dir%%", escapedDirectory), Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(content).find())
				{
					directoryListing = true;
					break;
				}
			}

			if (!directoryListing)
			{
				Matcher m = titlePattern.matcher(content);
				if (m.find())
				{
					String titleText = m.group(1);

					for (String rawPattern : titlePatterns)
					{
						Pattern pattern =
								Pattern.compile(rawPattern.replace("%%dir%%", escapedDirectory),
										Pattern.CASE_INSENSITIVE);
						if (pattern.matcher(titleText).find())
						{
							directoryListing = true;
							break;
						}
					}
				}
			}
		}
		return directoryListing;
	}

	private synchronized void logFinding(String url)
	{
		try
		{
			if (finding == null)
			{
				finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME));
			}
			else
			{
				finding.setLongDescription(finding.getLongDescription() + 
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + HtmlUtils.makeLink(url) + "<br>\n");
			}
		}
		catch (DataNotFoundException e)
		{
			String title = "Directory content listing detected";
			String shortDescription = "At least one directory was found supporting content listing.";
			String longDescription =
					"At least one directory was found supporting content listing."
					+ "The vulnerable directories(s) are listed below:<br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + HtmlUtils.makeLink(url) + "<br>\n";
			
			String impact =
				"Directory listings can be used to explore web site content that would "
						+ "otherwise be unknown to an attacker.";
			String recomendations = "Disable directory content listing on all web servers.";
			String references = "";

			finding = new Finding(null, getName(), FindingSeverity.INFO, url, title, 
					shortDescription, longDescription, impact, recomendations, references);
			Scan.getInstance().getFindings().addFinding(finding);
			Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
		}
				
	}

}
