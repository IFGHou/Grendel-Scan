package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;
import com.grendelscan.smashers.utils.sessionIDs.SessionIDLocation;

/**
 * 
 * @author David Byrne
 */
public interface InitialAuthenticationTest extends TestType
{
    public void testInitialAuthentication(int transactionID, SessionIDLocation sessionIDLocation, int testJobId) throws InterruptedScanException;
}
