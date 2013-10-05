package com.grendelscan.testing.modules.types;


/**
 * 
 * @author David Byrne
 */
public interface RecrawlTest extends TestType
{
	public void testAllTransactions(int transactionID, int testJobId) throws InterruptedException;
}
