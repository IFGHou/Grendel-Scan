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

import com.grendelscan.categorizers.MultiSetCategorizer;
import com.grendelscan.categorizers.TransactionCategorizer;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.DiscoveredContexts;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;
import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
import com.grendelscan.tests.testJobs.ByOutputContextTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByOutputContextTest;
import com.grendelscan.tests.testTypes.TestType;

public class ByOutputContextCategorizer extends MultiSetCategorizer implements TransactionCategorizer
{
    protected Set<String> getTestTokens(StandardHttpTransaction transaction)
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


	final private Pattern tokenPattern;

	protected ByOutputContextCategorizer(Class<? extends TestType> categoryTestClass)
	{
		super(categoryTestClass);
		String tokenPrefix = TokenTesting.getInstance().getTokenPrefix();
		int tokenSuffixLength = TokenTesting.getInstance().getTokenSuffixLength();
		tokenPattern = Pattern.compile("(" + tokenPrefix + "[A-Z]{" + tokenSuffixLength + "})", Pattern.CASE_INSENSITIVE);
	}

	public ByOutputContextCategorizer()
	{
		this(ByOutputContextTest.class);
	}

	@Override
	public TokenContextType[] getModuleTypes(TestModule module)
	{
		return ((ByOutputContextTest) module).getDesiredContexts();
	}

	protected void createJobs(DiscoveredContexts contexts, int transactionId, Map<TestModule, Set<TestJob>> tests)
	{
		for (String token: contexts.getAllTokens())
		{
			DiscoveredContexts tokenContexts = contexts.getAllOfToken(token);
			Map<TestModule, Set<TokenContext>> contextLists = new HashMap<TestModule, Set<TokenContext>>();
			for(TokenContextType type: tokenContexts.getAllTypes())
			{
				if (modulesByType.containsKey(type))
				{
					for(TestModule module: modulesByType.get(type))
					{
						if (!contextLists.containsKey(module))
						{
							contextLists.put(module, new HashSet<TokenContext>());
						}
	
						for(TokenContext context: tokenContexts.getAllOfType(type).getAllContexts())
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
			
			for (TestModule module: contextLists.keySet())
			{
				if (contextLists.get(module).size() > 0)
				{
					TestJob testJob = createTestJob(module.getClass(), transactionId, contextLists.get(module));
					addJobToCollection(testJob, module, tests);
				}
			}
		}
	}
	
	protected TestJob createTestJob(Class<? extends TestModule> moduleClass, int transactionId, Collection<TokenContext> contexts)
	{
		return new ByOutputContextTestJob(moduleClass, transactionId, contexts);
	}

	private void findRepeatableContexts(DiscoveredContexts contexts, StandardHttpTransaction transaction) throws InterruptedScanException
	{
		for (String token: contexts.getAllTokens())
		{
			DiscoveredContexts repeatableContexts = new DiscoveredContexts();
			boolean addAll = false;
			for(TokenContext context: contexts.getAllOfToken(token).getAllContexts())
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
						if (context.getOutputTransactionID() == context.getOriginatingTransactionID()
								 && context.isSingleTransaction())
						{
							addAll = true; // only look for one repeat
						}
						repeatableContexts.addContext(context);
					}
				}
				catch (UnrequestableTransaction e)
				{
					Log.warn("Token repeatability request unrequestable: " + e.toString());
				}
			}
			Scan.getInstance().getCategorizers().getByRepeatableOutputContextCategorizer().analyzeRepeatableOutputContexts(repeatableContexts, transaction.getId());
		}
	}
	
	@Override
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();
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

	
}
