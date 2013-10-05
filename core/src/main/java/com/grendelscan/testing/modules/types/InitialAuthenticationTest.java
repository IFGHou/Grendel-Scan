package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.utils.sessionIDs.SessionIDLocation;

/**
 * 
 * @author David Byrne
 */
public interface InitialAuthenticationTest extends TestType
{
    public void testInitialAuthentication(int transactionID, SessionIDLocation sessionIDLocation, int testJobId) throws InterruptedScanException;
}
