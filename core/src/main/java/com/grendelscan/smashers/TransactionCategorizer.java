/*
 * categorizer.java
 * 
 * Created on September 15, 2007, 8:53 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.smashers;

import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;

/**
 * 
 * @author David Byrne
 */
public interface TransactionCategorizer
{
    public Map<AbstractSmasher, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException;
}
