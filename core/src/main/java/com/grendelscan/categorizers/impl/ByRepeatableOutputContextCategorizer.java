package com.grendelscan.categorizers.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.ByRepeatableOutputContextTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByRepeatableOutputContextTest;
import com.grendelscan.testing.utils.tokens.DiscoveredContexts;
import com.grendelscan.testing.utils.tokens.TokenContext;
import com.grendelscan.testing.utils.tokens.TokenContextType;

public class ByRepeatableOutputContextCategorizer extends ByOutputContextCategorizer
{

    public ByRepeatableOutputContextCategorizer()
    {
        super(ByRepeatableOutputContextTest.class);

    }

    public void analyzeRepeatableOutputContexts(final DiscoveredContexts contexts, final int transactionID)
    {
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();
        createJobs(contexts, transactionID, tests);
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }

    @Override
    protected TestJob createTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionId, final Collection<TokenContext> contexts)
    {
        return new ByRepeatableOutputContextTestJob(moduleClass, transactionId, contexts);
    }

    @Override
    public TokenContextType[] getModuleTypes(final AbstractTestModule module)
    {
        return ((ByRepeatableOutputContextTest) module).getDesiredRepeatableContexts();
    }

}
