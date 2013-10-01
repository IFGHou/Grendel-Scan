package com.grendelscan.tests.testTypes;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;

public interface ByCookieTest extends TestType
{
	public void testByCookie(int transactionID, Cookie cookie, int testJobId) throws InterruptedScanException;
}
