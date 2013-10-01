package com.grendelscan.smashers.types;

import com.grendelscan.smashers.TestType;

/**
 * 
 * @author David Byrne
 */
public interface RecrawlTest extends TestType
{
	public void testAllTransactions(int transactionID, int testJobId) throws InterruptedException;
}
