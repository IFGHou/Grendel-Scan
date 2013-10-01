/*
 * categorizer.java
 * 
 * Created on September 15, 2007, 8:53 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.categorizers;


import java.util.Map;
import java.util.Set;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
/**
 * 
 * @author David Byrne
 */
public interface TransactionCategorizer
{
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException;
}
