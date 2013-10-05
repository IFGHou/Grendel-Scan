package com.grendelscan.categorizers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.SingleSetCategorizer;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.FinalAnalysisTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.FinalAnalysisTest;

/**
 * 
 * @author David Byrne
 */
public class FinalAnalysisCategorizer extends SingleSetCategorizer
{

    /** Creates a new instance of AllTransactionsCategorizer */
    public FinalAnalysisCategorizer()
    {
        super(FinalAnalysisTest.class);
    }

    public void runAnalysis()
    {
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();

        for (AbstractTestModule module : testModules)
        {
            FinalAnalysisTestJob testJob = new FinalAnalysisTestJob(module.getClass());
            addJobToCollection(testJob, module, tests);
        }
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }

}
