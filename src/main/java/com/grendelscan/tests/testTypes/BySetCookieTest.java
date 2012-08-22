package com.grendelscan.tests.testTypes;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;

public interface BySetCookieTest extends TestType
{
	public void testBySetCookie(int transactionID, Cookie cookie, int testJobId) throws InterruptedScanException;
}
