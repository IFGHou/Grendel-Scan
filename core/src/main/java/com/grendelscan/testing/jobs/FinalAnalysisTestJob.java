package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.FinalAnalysisTest;

public class FinalAnalysisTestJob extends TestJob
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public FinalAnalysisTestJob(final Class<? extends AbstractTestModule> moduleClass)
    {
        super(moduleClass);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((FinalAnalysisTest) getModule()).runAnalysis(getId());
    }

}
