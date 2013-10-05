package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.categorizers.interfaces.MultiSetCategorizer;
import com.grendelscan.categorizers.interfaces.TransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.ByOutputContextTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.TestType;
import com.grendelscan.testing.modules.types.ByOutputContextTest;
import com.grendelscan.testing.utils.tokens.DiscoveredContexts;
import com.grendelscan.testing.utils.tokens.TokenContext;
import com.grendelscan.testing.utils.tokens.TokenContextType;
import com.grendelscan.testing.utils.tokens.TokenTesting;

public class ByOutputContextCategorizer extends MultiSetCategorizer implements TransactionCategorizer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ByOutputContextCategorizer.class);
    final private Pattern tokenPattern;

    public ByOutputContextCategorizer()
    {
        this(ByOutputContextTest.class);
    }

    protected ByOutputContextCategorizer(final Class<? extends TestType> categoryTestClass)
    {
        super(categoryTestClass);
        String tokenPrefix = TokenTesting.getInstance().getTokenPrefix();
        int tokenSuffixLength = TokenTesting.getInstance().getTokenSuffixLength();
        tokenPattern = Pattern.compile("(" + tokenPrefix + "[A-Z]{" + tokenSuffixLength + "})", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Map<AbstractTestModule, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();
        DiscoveredContexts contexts = TokenTesting.getInstance().findTokenContexts(transaction);

        if (contexts.getCount() > 0)
        {
            handlePause_isRunning();
            createJobs(contexts, transaction.getId(), tests);

            handlePause_isRunning();
            findRepeatableContexts(contexts, transaction);
        }
        return tests;
    }

    protected void createJobs(final DiscoveredContexts contexts, final int transactionId, final Map<AbstractTestModule, Set<TestJob>> tests)
    {
        for (String token : contexts.getAllTokens())
        {
            DiscoveredContexts tokenContexts = contexts.getAllOfToken(token);
            Map<AbstractTestModule, Set<TokenContext>> contextLists = new HashMap<AbstractTestModule, Set<TokenContext>>();
            for (TokenContextType type : tokenContexts.getAllTypes())
            {
                if (modulesByType.containsKey(type))
                {
                    for (AbstractTestModule module : modulesByType.get(type))
                    {
                        if (!contextLists.containsKey(module))
                        {
                            contextLists.put(module, new HashSet<TokenContext>());
                        }

                        for (TokenContext context : tokenContexts.getAllOfType(type).getAllContexts())
                        {
                            if (context.isSingleTransaction())
                            {
                                contextLists.get(module).add(context);
                            }
                            else
                            {
                                List<TokenContext> tmpList = new ArrayList<TokenContext>();
                                tmpList.add(context);
                                addJobToCollection(createTestJob(module.getClass(), transactionId, tmpList), module, tests);
                            }
                        }
                    }
                }
            }

            for (AbstractTestModule module : contextLists.keySet())
            {
                if (contextLists.get(module).size() > 0)
                {
                    TestJob testJob = createTestJob(module.getClass(), transactionId, contextLists.get(module));
                    addJobToCollection(testJob, module, tests);
                }
            }
        }
    }

    protected TestJob createTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionId, final Collection<TokenContext> contexts)
    {
        return new ByOutputContextTestJob(moduleClass, transactionId, contexts);
    }

    private void findRepeatableContexts(final DiscoveredContexts contexts, final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        for (String token : contexts.getAllTokens())
        {
            DiscoveredContexts repeatableContexts = new DiscoveredContexts();
            boolean addAll = false;
            for (TokenContext context : contexts.getAllOfToken(token).getAllContexts())
            {
                try
                {
                    if (addAll && context.isSingleTransaction())
                    {
                        repeatableContexts.addContext(context);
                    }
                    // Need to track transaction from categorizers for session redo better
                    else if (TokenTesting.getInstance().verifyTokenRepeatability(context, "ByOutputContextCategorizer", TransactionSource.CATEGORIZER, -1))
                    {
                        if (context.getOutputTransactionID() == context.getOriginatingTransactionID() && context.isSingleTransaction())
                        {
                            addAll = true; // only look for one repeat
                        }
                        repeatableContexts.addContext(context);
                    }
                }
                catch (UnrequestableTransaction e)
                {
                    LOGGER.warn("Token repeatability request unrequestable: " + e.toString());
                }
            }
            Scan.getInstance().getCategorizers().getByRepeatableOutputContextCategorizer().analyzeRepeatableOutputContexts(repeatableContexts, transaction.getId());
        }
    }

    @Override
    public TokenContextType[] getModuleTypes(final AbstractTestModule module)
    {
        return ((ByOutputContextTest) module).getDesiredContexts();
    }

    protected Set<String> getTestTokens(final StandardHttpTransaction transaction)
    {
        Set<String> tokens = new HashSet<String>();
        String body = new String(transaction.getResponseWrapper().getBody());
        Matcher m = tokenPattern.matcher(body);
        while (m.find())
        {
            tokens.add(m.group(1));
        }
        return tokens;
    }

}
