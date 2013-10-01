/**
 * 
 */
package com.grendelscan.smashers.jobs;

import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByQueryNamedDataTest;

/**
 * @author david
 * 
 */
public class ByQueryNamedDataTestJob extends TransactionTestJob
{

    private static final long serialVersionUID = 1L;
    private final NameValuePairDataContainer datum;

    /**
     * @param moduleClass
     * @param transactionID
     */
    public ByQueryNamedDataTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final NameValuePairDataContainer datum)
    {
        super(moduleClass, transactionID);
        this.datum = datum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.tests.testJobs.TestJob#internalRunTest()
     */
    @Override
    protected void internalRunTest() throws InterruptedScanException
    {
        ((ByQueryNamedDataTest) getModule()).testByQueryNamedData(getTransactionID(), datum, getId());
    }

}
