package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.SingleSetCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.FinalAnalysisTestJob;
import com.grendelscan.smashers.types.FinalAnalysisTest;

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
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();

        for (AbstractSmasher module : testModules)
        {
            FinalAnalysisTestJob testJob = new FinalAnalysisTestJob(module.getClass());
            addJobToCollection(testJob, module, tests);
        }
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }

}
