package com.grendelscan.testing.jobs;

import java.io.Serializable;
import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByOutputContextTest;
import com.grendelscan.testing.utils.tokens.TokenContext;

public class ByOutputContextTestJob extends TransactionTestJob implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Collection<TokenContext> contexts;

    public ByOutputContextTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final Collection<TokenContext> contexts)
    {
        super(moduleClass, transactionID);
        this.contexts = contexts;
        if (contexts == null || contexts.size() == 0)
        {
            throw new IllegalArgumentException("Contexts needs a value");
        }
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByOutputContextTest) getModule()).testByOutputContext(contexts, getId());
    }
}
