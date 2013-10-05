/*
 * categorizer.java
 * 
 * Created on September 15, 2007, 8:53 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.categorizers.interfaces;

import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;

/**
 * 
 * @author David Byrne
 */
public interface TransactionCategorizer
{
    public Map<AbstractTestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException;
}
