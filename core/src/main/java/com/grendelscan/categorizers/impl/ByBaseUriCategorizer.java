/*
 * AllTransactionsCategorizer.java
 * 
 * Created on September 15, 2007, 8:59 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.categorizers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.SingleSetCategorizer;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.ByBaseUriTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByBaseUriTest;

/**
 * 
 * @author Administrator
 */

public class ByBaseUriCategorizer extends SingleSetCategorizer {

	/** Creates a new instance of AllTransactionsCategorizer */
	public ByBaseUriCategorizer() {
		super(ByBaseUriTest.class);
	}

	public void processBaseUri(final String baseUri) {
		Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();
		synchronized (testModules) {
			for (AbstractTestModule testModule : testModules) {
				ByBaseUriTestJob testJob = new ByBaseUriTestJob(
						testModule.getClass(), baseUri);
				addJobToCollection(testJob, testModule, tests);
			}
		}
		Scan.getInstance().getTesterQueue().submitJobs(tests);
	}

}
