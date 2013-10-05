package com.grendelscan.categorizers.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.categorizers.tokens.StringToken;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.ByFileTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByFileTest;

/**
 * 
 * @author David Byrne
 */
public class ByFileCategorizer extends ByTokenCategorizer<StringToken>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ByFileCategorizer.class);

    public ByFileCategorizer()
    {
        super(ByFileTest.class);
    }

    @Override
    protected List<StringToken> getTokens(final StandardHttpTransaction transaction) throws InterruptedScanException
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
                LOGGER.error(e.toString(), e);
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
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, @SuppressWarnings("unused") final StringToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        jobs.add(new ByFileTestJob(module.getClass(), transaction.getId()));
        return jobs;
    }

}
