package com.grendelscan.smashers.types;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByCookieTest extends TestType
{
	public void testByCookie(int transactionID, Cookie cookie, int testJobId) throws InterruptedScanException;
}
