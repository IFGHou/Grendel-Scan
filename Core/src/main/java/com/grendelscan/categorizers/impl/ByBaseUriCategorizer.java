/*
 * AllTransactionsCategorizer.java
 * 
 * Created on September 15, 2007, 8:59 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.categorizers.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.SingleSetCategorizer;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testJobs.ByBaseUriTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByBaseUriTest;

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
	
	public void processBaseUri(String baseUri)
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();
		synchronized (testModules)
		{
			for (TestModule testModule: testModules)
			{
				ByBaseUriTestJob testJob = 
					new ByBaseUriTestJob(testModule.getClass(), baseUri);
				addJobToCollection(testJob, testModule, tests);
			}
		}
		Scan.getInstance().getTesterQueue().submitJobs(tests);
	}

}
