package com.grendelscan.categorizers;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.TestType;
public abstract class Categorizer
{
	public static String categoryName;
	protected final Class<? extends TestType> categoryTestClass;
	
	public Categorizer(Class<? extends TestType> categoryTestClass)
	{
		this.categoryTestClass = categoryTestClass;
	}

	protected void addJobToCollection(TestJob testJob, TestModule module, Map<TestModule, Set<TestJob>> tests) 
	{
		if (!tests.containsKey(module))
		{
			Set<TestJob> testSet = new HashSet<TestJob>();
			tests.put(module, testSet);
		}
		tests.get(module).add(testJob);
	}
	
	protected void handlePause_isRunning() throws InterruptedScanException
	{
		Scan.getInstance().getCategorizerQueue().handlePause_isRunning();
	}
	
	public abstract void resolveDependencies();

	public abstract void addModule(TestModule module);

}
