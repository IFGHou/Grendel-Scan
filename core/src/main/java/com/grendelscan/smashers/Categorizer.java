package com.grendelscan.smashers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

public abstract class Categorizer
{
    public static String categoryName;
    protected final Class<? extends TestType> categoryTestClass;

    public Categorizer(final Class<? extends TestType> categoryTestClass)
    {
        this.categoryTestClass = categoryTestClass;
    }

    protected void addJobToCollection(final TestJob testJob, final AbstractSmasher module, final Map<AbstractSmasher, Set<TestJob>> tests)
    {
        if (!tests.containsKey(module))
        {
            Set<TestJob> testSet = new HashSet<TestJob>();
            tests.put(module, testSet);
        }
        tests.get(module).add(testJob);
    }

    public abstract void addModule(AbstractSmasher module);

    protected void handlePause_isRunning() throws InterruptedScanException
    {
        Scan.getInstance().getCategorizerQueue().handlePause_isRunning();
    }

    public abstract void resolveDependencies();

}
