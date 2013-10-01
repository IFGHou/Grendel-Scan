package com.grendelscan.categorizers.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.StringToken;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.ByFileTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByFileTest;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * 
 * @author David Byrne
 */
public class ByFileCategorizer extends ByTokenCategorizer<StringToken>
{
	public ByFileCategorizer()
	{
		super(ByFileTest.class);
	}

	@Override
	protected List<StringToken> getTokens(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		List<StringToken> tokens = new ArrayList<StringToken>(1);

		if (HttpUtils.fileExists(transaction.getLogicalResponseCode()))
		{
			String filename;
			try
			{
				filename = transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI());
			}
			catch (URISyntaxException e)
			{
				IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
				Log.error(e.toString(), e);
				throw ise;
			}
			if (!filename.isEmpty())
			{
				tokens.add(new StringToken(filename));
			}
		}
		
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, @SuppressWarnings("unused") StringToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByFileTestJob(module.getClass(), transaction.getId()));
		return jobs;
	}

}
