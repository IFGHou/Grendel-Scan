/*
 * AllTransactionsTest.java
 * 
 * Created on September 15, 2007, 10:32 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

/**
 * 
 * @author Administrator
 */
public interface AllTransactionsTest extends TestType
{
	public void testAllTransactions(int transactionID, int testJobId) throws InterruptedScanException;
}
