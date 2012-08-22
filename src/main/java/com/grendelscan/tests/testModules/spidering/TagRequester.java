package com.grendelscan.tests.testModules.spidering;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.factories.UriFactory;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.spidering.SpiderConfig;
import com.grendelscan.tests.libraries.spidering.SpiderUtils;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModuleUtils.settings.ConfigChangeHandler;
import com.grendelscan.tests.testModuleUtils.settings.MultiSelectOptionGroup;
import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHtmlElementTest;
import com.grendelscan.utils.URIStringUtils;
import com.grendelscan.utils.collections.CollectionUtils;

public class TagRequester extends TestModule implements ByHtmlElementTest
{
	private SelectableOption			allDirectoriesOption;
	private Set<String>					tagNames;
	// private Map<String, SelectableOption> tagOptions;
	private MultiSelectOptionGroup		tagOptionsGroup;

	/** Creates a new instance of Module00001 */
	public TagRequester()
	{
		requestOptions.testTransaction = true;

		tagNames = new HashSet<String>(1);
		// tagOptions = new HashMap<String, SelectableOption>(1);
		addConfigurationOption(SpiderConfig.ignoredParameters);
		addConfigurationOption(SpiderConfig.spiderStyle);
		allDirectoriesOption =
				new SelectableOption(
						"Request all directories",
						true,
						"This will request the directory of all the URLS discovered, but the full URL will only be requested if the tag is selected below.", null);
		addConfigurationOption(allDirectoriesOption);

		SelectableOption tagOption;
		tagOptionsGroup =
				new MultiSelectOptionGroup("Tag selection", "Select which tags this module should search in.", null);
		addConfigurationOption(tagOptionsGroup);

		ConfigChangeHandler changeHandler = new ConfigChangeHandler()
		{
			@Override
			public void handleChange()
			{
				initTagNames();
			}
		};
		
		tagOption = new SelectableOption("A", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("APPLET", false, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("AREA", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("BASE", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("EMBED", false, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("FRAME", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("IFRAME", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("LINK", false, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("SCRIPT", true, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);

		tagOption = new SelectableOption("IMG", false, "", changeHandler);
		tagOptionsGroup.addOption(tagOption);
		initTagNames();
	}

	@Override
	public String getDescription()
	{
		return "Can request the SRC or HREF attribute for the A, APPLET, " +
				"AREA, BASE, EMBED, FRAME, IFRAME, IMG, LINK, and SCRIPT " +
				"tags. This module carries some risk, especially with " +
				"the A tag. For example, if there is a link that deletes " +
				"content, this module won't know that it's dangerous. " +
				"The risk can be mitigated by using the URL blacklist " +
				"feature. Note that the HTTP standard states that GET " +
				"should only be used for requests that will not modify " +
				"data. That doesn't mean that all applications comply. " +
				"If you aren't familiar with the application, you should " +
				"probably disable the A tag.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.SPIDER;
	}

	@Override
	public String[] getHtmlElements()
	{
		String tags[];
		if (allDirectoriesOption.isSelected())
		{
			tags = new String[] { "APPLET", "EMBED", "FRAME", "IFRAME", "SCRIPT", "IMG",
					"A", "AREA", "BASE", "LINK" };
		}
		else
		{
			synchronized(tagNames)
			{
				tags = CollectionUtils.toStringArray(tagNames);
			}
		}
		return tags;
	}

	@Override
	public String getName()
	{
		return "HTML tag requester";
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}

	// String[] r = { "A", "AREA", "BASE", "LINK" };
	//
	// @Override
	// public String[] getHtmlElements()
	// {
	// String tags[];
	// if (allDirectoriesOption.isSelected())
	// {
	// tags = new String[] { "APPLET", "EMBED", "FRAME", "IFRAME", "SCRIPT",
	// "IMG" };

	@Override
	public void testByHtmlElement(int transactionID, HTMLElement element, String elementType, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		URI uri;
		try
		{
			uri = getUri(element);
			if (uri != null)
			{
				handlePause_isRunning();
				boolean testTag;
				synchronized(tagNames)
				{
					testTag = tagNames.contains(element.getTagName().toUpperCase()); 
				}
				if (testTag && SpiderUtils.getInstance().isUrlSpiderable(uri.toASCIIString(), true))
				{
					StandardHttpTransaction newTransaction = transaction.cloneForReferer(TransactionSource.SPIDER, testJobId);
					newTransaction.getRequestWrapper().setURI(uri.toASCIIString(), true);
					newTransaction.setRequestOptions(requestOptions);
					Scan.getInstance().getRequesterQueue().addTransaction(newTransaction);
				}
				/*
				 * We don't need to request the dir if the URI was requested; the
				 * byDir categorizer will take care of it for us
				 */
				else if (allDirectoriesOption.isSelected())
				{
					String directoryUri = URIStringUtils.getDirectoryUri(uri.toASCIIString());
					if (SpiderUtils.getInstance().isUrlSpiderable(directoryUri, true))
					{
						StandardHttpTransaction newTransaction = transaction.cloneForReferer(TransactionSource.SPIDER, testJobId);
						newTransaction.getRequestWrapper().setURI(directoryUri, true);
						newTransaction.setRequestOptions(requestOptions);
						Scan.getInstance().getRequesterQueue().addTransaction(newTransaction);
					}
					else
					{
						Log.debug("Didn't request " + directoryUri + ", it isn't spiderable");
					}
				}
			}
		}
		catch (URISyntaxException e)
		{
			Log.warn("URI in HTML element not valid (" + element.toString() + "): " + e.toString(), e);
		}
	}

	private URI getUri(HTMLElement element) throws URISyntaxException
	{
		String tag = element.getTagName().toUpperCase();
		String rawUri;
		if (tag.equals("APPLET") || tag.equals("EMBED") || tag.equals("FRAME") ||
				tag.equals("IFRAME") || tag.equals("SCRIPT") || tag.equals("IMG"))
		{
			rawUri = URIStringUtils.cleanupWhitespace(element.getAttribute("src"));
		}
		else
		// Assume that it's an HREF-type tag
		{
			rawUri = URIStringUtils.cleanupWhitespace(element.getAttribute("href"));
		}
		URI uri = null;
		if (URIStringUtils.isUsableUri(rawUri) || false)
		{
			try
			{
				uri = UriFactory.makeAbsoluteUri(rawUri, element.getBaseURI());
			}
			catch (URISyntaxException e)
			{
				// Don't really care
			}
		}
		return uri;
	}

	
	/**
	 * Intentionally default visibility
	 */
	void initTagNames()
	{
		synchronized(tagNames)
		{
			tagNames.clear();
			for (SelectableOption option : tagOptionsGroup.getAllOptions())
			{
				if (option.isSelected())
				{
					tagNames.add(option.getName());
				}
			}
		}
	}
}
