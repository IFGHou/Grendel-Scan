package com.grendelscan.testing.jobs;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByRepeatableOutputContextTest;
import com.grendelscan.testing.utils.tokens.TokenContext;

public class ByRepeatableOutputContextTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Collection<TokenContext> contexts;

    public ByRepeatableOutputContextTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final Collection<TokenContext> contexts)
    {
        super(moduleClass, transactionID);
        this.contexts = contexts;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByRepeatableOutputContextTest) getModule()).testByRepeatableOutputContext(contexts, getId());
    }
}
