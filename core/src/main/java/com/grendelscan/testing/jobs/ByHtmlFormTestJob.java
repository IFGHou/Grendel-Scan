/**
 * 
 */
package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHtmlFormTest;

/**
 * @author david
 * 
 */
public class ByHtmlFormTestJob extends TransactionTestJob
{
    private static final long serialVersionUID = 1L;
    private final String hash;

    /**
     * @param moduleClass
     * @param transactionID
     */
    public ByHtmlFormTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final String hash)
    {
        super(moduleClass, transactionID);
        this.hash = hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.tests.testJobs.TestJob#internalRunTest()
     */
    @Override
    protected void internalRunTest() throws InterruptedScanException
    {
        ((ByHtmlFormTest) getModule()).testByHtmlForm(transactionID, hash, getId());
    }

}
