package com.grendelscan.queues.tester;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.requester.sessionState.SessionState;
import com.grendelscan.tests.testJobs.TestJob;
public class TestBreakpoint
{
	private List <TestJob> jobs;
	private SessionState sessionState;
	
	/**
     * @param sessionState
     * @param scan
     */
    public TestBreakpoint(SessionState sessionState)
    {
	    this.sessionState = sessionState;
		jobs = new ArrayList<TestJob>(1);
    }

    /**
     * Will check to see if the session state is still valid. If
     * it isn't, the a login will be attempted, and the 
     * transactions and test jobs will be rerun.
     * @return
     */
    public boolean testSessionValidity()
    {
    	return false;
    }

	public void addNewJob(TestJob job)
	{
		jobs.add(job);
	}
}
