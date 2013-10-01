package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.types.FinalAnalysisTest;

public class FinalAnalysisTestJob extends TestJob
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public FinalAnalysisTestJob(final Class<? extends AbstractSmasher> moduleClass)
    {
        super(moduleClass);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((FinalAnalysisTest) getModule()).runAnalysis(getId());
    }

}
