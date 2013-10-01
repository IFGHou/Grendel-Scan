package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.types.ByBaseUriTest;

public class ByBaseUriTestJob extends TestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String baseUri;

    public ByBaseUriTestJob(final Class<? extends AbstractSmasher> moduleClass, final String baseUri)
    {
        super(moduleClass);
        this.baseUri = baseUri;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByBaseUriTest) getModule()).testByBaseUri(baseUri, getId());
    }
}
