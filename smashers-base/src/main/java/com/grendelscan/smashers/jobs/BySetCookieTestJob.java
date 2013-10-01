package com.grendelscan.smashers.jobs;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.BySetCookieTest;

public class BySetCookieTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final Cookie cookie;

    public BySetCookieTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final Cookie cookie)
    {
        super(moduleClass, transactionID);
        this.cookie = cookie;
        this.transactionID = transactionID;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((BySetCookieTest) getModule()).testBySetCookie(transactionID, cookie, getId());
    }
}
