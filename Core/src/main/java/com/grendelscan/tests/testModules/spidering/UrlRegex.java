/*
 * 
 * Created on September 15, 2007, 10:49 PM
 */

package com.grendelscan.tests.testModules.spidering;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.spidering.SpiderConfig;
import com.grendelscan.tests.libraries.spidering.SpiderUtils;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.dataFormating.DataFormatException;
import com.grendelscan.utils.dataFormating.DataFormatType;
import com.grendelscan.utils.dataFormating.DataFormatUtils;

/**
 * 
 * @author David Byrne
 */

public class UrlRegex extends TestModule implements ByHttpResponseCodeTest
{

	private Pattern	urlPattern;

	public UrlRegex()
	{
		requestOptions.testTransaction = true;
		addConfigurationOption(SpiderConfig.ignoredParameters);
		addConfigurationOption(SpiderConfig.spiderStyle);
	
		urlPattern = Pattern.compile(
				"(https?://[\\-a-z0-9.]+(?::\\d+)?(?:/[^\\x00-\\x20\\x7f\"#<>'\\\\]+))",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	}

	@Override
	public String getDescription()
	{
		return "Searches all responses for simple, full URLs using a regex. " +
				"Enable this module ONLY if you are familiar with the application. " +
				"For example, if there is a link that deletes content, this module " +
				"won't know that it's dangerous. The risk can be mitigated by " +
				"using the URL blacklist feature. Note that the HTTP standard " +
				"states that GET should only be used for requests that will not " +
				"modify data. That doesn't mean that all applications comply.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SPIDER;
	}

	public String getLongName()
	{
		return getName();
	}

	@Override
	public String getName()
	{
		return "URL-Regex";
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
	public void testByHttpResponseCode(int transactionID, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		if (!MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			return;
		}
		String response = new String(transaction.getResponseWrapper().getBody());
		Matcher matcher = urlPattern.matcher(response);

		while (matcher.find())
		{
			handlePause_isRunning();
			String url;
			try
			{
				url = new String(DataFormatUtils.decodeData(matcher.group(1).getBytes(), DataFormatType.HTML_BASIC_ENTITIES));
			}
			catch (DataFormatException e1)
			{
				Log.error("Problem decoding HTML (" + matcher.group(1) + "): " + e1.toString(), e1);
				continue;
			}
			try
			{
				if (SpiderUtils.getInstance().isUrlSpiderable(url, true))
				{
					StandardHttpTransaction spiderTransaction = transaction.cloneForReferer(TransactionSource.SPIDER, testJobId);
					spiderTransaction.getRequestWrapper().setURI(url, true);
					spiderTransaction.setRequestOptions(requestOptions);
					Scan.getInstance().getRequesterQueue().addTransaction(spiderTransaction);
				}
				else
				{
					Log.debug("URL is not requestable: " + url);
				}
			}
			catch (URISyntaxException e)
			{
				Log.warn("Invalid URL discovered with regex (" + url + "): " + e.toString());
			}
		}
	}

}
