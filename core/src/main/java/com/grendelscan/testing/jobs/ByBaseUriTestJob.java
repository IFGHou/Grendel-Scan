package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByBaseUriTest;

public class ByBaseUriTestJob extends TestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String baseUri;

    public ByBaseUriTestJob(final Class<? extends AbstractTestModule> moduleClass, final String baseUri)
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
