/*
 * TesterQueue.java
 * 
 * Created on September 15, 2007, 9:52 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.queues.tester;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.data.database.BulkInsertJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.queues.AbstractQueueThread;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.queues.QueueItem;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;

public class TesterQueue extends AbstractScanQueue
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TesterQueue.class);
    private static final String TEST_DEPENDENCY_TABLE = "test_dependencies";
    private static final String TEST_QUEUE_TABLE = "test_queue";
    private int lastJobGroupNumber = 0;
    private final Map<Class<? extends AbstractSmasher>, Integer> processedCount;
    private final Map<Class<? extends AbstractSmasher>, Integer> times;

    private long nextItemTime;

    private final Object nextItemLock = new Object();

    private long removeTime;

    private final Object removeLock = new Object();

    private long addTime;
    private final Object addLock = new Object();
    private long pendingTime;

    private final Object pendingLock = new Object();

    private long redoTime;

    private final Object redoLock = new Object();

    public TesterQueue()
    {
        super("Tester queue", TEST_QUEUE_TABLE);
        processedCount = new HashMap<Class<? extends AbstractSmasher>, Integer>();
        times = new HashMap<Class<? extends AbstractSmasher>, Integer>();
    }

    private void addDependencies(final TestJob target, final Set<TestJob> dependencies)
    {
        if (dependencies != null)
        {
            for (TestJob dep : dependencies)
            {
                target.addDependency(dep);
            }
        }
    }

    public void addTests(final Set<TestJob> tests)
    {
        long start = new Date().getTime();
        lastJobGroupNumber++;
        try
        {
            BulkInsertJob job = new BulkInsertJob();
            for (TestJob test : tests)
            {
                String query = "INSERT INTO " + TEST_QUEUE_TABLE + " (test_id, group_id, serialized_test_job, module_class, active) VALUES (?, ?, ?, ?, ?)";
                Object[] values = new Object[] { test.getId(), lastJobGroupNumber, test, test.getModuleClass(), isModuleEnabled(test.getModuleClass()) };
                job.addInsert(query, values);

                for (int dependency : test.getDependencies())
                {
                    job.addInsert("INSERT INTO " + TEST_DEPENDENCY_TABLE + " (test_id, dependency) VALUES (" + test.getId() + ", " + dependency + ")", new Object[0]);
                }
            }
            database.execute(job);
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem with adding a queue item: " + e.toString(), e);
        }

        synchronized (addLock)
        {
            addTime += new Date().getTime() - start;
        }

    }

    public synchronized void addTime(final TestJob testJob, final long time)
    {
        int total = 0;
        Class<? extends AbstractSmasher> moduleClass = testJob.getModuleClass();
        if (times.containsKey(moduleClass))
        {
            total = times.get(moduleClass);
        }
        total += time;
        times.put(moduleClass, total);
    }

    public void disableModule(final Class<? extends AbstractSmasher> moduleClass)
    {
        try
        {
            database.execute("UPDATE " + TEST_QUEUE_TABLE + " " + "SET active = 0 " + "WHERE " + "locked = 0 " + "AND complete = 0 " + "AND module_class = '" + moduleClass + "'");
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem with disabling a module	: " + e.toString(), e);
        }
    }

    public void enableModule(final Class<? extends AbstractSmasher> moduleClass)
    {
        try
        {
            database.execute("UPDATE " + TEST_QUEUE_TABLE + " " + "SET active = 1 " + "WHERE " + "locked = 0 " + "AND complete = 0 " + "AND module_class = '" + moduleClass + "'");
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem with enabling a module: " + e.toString(), e);
        }
    }

    @Override
    protected String getDBPath()
    {
        return "test-queue.db";
    }

    @Override
    protected int getMaxThreadCount()
    {
        return Scan.getScanSettings().getMaxTesterThreads();
    }

    @Override
    protected AbstractQueueThread getNewThread()
    {
        return new TesterThread(getThreadGroup());
    }

    @Override
    public synchronized QueueItem getNextQueueItem()
    {
        long start = new Date().getTime();
        try
        {
            TestJob job = (TestJob) database.selectSimpleObject("SELECT serialized_test_job " + "FROM " + TEST_QUEUE_TABLE + " " + "WHERE " + "test_id = (" + "SELECT MIN(test_id) " + "FROM " + TEST_QUEUE_TABLE + " " + "WHERE " + "locked = 0 "
                            + "AND complete = 0 " + "AND active = 1 " + "AND test_id NOT IN (" + "SELECT " + "DISTINCT " + TEST_DEPENDENCY_TABLE + ".test_id " + "FROM " + TEST_DEPENDENCY_TABLE + ", " + TEST_QUEUE_TABLE + " " + "WHERE " + TEST_DEPENDENCY_TABLE
                            + ".test_id = " + TEST_QUEUE_TABLE + ".test_id " + "AND locked = 0 " + "AND active = 1 " + "AND complete = 0 " + ") " + ") "

            , new Object[] {});

            database.execute("UPDATE " + TEST_QUEUE_TABLE + " SET locked = 1 WHERE test_id = " + job.getId());
            synchronized (nextItemLock)
            {
                nextItemTime += new Date().getTime() - start;
            }

            return job;
        }
        catch (DataNotFoundException e)
        {
            // Nothing needs to be done
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem with getting a queue item: " + e.toString(), e);
        }
        return null;
    }

    public int getPendingCount(final Class<? extends AbstractSmasher> moduleClass)
    {
        long start = new Date().getTime();
        int count = 0;
        try
        {
            count = database.selectSimpleInt("SELECT count(*) " + "FROM " + TEST_QUEUE_TABLE + " " + "WHERE complete = 0 AND active = 1 AND module_class = ?", new Object[] { moduleClass });
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem getting queue size: " + e.toString(), e);
        }
        synchronized (pendingLock)
        {
            pendingTime += new Date().getTime() - start;
        }
        return count;
    }

    public Map<Class<? extends AbstractSmasher>, Integer> getProcessedCount()
    {
        return processedCount;
    }

    public Map<Class<? extends AbstractSmasher>, Integer> getTimes()
    {
        return times;
    }

    @Override
    protected void initializeNewDatabase()
    {
        LOGGER.debug("Initializing database for test job storage");
        try
        {
            String tableQuery = "CREATE TABLE " + TEST_QUEUE_TABLE + " (\n" + "test_id INT,\n" + "group_id INT,\n" + "module_class varchar(100),\n" + "active BOOLEAN,\n" + "complete BOOLEAN default 0,\n" + "locked BOOLEAN default 0,\n"
                            + "serialized_test_job BLOB,\n" + "PRIMARY KEY (test_id))";
            // String indexQuery = "CREATE INDEX IDX_TEST_JOB_GROUP_ID ON test_queue (group_id)";
            database.execute(tableQuery);
            // database.execute(indexQuery);
            database.execute("CREATE INDEX IDX_TEST_JOB_module_class ON test_queue (module_class, locked, complete)");
            database.execute("CREATE INDEX IDX_TEST_JOB_TEST_ID ON test_queue (test_id)");
            database.execute("CREATE INDEX IDX_TEST_LOCKED_ACTIVE_COMPLETE_TESTID ON test_queue (locked, active, complete, test_id)");

            tableQuery = "CREATE TABLE " + TEST_DEPENDENCY_TABLE + " (\n" + "test_id INT,\n" + "dependency INT,\n" + "PRIMARY KEY (dependency, test_id))";
            database.execute(tableQuery);
            database.execute("CREATE INDEX IDX_TEST_DEPENDENCY_DEP ON " + TEST_DEPENDENCY_TABLE + " (dependency)");
            database.execute("CREATE INDEX IDX_TEST_DEPENDENCY_TEST_ID ON " + TEST_DEPENDENCY_TABLE + " (test_id)");

        }
        catch (Throwable e)
        {
            LOGGER.error("Problem with creating tester queue database: " + e.toString(), e);
            System.exit(1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.queues.AbstractScanQueue#initializeOldDatabase()
     */
    @Override
    protected void initializeOldDatabase()
    {
        try
        {
            TestJob.setLastID(database.selectSimpleInt("SELECT MAX(test_id) FROM " + getQueueTableName(), new Object[0]));
        }
        catch (DataNotFoundException e)
        {
            LOGGER.debug("No test jobs found; starting with ID 0");
            TestJob.setLastID(0);
        }
        catch (Throwable e)
        {
            LOGGER.error("Odd problem setting up test queue database: " + e.toString(), e);
            throw new IllegalStateException(e);
        }
    }

    private int isModuleEnabled(final Class<? extends AbstractSmasher> moduleClass)
    {
        return Scan.getInstance().getEnabledModules().contains(MasterTestModuleCollection.getInstance().getTestModule(moduleClass)) ? 1 : 0;
    }

    public void redoJobs(final Collection<Integer> jobs)
    {
        long start = new Date().getTime();
        for (Integer i : jobs)
        {
            try
            {
                database.execute("UPDATE " + TEST_QUEUE_TABLE + " " + "SET complete = 0, locked = 0 " + "WHERE test_id = " + i);

            }
            catch (Throwable e)
            {
                LOGGER.error("Problem with redoing a test job: " + e.toString(), e);
            }

        }
        synchronized (redoLock)
        {
            redoTime += new Date().getTime() - start;
        }
    }

    @Override
    public void removeQueueItem(final QueueItem finishedItem)
    {
        long start = new Date().getTime();
        TestJob testJob = (TestJob) finishedItem;
        try
        {
            database.execute("UPDATE " + TEST_QUEUE_TABLE + " " + "SET complete = 1, locked = 0 " + "WHERE test_id = " + testJob.getId());

            database.execute("DELETE FROM " + TEST_DEPENDENCY_TABLE + " " + "WHERE dependency = " + testJob.getId());
        }
        catch (InterruptedException e)
        {
            LOGGER.info("Scan must be terminated");
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem with removing a queue item: " + e.toString(), e);
        }

        if (processedCount.containsKey(testJob.getModuleClass()))
        {
            processedCount.put(testJob.getModuleClass(), processedCount.get(testJob.getModuleClass()) + 1);
        }
        else
        {
            processedCount.put(testJob.getModuleClass(), 1);
        }

        synchronized (removeLock)
        {
            removeTime += new Date().getTime() - start;
        }
    }

    public void submitJobs(final Map<AbstractSmasher, Set<TestJob>> tests)
    {
        // Setup dependencies
        Set<TestJob> allJobs = new HashSet<TestJob>();
        for (AbstractSmasher module : tests.keySet())
        {
            allJobs.addAll(tests.get(module));
            for (TestJob test : tests.get(module))
            {
                for (Class<? extends AbstractSmasher> prereq : module.getPrerequisites())
                {
                    AbstractSmasher prereqModule = MasterTestModuleCollection.getInstance().getTestModule(prereq);
                    addDependencies(test, tests.get(prereqModule));
                }

                for (Class<? extends AbstractSmasher> prereq : module.getSoftPrerequisites())
                {
                    AbstractSmasher prereqModule = MasterTestModuleCollection.getInstance().getTestModule(prereq);
                    if (tests.containsKey(prereqModule))
                    {
                        addDependencies(test, tests.get(prereqModule));
                    }
                }
            }
        }

        addTests(allJobs);
    }
}
