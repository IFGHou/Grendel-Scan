/*
 * AllTransactionsCategorizer.java
 * 
 * Created on September 15, 2007, 8:59 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.SingleSetCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByBaseUriTestJob;
import com.grendelscan.smashers.types.ByBaseUriTest;

/**
 * 
 * @author Administrator
 */

public class ByBaseUriCategorizer extends SingleSetCategorizer
{

    /** Creates a new instance of AllTransactionsCategorizer */
    public ByBaseUriCategorizer()
    {
        super(ByBaseUriTest.class);
    }

    public void processBaseUri(final String baseUri)
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();
        synchronized (testModules)
        {
            for (AbstractSmasher testModule : testModules)
            {
                ByBaseUriTestJob testJob = new ByBaseUriTestJob(testModule.getClass(), baseUri);
                addJobToCollection(testJob, testModule, tests);
            }
        }
        Scan.getInstance().getTesterQueue().submitJobs(tests);
    }

}
