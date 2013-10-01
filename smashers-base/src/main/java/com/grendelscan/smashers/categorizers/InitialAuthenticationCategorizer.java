package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.SingleSetCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.InitialAuthenticationTestJob;
import com.grendelscan.smashers.types.InitialAuthenticationTest;
import com.grendelscan.smashers.utils.sessionIDs.SessionIDLocation;

/**
 * 
 * @author David Byrne
 */
public class InitialAuthenticationCategorizer extends SingleSetCategorizer
{

    /** Creates a new instance of InitialAuthenticationCategorizer */
    public InitialAuthenticationCategorizer()
    {
        super(InitialAuthenticationTest.class);
    }

    public void analyzeAuthentication(final StandardHttpTransaction transaction, final SessionIDLocation sessionIDLocation)
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();
        for (AbstractSmasher module : testModules)
        {
            InitialAuthenticationTestJob testJob = new InitialAuthenticationTestJob(module.getClass(), transaction.getId(), sessionIDLocation);
            addJobToCollection(testJob, module, tests);
        }
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }
}
