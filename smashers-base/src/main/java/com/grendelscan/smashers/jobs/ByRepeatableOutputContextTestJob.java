package com.grendelscan.smashers.jobs;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByRepeatableOutputContextTest;
import com.grendelscan.smashers.utils.tokens.TokenContext;

public class ByRepeatableOutputContextTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Collection<TokenContext> contexts;

    public ByRepeatableOutputContextTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final Collection<TokenContext> contexts)
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
