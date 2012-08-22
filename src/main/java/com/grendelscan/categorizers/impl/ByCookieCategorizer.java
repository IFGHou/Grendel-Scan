package com.grendelscan.categorizers.impl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.CookieToken;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.BySetCookieTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByCookieTest;

public class ByCookieCategorizer extends ByTokenCategorizer<CookieToken>
{
	public ByCookieCategorizer()
	{
		super(ByCookieTest.class);
	}

	@Override
    protected List<CookieToken> getTokens(StandardHttpTransaction transaction)
    {
		List<CookieToken> tokens = new ArrayList<CookieToken>(1);
		if (!transaction.isLoginTransaction()) // skip it for logins, it is too likely to mess things up
		{
			for (SerializableBasicCookie cookie: transaction.getCookieJar().getCookies())
			{
				tokens.add(new CookieToken(cookie));
			}
		}
	    return tokens;
    }

	@Override
    protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, CookieToken token)
    {
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new BySetCookieTestJob(module.getClass(), transaction.getId(), token.getCookie()));
		return jobs;
   }

}
