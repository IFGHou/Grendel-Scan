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
import com.grendelscan.testing.jobs.ByDirectoryTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByDirectoryTest;

/**
 * 
 * @author Administrator
 */
public class ByDirectoryCategorizer extends ByTokenCategorizer<StringToken>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ByDirectoryCategorizer.class);

    // private Set<String> testedDirs;
    public ByDirectoryCategorizer()
    {
        super(ByDirectoryTest.class);
        // testedDirs = new HashSet<String>();
    }

    @Override
    protected List<StringToken> getTokens(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        List<StringToken> tokens = new ArrayList<StringToken>(1);
        int responseCode = transaction.getLogicalResponseCode();
        String path;
        try
        {
            path = URIStringUtils.getDirectory(transaction.getRequestWrapper().getURI());
            if (HttpUtils.fileExists(responseCode) || path.equals("/"))
            {
                for (String uri : URIStringUtils.getAllDirectoryURIs(transaction.getRequestWrapper().getAbsoluteUriString()))
                {
                    tokens.add(new StringToken(uri));
                }
                // int lastSlashIndex = -1;
                // int slashIndex;
                // while ((slashIndex = path.indexOf("/", lastSlashIndex + 1)) >= 0)
                // {
                // if (!(slashIndex - lastSlashIndex == 1 && lastSlashIndex > 0))
                // {
                // String directory = path.substring(0, slashIndex + 1);
                // String testUri = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString()) + directory;
                // // if (!testedDirs.contains(testUri))
                // // {
                // // testedDirs.add(testUri);
                // tokens.add(new StringToken(testUri));
                // // }
                // }
                // else
                // {
                // LOGGER.debug("odd URL");
                // }
                // lastSlashIndex = slashIndex;
                // }
            }
            return tokens;
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }
    }

    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, final StringToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        jobs.add(new ByDirectoryTestJob(module.getClass(), transaction.getId(), token.getToken()));

        return jobs;
    }

}
