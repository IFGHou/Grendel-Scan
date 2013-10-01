package com.grendelscan.queues.tester;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.scan.sessionState.SessionState;
import com.grendelscan.smashers.TestJob;

public class TestBreakpoint
{
    private final List<TestJob> jobs;
    private final SessionState sessionState;

    /**
     * @param sessionState
     * @param scan
     */
    public TestBreakpoint(final SessionState sessionState)
    {
        this.sessionState = sessionState;
        jobs = new ArrayList<TestJob>(1);
    }

    public void addNewJob(final TestJob job)
    {
        jobs.add(job);
    }

    /**
     * Will check to see if the session state is still valid. If it isn't, the a login will be attempted, and the transactions and test jobs will be rerun.
     * 
     * @return
     */
    public boolean testSessionValidity()
    {
        return false;
    }
}
