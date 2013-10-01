package com.grendelscan.smashers.sessionManagement;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.collections.CollectionUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.BySetCookieTest;
import com.grendelscan.smashers.types.InitialAuthenticationTest;
import com.grendelscan.smashers.utils.sessionIDs.SessionID;
import com.grendelscan.smashers.utils.sessionIDs.SessionIDLocation;

public class CookieStrength extends AbstractSmasher implements BySetCookieTest, InitialAuthenticationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieStrength.class);
    private final int maxCookieRequests;
    private final int minAcceptableBitLength;

    public CookieStrength()
    {
        maxCookieRequests = SessionID.getInstance().getMaxSessionIDRequests();
        minAcceptableBitLength = SessionID.getInstance().getMinAcceptableBitLength();
    }

    private void checkCookieEntropy(final StandardHttpTransaction originalTransaction, final String cookieName, final String[] testCookiesValues, final StandardHttpTransaction[] testTransactions)
    {

        int bitChanges;
        if ((bitChanges = SessionID.getInstance().getSessionIDBitEntropy(testCookiesValues)) >= 0)
        {

            String longDescription = "The cookie named " + cookieName + " appears to be used to track session state. " + "Approximately " + String.valueOf(bitChanges) + " bits of random data were observed. A minimum of " + minAcceptableBitLength
                            + " is generally recommended.<br><br>";
            longDescription += getTransactionLinks(testTransactions);

            String recomendations = "Assure that session ID contain at least " + minAcceptableBitLength + " bits of random data.";
            String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Session_Management");
            report(originalTransaction, testCookiesValues, "Similar session IDs detected", longDescription, recomendations, references, FindingSeverity.MEDIUM);
        }
    }

    private void findDuplicateCookies(final StandardHttpTransaction originalTransaction, final String cookieName, final String[] testCookiesValues, final StandardHttpTransaction[] testTransactions)
    {

        if (SessionID.getInstance().findDuplicateSessionIDs(testCookiesValues))
        {
            String longDescription = "The cookie named " + cookieName + " appears to be used to track session state. " + "However, some duplicate cookie values were detected. If " + cookieName + " is used"
                            + "to track session state, all values supplied by the web server should be unique. " + "Detection of duplicate values implies that the generation algorithm outputs " + "predictably named cookieJar. <br><br>";
            longDescription += getTransactionLinks(testTransactions);
            String recomendations = "Assure that cookieJar contain at least " + minAcceptableBitLength + " bits of random data.";
            String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Session_Management");
            report(originalTransaction, testCookiesValues, "Duplicate session IDs detected", longDescription, recomendations, references, FindingSeverity.MEDIUM);
        }
    }

    private String[] getCookieValues(final String cookieName, final StandardHttpTransaction[] testTransactions) throws InterruptedScanException
    {
        String[] testCookiesValues = new String[maxCookieRequests + 1];
        int index = 0;
        for (Cookie cookie : SessionID.getInstance().getSetCookiesByName(testTransactions, cookieName))
        {
            if (cookie == null)
            {
                LOGGER.error("Cookie not found where one expected.", new Throwable());
                continue;
            }
            testCookiesValues[index++] = cookie.getValue();
        }

        return testCookiesValues;
    }

    @Override
    public String getDescription()
    {
        return "Checks cookies for weak session keys based on net entropy, " + "repeated values and overall key length.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SESSION_MANAGEMENT;
    }

    @Override
    public String getName()
    {
        return "Cookie strength ";
    }

    public String[] getResponseHeaders()
    {
        String headers[] = { "Set-Cookie" };
        return headers;
    }

    private String getTransactionLinks(final StandardHttpTransaction[] testTransactions)
    {
        String links = "The following transactions were used for the test:<br>";
        for (StandardHttpTransaction transaction : testTransactions)
        {
            transaction.writeToDisk();
            links += HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId())) + "<br>\n";
        }

        return links;
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private void report(final StandardHttpTransaction originalTransaction, final String[] testCookiesValues, final String title, String longDescription, final String recomendations, final String references, final FindingSeverity severity)
    {
        longDescription += "<br><br>The following cookies were received during testing:<br>\n";
        String impact = "If the session ID is session ID is generated in a predictable manner, an attacker could hijack legitimate sessions " + "by guessing the session IDs of authenticated users.";
        for (String value : testCookiesValues)
        {
            longDescription += "<tab>" + value + "<br/>\n";
        }
        Finding event = new Finding(null, getName(), severity, originalTransaction.getRequestWrapper().getURI(), title, "A problem was discovered with cookie values.", longDescription, impact, recomendations, references);
        Scan.getInstance().getFindings().addFinding(event);
    }

    @Override
    public void testBySetCookie(final int transactionID, final Cookie cookie, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        testTransaction(transaction, cookie, testJobId);
    }

    private void testCookies(final StandardHttpTransaction originalTransaction, final String cookieName, final StandardHttpTransaction[] testTransactions) throws InterruptedScanException
    {
        String[] testCookiesValues = getCookieValues(cookieName, testTransactions);
        findDuplicateCookies(originalTransaction, cookieName, testCookiesValues, testTransactions);
        checkCookieEntropy(originalTransaction, cookieName, testCookiesValues, testTransactions);
    }

    @Override
    public void testInitialAuthentication(final int transactionID, final SessionIDLocation sessionIDLocation, final int testJobId) throws InterruptedScanException
    {
        if (sessionIDLocation == SessionIDLocation.COOKIE_SESSION_ID)
        {
            StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
            for (Cookie cookie : HttpUtils.getSetCookies(transaction))
            {
                testTransaction(transaction, cookie, testJobId);
            }
        }
    }

    private void testTransaction(final StandardHttpTransaction transaction, final Cookie cookie, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction[] testTransactions = null;

        Set<String> oldCookies = new HashSet<String>(transaction.getUsedCookieNames());
        for (NameValuePairDataContainer parameter : DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
        {
            oldCookies.add(new String(DataUtils.getBytes(parameter.getNameData())));
        }

        handlePause_isRunning();
        /*
         * If this cookie was supplied by the initial transaction in a header, or as a URL parameter, or if it's already been tested in another transaction, don't test it.
         */
        synchronized (SessionID.getInstance().getTestedSessionIDs())
        {
            if (CollectionUtils.containsStringIgnoreCase(oldCookies, cookie.getName()) || SessionID.getInstance().getTestedSessionIDs().contains(cookie.getName()))
            {
                return;
            }

            SessionID.getInstance().getTestedSessionIDs().add(cookie.getName());
        }

        testTransactions = SessionID.getInstance().getSampleCookieTransactions(transaction, cookie, getName(), testJobId);
        if (testTransactions == null)
        {
            LOGGER.debug(cookie.getName() + " doesn't seem to be a session ID");
            return;
        }

        LOGGER.debug(getName() + " is testing the cookie \"" + cookie.getName() + "\"");

        handlePause_isRunning();
        if (SessionID.getInstance().isKnownSessionID(cookie.getName()))
        {
            testCookies(transaction, cookie.getName(), testTransactions);
        }
    }

}
