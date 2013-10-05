package com.grendelscan.testing.modules.impl.sessionManagement;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByCookieTest;
import com.grendelscan.testing.modules.types.InitialAuthenticationTest;
import com.grendelscan.testing.utils.sessionIDs.SessionID;
import com.grendelscan.testing.utils.sessionIDs.SessionIDLocation;

public class SessionFixation extends AbstractTestModule implements ByCookieTest, InitialAuthenticationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFixation.class);
    private final DatabaseBackedList<String> vulnerableSessionIDs;

    public SessionFixation()
    {
        vulnerableSessionIDs = new DatabaseBackedList<String>("session-ids-vulnerable-to-fixation");
    }

    private boolean containsSetCookie(final StandardHttpTransaction transaction, final String cookieName, final String cookieValue)
    {
        boolean contains = false;

        for (Cookie cookie : HttpUtils.getSetCookies(transaction))
        {
            if (cookie.getName().equalsIgnoreCase(cookieName) && cookie.getValue().equalsIgnoreCase(cookieValue))
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    @Override
    public String getDescription()
    {
        return "Checks for session fixation with cookies. You are encouraged to enable " + "the \"Cookie session ID strength checker\" module. It will help to more " + "accurately identify which cookies may be used for session tracking. ";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SESSION_MANAGEMENT;
    }

    @Override
    public String getName()
    {
        return "Session fixation (cookies)";
    }

    @Override
    public Class<AbstractTestModule>[] getSoftPrerequisites()
    {
        return new Class[] { CookieStrength.class };
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private void report(final StandardHttpTransaction transaction, final String cookieName, final String longDescription)
    {
        vulnerableSessionIDs.add(cookieName.toUpperCase());

        String title = "Possible session fixation identified";
        String impact = "One form of session fixation is when an arbitrary session ID can be set from an HTTP query parameter. " + "If an attacker can get the user to execute the query (perhaps by clicking on a link), the "
                        + "session can be easily hijacked since the session ID is already known. One way this could be " + "performed is by sending a URL containing the session ID to the user. ";
        String recomendations = "Correcting session fixation flaws will usually require reviewing the application " + "source code and removing the ability to set the session ID from a query. ";
        String references = HtmlUtils.makeLink("http://www.webappsec.org/projects/threat/classes/session_fixation.shtml");
        Finding event = new Finding(null, getName(), FindingSeverity.MEDIUM, transaction.getRequestWrapper().getURI(), title, "A problem was discovered with cookie values.", longDescription, impact, recomendations, references);
        Scan.getInstance().getFindings().addFinding(event);
    }

    /**
     * This checks to see if any random string can be supplied as a cookie value and result in successful fixation.
     * 
     * @param transaction
     * @param testPattern
     * @param cookieName
     * @return
     * @throws InterruptedScanException
     */
    private boolean testArbitraryCookie(final StandardHttpTransaction transaction, final String testPattern, final Cookie cookie, final int testJobId) throws InterruptedScanException
    {
        boolean arbitrary = false;
        String newCookieValue = StringUtils.generateRandomString(StringUtils.FORMAT_LOWER_CASE_ALPHA, 6);
        StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        testTransaction.getRequestWrapper().setURI(updateUri(testPattern, testTransaction, cookie.getName(), newCookieValue), true);
        testTransaction.setRequestOptions(requestOptions);
        try
        {
            testTransaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable: " + e.toString());
        }

        if (containsSetCookie(testTransaction, cookie.getName(), newCookieValue))
        {
            arbitrary = true;
        }
        else
        {
            // If it didn't work with letters, try numbers
            newCookieValue = StringUtils.generateRandomString(StringUtils.FORMAT_DECIMAL, 6);
            testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
            testTransaction.getRequestWrapper().setURI(updateUri(testPattern, testTransaction, cookie.getName(), newCookieValue), true);
            try
            {
                testTransaction.execute();
                if (containsSetCookie(testTransaction, cookie.getName(), newCookieValue))
                {
                    arbitrary = true;
                }
            }
            catch (UnrequestableTransaction e)
            {
                LOGGER.warn(getName() + " request unrequestable: " + e.toString());
            }
        }

        if (arbitrary)
        {
            testTransaction.writeToDisk();
            String longDescription = "The cookie named " + cookie.getName() + " appears to be used to track session state. If so, it also appears to be "
                            + "vulnerable to session fixation. When the URL listed below was supplied, the response set the cookie to " + "the same value provided in the URL. It appears to be possible to set the cookie to arbitrary "
                            + "values. This means that the attacker does not need to identify a valid session ID before fixating the victim " + "on it. <br> " + testTransaction.getRequestWrapper().getURI()
                            + "<br><br>The transaction used for testing can be viewed " + HtmlUtils.makeLink(testTransaction.getSavedUrl(), "here") + ".";

            report(testTransaction, cookie.getName(), longDescription);
        }

        return arbitrary;
    }

    @Override
    public void testByCookie(final int transactionID, final Cookie cookie, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        if (!SessionID.getInstance().isKnownSessionID(cookie.getName()) || vulnerableSessionIDs.contains(cookie.getName().toUpperCase()))
        {
            return;
        }

        testCookie(transaction, cookie, testJobId);
    }

    private void testCookie(final StandardHttpTransaction transaction, final Cookie cookie, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        testTransaction.removeCookie(cookie.getName());

        for (String pattern : SessionID.getInstance().getFixationTestPatterns())
        {
            handlePause_isRunning();
            try
            {
                if (testArbitraryCookie(testTransaction, pattern, cookie, testJobId) || testValidCookie(testTransaction, pattern, cookie, testJobId))
                {
                    break;
                }
            }
            catch (UnrequestableTransaction e)
            {
                LOGGER.warn(getName() + " request unrequestable: " + e.toString());
            }
        }
    }

    @Override
    public void testInitialAuthentication(final int transactionID, final SessionIDLocation sessionIDLocation, final int testJobId) throws InterruptedScanException
    {
        if (sessionIDLocation == SessionIDLocation.COOKIE_SESSION_ID)
        {
            StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
            testTransaction(transaction, testJobId);
        }
    }

    private synchronized void testTransaction(final StandardHttpTransaction transaction, final int testJobId) throws InterruptedScanException
    {
        // if the page is already authenticated, or if it's a login transaction,
        // then it's not fixation
        if (!transaction.isAuthenticated() && !transaction.isLoginTransaction())
        {
            List<SerializableBasicCookie> cookies = HttpUtils.getSetCookies(transaction);
            for (SerializableBasicCookie cookie : cookies)
            {
                handlePause_isRunning();
                if (!SessionID.getInstance().isKnownSessionID(cookie.getName()) || vulnerableSessionIDs.contains(cookie.getName().toUpperCase()))
                {
                    continue;
                }

                testCookie(transaction, cookie, testJobId);
            }
        }
    }

    private boolean testValidCookie(final StandardHttpTransaction transaction, final String testPattern, final Cookie cookie, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        boolean fixed = false;

        StandardHttpTransaction newTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        newTransaction.getRequestWrapper().setURI(updateUri(testPattern, newTransaction, cookie.getName(), cookie.getValue()), true);
        newTransaction.setRequestOptions(requestOptions);
        newTransaction.execute();
        if (containsSetCookie(newTransaction, cookie.getName(), cookie.getValue()))
        {
            transaction.writeToDisk();
            String longDescription = null;
            try
            {
                longDescription = "The cookie named " + cookie.getName() + " appears to be used to track session state. If so, it also appears to be " + "vulnerable to session fixation. When the URL listed below was supplied, the response set the cookie to "
                                + "the same value provided in the URL. It does not appear to be possible to set the cookie to arbitrary " + "values. This means that the attacker must first identify a valid session ID before fixating the victim "
                                + "on it. <br> " + URIStringUtils.getQuery(transaction.getRequestWrapper().getAbsoluteUriString()) + "<br><br>The transaction used for testing can be viewed " + HtmlUtils.makeLink(transaction.getSavedUrl(), "here") + ".";
            }
            catch (URISyntaxException e)
            {
                IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
                LOGGER.error(e.toString(), e);
                throw ise;
            }

            report(transaction, cookie.getName(), longDescription);
            fixed = true;
        }

        return fixed;
    }

    private String updateUri(final String testPattern, final StandardHttpTransaction transaction, final String name, final String value)
    {
        try
        {
            return testPattern.replace("%%baseurl%%", URIStringUtils.getFileUri(transaction.getRequestWrapper().getURI())).replace("%%idname%%", name).replace("%%idvalue%%", value)
                            .replace("%%query%%", URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()));
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }
    }

    // public void testByResponseHeader(int transactionID, String
    // responseHeaderName)
    // {
    // AbstractHttpTransaction transaction =
    // Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
    // /*
    // * We don't want to tests logins because it could screw up the good
    // sessions for spidering.
    // * The actual login page can be tested by sending a transaction not marked
    // as a login
    // * before the spidering begins.
    // *
    // * We also don't want redirected requests. We will test the request for
    // the redirect location.
    // */
    // String summaryUri =
    // URIStringUtils.getBaseUri(transaction.getRequestWrapper().getURI()) + "?"
    // + transaction.getQuerySummary(false);
    // if (! transaction.isLoginTransaction() &&
    // transaction.getRedirectRequestTransactionID() == 0 &&
    // !testedUris.contains(summaryUri))
    // {
    // testedUris.add(summaryUri);
    // testTransaction(transaction);
    // }
    // }

}
