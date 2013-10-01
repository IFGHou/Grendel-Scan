package com.grendelscan.smashers.types;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface BySetCookieTest extends TestType
{
	public void testBySetCookie(int transactionID, Cookie cookie, int testJobId) throws InterruptedScanException;
}
