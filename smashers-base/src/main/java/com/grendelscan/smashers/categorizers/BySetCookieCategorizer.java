package com.grendelscan.smashers.categorizers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.BySetCookieTestJob;
import com.grendelscan.smashers.types.BySetCookieTest;

public class BySetCookieCategorizer extends ByTokenCategorizer<CookieToken>
{
    public BySetCookieCategorizer()
    {
        super(BySetCookieTest.class);
    }

    @Override
    protected List<CookieToken> getTokens(final StandardHttpTransaction transaction)
    {
        List<CookieToken> tokens = new ArrayList<CookieToken>(1);
        for (SerializableBasicCookie cookie : HttpUtils.getSetCookies(transaction))
        {
            tokens.add(new CookieToken(cookie));
        }
        return tokens;
    }

    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractSmasher module, final CookieToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        jobs.add(new BySetCookieTestJob(module.getClass(), transaction.getId(), token.getCookie()));
        return jobs;
    }

}
