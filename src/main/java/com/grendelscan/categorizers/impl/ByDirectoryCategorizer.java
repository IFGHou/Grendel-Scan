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
import com.grendelscan.tests.testJobs.ByDirectoryTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByDirectoryTest;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * 
 * @author Administrator
 */
public class ByDirectoryCategorizer extends ByTokenCategorizer<StringToken>
{
//	private Set<String> testedDirs;
	public ByDirectoryCategorizer()
	{
		super(ByDirectoryTest.class);
//		testedDirs = new HashSet<String>();
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, StringToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByDirectoryTestJob(module.getClass(), transaction.getId(), token.getToken()));
		
		return jobs;
	}
	
	@Override
	protected List<StringToken> getTokens(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		List<StringToken> tokens = new ArrayList<StringToken>(1);
		int responseCode = transaction.getLogicalResponseCode();
		String path;
		try
		{
			path = URIStringUtils.getDirectory(transaction.getRequestWrapper().getURI());
			if (HttpUtils.fileExists(responseCode) || path.equals("/"))
			{
				for (String uri: URIStringUtils.getAllDirectoryURIs(transaction.getRequestWrapper().getAbsoluteUriString()))
				{
					tokens.add(new StringToken(uri));
				}
//				int lastSlashIndex = -1;
//				int slashIndex;
//				while ((slashIndex = path.indexOf("/", lastSlashIndex + 1)) >= 0)
//				{
//					if (!(slashIndex - lastSlashIndex == 1 && lastSlashIndex > 0))
//					{
//						String directory = path.substring(0, slashIndex + 1);
//						String testUri = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString()) + directory;
////						if (!testedDirs.contains(testUri))
////						{
////							testedDirs.add(testUri);
//		                    tokens.add(new StringToken(testUri));
////						}
//					}
//					else
//					{
//						Log.debug("odd URL");
//					}
//					lastSlashIndex = slashIndex;
//				}
			}
			return tokens;
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
	}

}
