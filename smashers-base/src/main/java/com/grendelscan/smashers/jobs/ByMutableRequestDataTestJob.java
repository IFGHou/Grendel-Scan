package com.grendelscan.smashers.jobs;

import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByRequestDataLocationTest;

public class ByMutableRequestDataTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    // private ByteData datum;
    private final DataReferenceChain chain;

    // public ByMutableRequestDataTestJob(Class<? extends AbstractSmasher> moduleClass, int transactionID, ByteData datum)
    public ByMutableRequestDataTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final DataReferenceChain chain)
    {
        super(moduleClass, transactionID);
        this.chain = chain;
        // this.datum = datum;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        // ((ByRequestDataTest) getModule()).testByRequestData(getTransactionID(), datum, this.getId());
        ((ByRequestDataLocationTest) getModule()).testByRequestData(getTransactionID(), chain, getId());
    }
}
