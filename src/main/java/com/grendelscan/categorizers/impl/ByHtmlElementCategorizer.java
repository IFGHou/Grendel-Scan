package com.grendelscan.categorizers.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.cobra_grendel.html.domimpl.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.grendelscan.categorizers.MultiSetTransactionCategorizer;
import com.grendelscan.data.database.collections.DatabaseBackedSet;
import com.grendelscan.html.HtmlNodeUtilities;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.ByHtmlElementTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHtmlElementTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.MimeUtils;

public class ByHtmlElementCategorizer extends MultiSetTransactionCategorizer
{
	private final DatabaseBackedSet<String> elementHashes;

	public ByHtmlElementCategorizer()
	{
		super(ByHtmlElementTest.class);
		elementHashes = new DatabaseBackedSet<String>("html_element_categorizer_hashes", 500);
	}

	@Override
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();

		if (MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			Document doc = transaction.getResponseWrapper().getResponseDOM();

			for (Object tagnameObject: modulesByType.keySet())
			{
				String tagName = (String) tagnameObject;
				NodeList nodes = doc.getElementsByTagName(tagName);
				for (int index = 0; index < nodes.getLength(); index++)
				{
					handlePause_isRunning();
					HTMLElementImpl element = (HTMLElementImpl) nodes.item(index).cloneNode(true);
					HtmlUtils.StripAllFamily(element);
					String hash = HtmlNodeUtilities.getNodeHash(element, true);
					if (!elementHashes.contains(hash))
					{
						elementHashes.add(hash);
						for (TestModule module: modulesByType.get(tagName))
						{
							handlePause_isRunning();
							ByHtmlElementTestJob testJob = new ByHtmlElementTestJob(module.getClass(), transaction.getId(), element, tagName);
							addJobToCollection(testJob, module, tests);
						}
					}
				}
			}
		}
		return tests;
	}

	@Override
	public String[] getModuleTypes(TestModule module)
	{
		ByHtmlElementTest test = (ByHtmlElementTest) module;
		return test.getHtmlElements();
	}

	@SuppressWarnings("unused")
	@Override
	/**
	 * This isn't used by ByHtmlElementCategorizer because it implements its own analyze transaction
	 */
	protected Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction)
	{
		throw new NotImplementedException();
	}

	@SuppressWarnings("unused")
	@Override
	/**
	 * This isn't used by ByHtmlElementCategorizer because it implements its own analyze transaction
	 */
	protected TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, String type)
	{
		throw new NotImplementedException();
	}
}
