package com.grendelscan.tests.testModules.architecture;

import java.io.File;
import java.net.URISyntaxException;

import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModuleUtils.settings.IntegerOption;
import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;
import com.grendelscan.utils.ArrayUtils;
import com.grendelscan.utils.FileUtils;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.URIStringUtils;

public class WebsiteMirror extends TestModule implements ByHttpResponseCodeTest
{
	private static final String	FINDING_NUMBER_NAME	= "WEBSITE_MIRROR_FINDING";
	private String						baseDir;
	private IntegerOption				maxFileSize;
	private Finding finding;
	private SelectableOption			recordAllHTMLFilesOption;
	private SelectableOption			recordQueriesOption;

	public WebsiteMirror()
	{
//		outputDirectoryOption =
//				new FileNameOption("Output directory", "site-mirror", "The directory for the website mirror.", true, null);
//		configurationOptions.add(outputDirectoryOption);

		baseDir =
			Scan.getInstance().getOutputDirectory() + File.separator + "site-mirror"
					+ File.separator;
		
		recordQueriesOption =
				new SelectableOption("Record URL queries", true,
						"If selected, all unique URL query strings will be saved to separate files.", null);
		addConfigurationOption(recordQueriesOption);

		recordAllHTMLFilesOption =
				new SelectableOption(
						"Record all files",
						true,
						"If not selected, only the URLs to web files (HTML, XHTML, XML, JavaScript, CSS, and text) will be recorded. This will omit images, audio, video, PDFs, etc",
						null);
		addConfigurationOption(recordAllHTMLFilesOption);

		maxFileSize = new IntegerOption("Max file size", 1000000, "The maximum amount of bytes to write to disk", null);
		addConfigurationOption(maxFileSize);
	}


	@Override
	public String getDescription()
	{
		return "Saves pages to disk, essentially mirroring the targeted website. Requests used only for test purposes are not saved. Right now, only the first request for a file is saved.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.ARCHITECTURE;
	}

	@Override
	public String getName()
	{
		return "Website mirror";
	}

	@Override
	public String[] getResponseCodes()
	{
		return new String[] { "200" };
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}


	@Override
	public void testByHttpResponseCode(int transactionID, int testJobId)
	{
		generateFinding();
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		if (recordAllHTMLFilesOption.isSelected()
				|| MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			saveToDisk(transaction);
		}
	}

	private String getFileName(StandardHttpTransaction transaction)
	{
		String path = baseDir;
		String filename;
		
		path +=
				transaction.getRequestWrapper().getHost() + "-" + transaction.getRequestWrapper().getNetworkPort()
						+ File.separator;

		try
		{
			for (String dir : URIStringUtils.getDirectory(transaction.getRequestWrapper().getURI()).split("/"))
			{
				if (dir.length() > 0)
				{
					path += dir + File.separator;
				}
			}
			FileUtils.createDirectories(path);
			String originalFilename = URIStringUtils.getFilename(transaction.getRequestWrapper().getURI());
			if (originalFilename.equals(""))
			{
				originalFilename = "_ROOT_";
			}
			filename = originalFilename;
			if (recordQueriesOption.isSelected()
					&& (URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()).length() > 0))
			{
				String query = URIStringUtils.getQuery(transaction.getRequestWrapper().getURI());
				query = query.replace("?", "_QUESTION_");
				query = query.replace("/", "_FSLASH_");
				query = query.replace("\\", "_BSLASH_");
				query = query.replace("<", "_LT_");
				query = query.replace(">", "_GT_");
				query = query.replace(":", "_COL_");
				query = query.replace("*", "_STAR_");
				query = query.replace("|", "_PIPE_");
				query = query.replace("\"", "_DQUOTE_");
				query = query.replace("^", "_CARET_");
				query = query.replace("|", "_PIPE_");
				query = query.replace("|", "_PIPE_");
				query = query.replace("|", "_PIPE_");
				filename += "_QUESTION_" + query;
			}
			// fix the length before the extension is added
			int endIndex = filename.length() > 250 ? 250 : filename.length();
			filename = filename.substring(0, endIndex);

			String extension = MimeUtils.getFileExtension(transaction.getResponseWrapper().getHeaders().getMimeType());
			if (!filename.toLowerCase().endsWith(extension))
			{
				filename += extension;
			}
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}

		return path + filename;
	}

	private synchronized void generateFinding()
	{
		if (finding == null)
		{
			try
			{
				int findingNumber = Scan.getInstance().getTestData().getInt(FINDING_NUMBER_NAME);
				finding = Scan.getInstance().getFindings().get(findingNumber);
			}
			catch (DataNotFoundException e1)
			{
				String title = "Website mirror";
				String shortDesc = "A mirror of discovered websites was created.";
				String longDesc =
						"A mirror of discovered websites was created and saved to "
								+ HtmlUtils.makeLink(baseDir) +
								".";
				finding = new Finding(null, getName(), FindingSeverity.INFO, "",
						title, shortDesc, longDesc, "", "", "");
				Scan.getInstance().getFindings().addFinding(finding);
				Scan.getInstance().getTestData().setInt(FINDING_NUMBER_NAME, finding.getId());
			}
		}
	}

	private void saveToDisk(StandardHttpTransaction transaction)
	{
		String fileName = getFileName(transaction);
		if (!FileUtils.fileExists(fileName))
		{
			int endIndex = transaction.getResponseWrapper().getBody().length > maxFileSize.getValue()
							? maxFileSize.getValue() : transaction.getResponseWrapper().getBody().length;
			byte[] body = ArrayUtils.copyOfRange(transaction.getResponseWrapper().getBody(), 0, endIndex);
			FileUtils.writeToFile(fileName, body);
		}
	}
}
