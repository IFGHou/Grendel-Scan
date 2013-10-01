package com.grendelscan.smashers.categorizers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByRepeatableOutputContextTestJob;
import com.grendelscan.smashers.types.ByRepeatableOutputContextTest;
import com.grendelscan.smashers.utils.tokens.DiscoveredContexts;
import com.grendelscan.smashers.utils.tokens.TokenContext;
import com.grendelscan.smashers.utils.tokens.TokenContextType;

public class ByRepeatableOutputContextCategorizer extends ByOutputContextCategorizer
{

    public ByRepeatableOutputContextCategorizer()
    {
        super(ByRepeatableOutputContextTest.class);

    }

    public void analyzeRepeatableOutputContexts(final DiscoveredContexts contexts, final int transactionID)
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();
        createJobs(contexts, transactionID, tests);
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }

    @Override
    protected TestJob createTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionId, final Collection<TokenContext> contexts)
    {
        return new ByRepeatableOutputContextTestJob(moduleClass, transactionId, contexts);
    }

    @Override
    public TokenContextType[] getModuleTypes(final AbstractSmasher module)
    {
        return ((ByRepeatableOutputContextTest) module).getDesiredRepeatableContexts();
    }

}
