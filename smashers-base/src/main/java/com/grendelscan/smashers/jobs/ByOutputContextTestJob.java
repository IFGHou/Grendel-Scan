package com.grendelscan.smashers.jobs;

import java.io.Serializable;
import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByOutputContextTest;
import com.grendelscan.smashers.utils.tokens.TokenContext;

public class ByOutputContextTestJob extends TransactionTestJob implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Collection<TokenContext> contexts;

    public ByOutputContextTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final Collection<TokenContext> contexts)
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
