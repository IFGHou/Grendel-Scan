package com.grendelscan.queues.categorizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.TransactionCategorizer;
import com.grendelscan.logging.Log;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.queues.AbstractTransactionBasedQueueThread;
import com.grendelscan.queues.QueueThreadGroup;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;

public class CategorizerThread extends AbstractTransactionBasedQueueThread

{

	public CategorizerThread(QueueThreadGroup threadGroup)
	{
		super(threadGroup);
	}


	@Override
	protected void processNextTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Map<TestModule, Set<TestJob>> jobs = new HashMap<TestModule, Set<TestJob>>();
		if (transaction.getRequestOptions().tokenSubmission)
		{
			/* Token submissions should only be reviewed for token output, and
			 * not used for other types of testing 
			 */
			handlePause_isRunning();
			Map<TestModule, Set<TestJob>> tempJobs = 
					Scan.getInstance().getCategorizers().getByOutputContextCategorizer().analyzeTransaction(transaction);
			joinJobLists(jobs, tempJobs);
		}
		else
		{
			for (TransactionCategorizer categorizer: Scan.getInstance().getCategorizers().getTransactionCategorizers())
			{
				handlePause_isRunning();
				Map<TestModule, Set<TestJob>> tempJobs = categorizer.analyzeTransaction(transaction);
				joinJobLists(jobs, tempJobs);
			}
		}
		Log.trace(jobs.size() + " tests created for transaction #" + transaction.getId());
		Scan.getInstance().getTesterQueue().submitJobs(jobs);
	}

	
	private void joinJobLists(Map<TestModule, Set<TestJob>> target, Map<TestModule, Set<TestJob>> source)
	{
		for (TestModule module: source.keySet())
		{
			if (target.containsKey(module))
			{
				Set<TestJob> tests = target.get(module);
				for (TestJob job: source.get(module))
				{
					tests.add(job);
				}
			}
			else
			{
				target.put(module, source.get(module));
			}
		}
	}


	@Override
	protected AbstractScanQueue getQueue()
	{
		return Scan.getInstance().getCategorizerQueue();
	}



}
