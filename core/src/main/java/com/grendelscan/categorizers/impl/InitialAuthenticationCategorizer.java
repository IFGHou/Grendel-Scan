package com.grendelscan.categorizers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.SingleSetCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.InitialAuthenticationTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.InitialAuthenticationTest;
import com.grendelscan.testing.utils.sessionIDs.SessionIDLocation;

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
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();
        for (AbstractTestModule module : testModules)
        {
            InitialAuthenticationTestJob testJob = new InitialAuthenticationTestJob(module.getClass(), transaction.getId(), sessionIDLocation);
            addJobToCollection(testJob, module, tests);
        }
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }
}
