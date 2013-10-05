package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.tokens.CookieToken;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.BySetCookieTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByCookieTest;

public class ByCookieCategorizer extends ByTokenCategorizer<CookieToken>
{
    public ByCookieCategorizer()
    {
        super(ByCookieTest.class);
    }

    @Override
    protected List<CookieToken> getTokens(final StandardHttpTransaction transaction)
    {
        List<CookieToken> tokens = new ArrayList<CookieToken>(1);
        if (!transaction.isLoginTransaction()) // skip it for logins, it is too likely to mess things up
        {
            for (SerializableBasicCookie cookie : transaction.getCookieJar().getCookies())
            {
                tokens.add(new CookieToken(cookie));
            }
        }
        return tokens;
    }

    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, final CookieToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        jobs.add(new BySetCookieTestJob(module.getClass(), transaction.getId(), token.getCookie()));
        return jobs;
    }

}
