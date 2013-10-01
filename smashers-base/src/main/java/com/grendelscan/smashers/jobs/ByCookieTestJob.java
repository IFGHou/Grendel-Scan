package com.grendelscan.smashers.jobs;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByCookieTest;

public class ByCookieTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Cookie cookie;

    public ByCookieTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final Cookie cookie)
    {
        super(moduleClass, transactionID);
        this.cookie = cookie;
        this.transactionID = transactionID;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByCookieTest) getModule()).testByCookie(transactionID, cookie, getId());
    }
}
