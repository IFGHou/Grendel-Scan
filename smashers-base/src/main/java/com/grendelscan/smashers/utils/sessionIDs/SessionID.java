package com.grendelscan.smashers.utils.sessionIDs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.collections.CollectionUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;

public class SessionID
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionID.class);

    public static final SessionID getInstance()
    {
        return instance;
    }

    private final DatabaseBackedMap<String, int[]> cookieSamples;
    private final List<String> fixationTestPatterns;
    private final int maxCookieRequests;
    private final int maxSessionIDRequests;
    private final float minAcceptableBitChangePercent;
    private final int minAcceptableBitLength;
    private final float minAcceptableNetBitChangePercent;
    private final List<String> notSessionIDs;

    private final DatabaseBackedList<String> testedSessionIDs;

    private static SessionID instance;

    public static void initialize()
    {
        instance = new SessionID();
    }

    private SessionID()
    {
        maxSessionIDRequests = ConfigurationManager.getInt("session_id_analysis.max_session_id_requests");
        minAcceptableBitLength = ConfigurationManager.getInt("session_id_analysis.min_acceptable_bit_length");
        minAcceptableBitChangePercent = ConfigurationManager.getFloat("session_id_analysis.min_bit_change_percentage");
        minAcceptableNetBitChangePercent = ConfigurationManager.getFloat("session_id_analysis.min_net_bit_change_percentage");
        notSessionIDs = ConfigurationManager.getList("session_id_analysis.not_session_ids");
        fixationTestPatterns = ConfigurationManager.getList("session_id_analysis.session_fixation_patterns");
        maxCookieRequests = getMaxSessionIDRequests();
        cookieSamples = new DatabaseBackedMap<String, int[]>("session_id_cookie_samples");
        testedSessionIDs = new DatabaseBackedList<String>("tested_session_ids");
    }

    public void addKnownSessionID(final String sessionID)
    {
        Pattern p = Pattern.compile(Pattern.quote(sessionID), Pattern.CASE_INSENSITIVE);
        Scan.getScanSettings().addKnownSessionIDRegex(p);
    }

    public boolean findDuplicateSessionIDs(final Iterable<String> testSessionIDValues)
    {
        return findDuplicateSessionIDs(CollectionUtils.toStringArray(testSessionIDValues));
    }

    public boolean findDuplicateSessionIDs(final String[] testSessionIDValues)
    {
        boolean duplicate = false;
        HashSet<String> cookies = new HashSet<String>(maxSessionIDRequests + 1);

        for (int index = 0; index <= maxSessionIDRequests; index++)
        {
            if (cookies.contains(testSessionIDValues[index]))
            {
                duplicate = true;
                break;
            }
            cookies.add(testSessionIDValues[index]);
        }

        return duplicate;
    }

    /**
     * Looks for SessionIDs in Set-Cookie parameters and the response body. If there is a cookie and a URL parameter sessionID, only the cookie location will be returned
     * 
     * @param transaction
     * @return
     */
    public SessionIDLocation findSessionIDLocation(final StandardHttpTransaction transaction)
    {
        SessionIDLocation containsID = SessionIDLocation.NO_SESSION_ID;

        for (Cookie cookie : HttpUtils.getSetCookies(transaction))
        {
            containsID = SessionIDLocation.COOKIE_SESSION_ID;
            for (String knownID : notSessionIDs)
            {
                if (cookie.getName().equalsIgnoreCase(knownID))
                {
                    containsID = SessionIDLocation.NO_SESSION_ID;
                    break;
                }
            }
            if (containsID == SessionIDLocation.COOKIE_SESSION_ID)
            {
                break;
            }
        }

        if (containsID == SessionIDLocation.NO_SESSION_ID)
        {
            String body = new String(transaction.getResponseWrapper().getBody());

            if (stringContainsURLSessionID(body))
            {
                containsID = SessionIDLocation.URL_SESSION_ID;
            }
        }
        return containsID;
    }

    public Set<String> findUrlSessionIDs(final String htmlBody)
    {
        Set<String> ids = new HashSet<String>();

        for (Pattern sessionIDPattern : Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs())
        {
            Pattern p = Pattern.compile("\\b(" + sessionIDPattern.pattern() + ")['\"\\s]*=", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(htmlBody);
            while (m.find())
            {
                ids.add(m.group(1));
            }
        }

        return ids;
    }

    public void findUrlSessionIDsAndValues(final String htmlBody, final Map<String, List<String>> sessionIDs)
    {

        for (Pattern sessionIDPattern : Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs())
        {
            Pattern p = Pattern.compile("\\b(" + sessionIDPattern.pattern() + ")['\"\\s]*=['\"\\s]*([\\w\\-_]+)", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(htmlBody);
            while (m.find())
            {
                String id = m.group(1);
                List<String> values;
                if (!sessionIDs.containsKey(id))
                {
                    values = new ArrayList<String>();
                    sessionIDs.put(id, values);
                }
                else
                {
                    values = sessionIDs.get(id);
                }

                if (!values.contains(m.group(2)))
                {
                    values.add(m.group(2));
                }
            }
        }
    }

    public int getBitChangeCount(final String stringA, final String stringB)
    {
        String[] testStrings = { stringA, stringB };
        return getNetBitChangeCount(testStrings);
    }

    public int getBitSetCount(int target)
    {
        int bits = 0;
        /*
         * Interesting trivia from http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetKernighan
         * 
         * Published in 1988, the C Programming Language 2nd Ed. (by Brian W. Kernighan and Dennis M. Ritchie) mentions this in exercise 2-9. On April 19, 2006 Don Knuth pointed out to me that this
         * method "was first published by Peter Wegner in CACM 3 (1960), 322. (Also discovered independently by Derrick Lehmer and published in 1964 in a book edited by Beckenbach.)"
         */
        for (bits = 0; target > 0; bits++)
        {
            target &= target - 1; // clear the least
            // significant bit set
        }

        return bits;
    }

    /*
     * public Set<String> getCookieNames(AbstractHttpTransaction transaction) { HashSet<String> cookies = new HashSet<String>();
     * 
     * Pattern cookiePattern = Pattern .compile( "((?:\"[^\"]+\")|(?:[a-zA-Z0-9\\-_]+))\\s*=\\s*((?:\"[^\"]+\")|(?:[^\\S;]+))\\s*;?+\\s*" ); Matcher matcher =
     * cookiePattern.matcher(transaction.getFirstRequestHeader( "Cookie").getValue()); while (matcher.find()) { cookies.add(matcher.group(1)); } return cookies; }
     */

    private String getCookieFingerprint(final Cookie cookie)
    {
        return cookie.getName() + cookie.getDomain() + cookie.getPath();
    }

    public List<String> getFixationTestPatterns()
    {
        return fixationTestPatterns;
    }

    public int getMaxSessionIDRequests()
    {
        return maxSessionIDRequests;
    }

    public float getMinAcceptableBitChangePercent()
    {
        return minAcceptableBitChangePercent;
    }

    public int getMinAcceptableBitLength()
    {
        return minAcceptableBitLength;
    }

    public float getMinAcceptableNetBitChangePercent()
    {
        return minAcceptableNetBitChangePercent;
    }

    public int getNetBitChangeCount(final String[] testSessionIDValues)
    {
        int minLength;
        int bitsSet = 0;
        byte[] currentBytes, originalBytes;
        int[] netBytes;

        minLength = testSessionIDValues[0].length();
        for (int index = 1; index < testSessionIDValues.length; index++)
        {
            if (testSessionIDValues[index] != null && testSessionIDValues[index].length() < minLength)
            {
                minLength = testSessionIDValues[index].length();
            }
        }

        originalBytes = testSessionIDValues[0].getBytes(StringUtils.getDefaultCharset());
        netBytes = new int[minLength];
        // Copy originalBytes into netBytes
        for (int byteIndex = 0; byteIndex < minLength; byteIndex++)
        {
            // netBytes[byteIndex] = originalBytes[byteIndex];
            netBytes[byteIndex] = 0;
        }

        // This tracks which bits have changed at some point across the requests
        // compared to the first request
        for (int index = 1; index < testSessionIDValues.length; index++)
        {
            currentBytes = testSessionIDValues[index].getBytes(StringUtils.getDefaultCharset());
            for (int byteIndex = 0; byteIndex < minLength; byteIndex++)
            {
                netBytes[byteIndex] = netBytes[byteIndex] | originalBytes[byteIndex] ^ currentBytes[byteIndex];
            }
        }

        for (int byteIndex = 0; byteIndex < minLength; byteIndex++)
        {
            bitsSet += getBitSetCount(netBytes[byteIndex]);
        }

        return bitsSet;
    }

    public List<String> getNotSessionIDs()
    {
        return notSessionIDs;
    }

    /**
     * Will return an array of sample cookie transactions. It will also add any changing cookie values to the list of known session IDs
     * 
     * @param transaction
     *            The transaction to duplicate if no cookie samples are available
     * @param exampleCookie
     *            The type of cookie being sought. The name, domain and path are considered.
     * @param source
     *            The source to use when executing new requests
     * @return
     * @throws InterruptedException
     */
    public StandardHttpTransaction[] getSampleCookieTransactions(final StandardHttpTransaction transaction, final Cookie exampleCookie, final String source, final int testJobId) throws InterruptedScanException
    {
        String exampleCookieFingerprint = getCookieFingerprint(exampleCookie);
        if (!cookieSamples.containsKey(exampleCookieFingerprint))
        {
            if (transaction.getResponseWrapper().getHeaders().getHeaders("Set-Cookie").size() > 0)
            {
                seedSessionIDDatabase(transaction, source, testJobId);
            }
            else
            {
                throw new IllegalArgumentException("No cookies were found, and the transaction didn't have any set-cookie response headers.");
            }
        }
        if (!cookieSamples.containsKey(exampleCookieFingerprint))
        {
            return null;
        }
        StandardHttpTransaction cookieTransactions[] = new StandardHttpTransaction[maxCookieRequests + 1];
        int index = 0;
        for (int transactionID : cookieSamples.get(exampleCookieFingerprint))
        {
            StandardHttpTransaction cookieTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
            cookieTransactions[index++] = cookieTransaction;
        }
        Scan.getInstance().getCategorizerQueue();
        return cookieTransactions;
    }

    private int[] getSampleTransactions(final StandardHttpTransaction transaction, final String source, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction testTransactions[] = new StandardHttpTransaction[maxCookieRequests + 1];
        int[] testTransactionIDs = new int[maxCookieRequests + 1];
        testTransactionIDs[0] = transaction.getId();
        testTransactions[0] = transaction;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.reason = "Sample cookie fetcher on behalf of " + source;
        requestOptions.testTransaction = false;
        requestOptions.followRedirects = false;

        for (int index = 1; index <= maxCookieRequests; index++)
        {
            StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
            /*
             * The craw client is used because it will not use connection handling, causing the connection to be closed after each request. This is required because some web apps will attach session
             * state with a TCP session. Very stupid, but it still happens in the real world.
             */
            // This header stuff is because it has to be sent with the raw
            // client
            if (testTransaction.getRequestWrapper().getMethod().equalsIgnoreCase("POST"))
            {
                int length = transaction.getRequestWrapper().getBody().length;
                testTransaction.getRequestWrapper().getHeaders().addHeader("Content-Length", String.valueOf(length));
            }
            testTransaction.setRequestOptions(requestOptions);

            try
            {
                testTransaction.execute();
                testTransactionIDs[index] = testTransaction.getId();
                testTransactions[index] = testTransaction;

                if (index == 1)
                {
                    /*
                     * Pauses to let the second counter increment on the web server. This is because a poorly written app may generate identical session ids in a short time period. We'll pause for the
                     * first request, but subsequent requests will be executed quickly to try and catch a duplicate value.
                     */
                    try
                    {
                        Thread.sleep(1500);
                    }
                    catch (InterruptedException e)
                    {
                        throw new InterruptedScanException(e);
                    }
                }
            }
            catch (UnrequestableTransaction e1)
            {
                LOGGER.warn("Sample cookie fetcher request unrequstable (" + testTransaction.getRequestWrapper().getAbsoluteUriString() + "): " + e1.toString(), e1);
            }
        }

        for (Cookie cookie : HttpUtils.getSetCookies(transaction))
        {
            /*
             * If it isn't already known as a session ID, and if it isn't a a known non-session ID, and if the value is changing
             */
            if (!isKnownSessionID(cookie.getName()) && !notSessionIDs.contains(cookie.getName()) && isValueChanging(testTransactions, cookie.getName()))
            {
                addKnownSessionID(cookie.getName());
            }
        }

        return testTransactionIDs;
    }

    /**
     * 
     * @param testSessionIDValues
     * @param bitChanges
     * @return -1 if the bits are okay, otherwise the could of bits
     */
    public int getSessionIDBitEntropy(final List<String> testSessionIDValues)
    {
        return getSessionIDBitEntropy(CollectionUtils.toStringArray(testSessionIDValues));
    }

    /**
     * 
     * @param testSessionIDValues
     * @param bitChanges
     * @return -1 if the bits are okay, otherwise the could of bits
     */
    public int getSessionIDBitEntropy(final String[] testSessionIDValues)
    {
        int bitChanges = -1;
        boolean bad = false;
        for (int index = 0; index < testSessionIDValues.length - 1; index++)
        {
            if (testSessionIDValues[index] == null || testSessionIDValues[index + 1] == null)
            {
                // Debug.errDebug("Null session ID.", new Throwable());
                continue;
            }
            bitChanges = getBitChangeCount(testSessionIDValues[index], testSessionIDValues[index + 1]);
            if (bitChanges < minAcceptableBitChangePercent * minAcceptableBitLength)
            {
                bad = true;
                break;
            }
        }

        if (!bad)
        {
            bitChanges = getNetBitChangeCount(testSessionIDValues);
            if (bitChanges < minAcceptableNetBitChangePercent * minAcceptableBitLength)
            {
                bad = true;
            }
        }

        if (!bad)
        {
            bitChanges = -1;
        }

        return bitChanges;
    }

    public Cookie[] getSetCookiesByName(final StandardHttpTransaction[] testTransactions, final String cookieName) throws InterruptedScanException
    {
        Cookie cookies[] = new Cookie[testTransactions.length];

        for (int index = 0; index < testTransactions.length; index++)
        {
            boolean foundCookie = false;
            for (Cookie setCookie : HttpUtils.getSetCookies(testTransactions[index]))
            {
                Scan.getInstance().getTesterQueue().handlePause_isRunning();
                if (setCookie.getName().equals(cookieName))
                {
                    cookies[index] = setCookie;
                    foundCookie = true;
                    break;
                }
            }
            if (!foundCookie)
            {
                LOGGER.warn("Cookie not found where one was expected.");
            }
        }

        return cookies;
    }

    public DatabaseBackedList<String> getTestedSessionIDs()
    {
        return testedSessionIDs;
    }

    public boolean isKnownSessionID(final String candidate)
    {
        boolean known = false;

        for (Pattern p : Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs())
        {
            if (p.matcher(candidate).find())
            {
                known = true;
                break;
            }
        }

        return known;
    }

    private boolean isValueChanging(final StandardHttpTransaction[] testTransactions, final String cookieName) throws InterruptedScanException
    {
        boolean valueChanged = true;

        Cookie testCookies[] = getSetCookiesByName(testTransactions, cookieName);

        for (int index = 1; index <= maxCookieRequests; index++)
        {
            // If the value is changing with different
            // requests, then it should be tested

            Scan.getInstance().getTesterQueue().handlePause_isRunning();
            if (testCookies[index] == null)
            {
                // Debug.errDebug("No cookie where one expected in index " +
                // index, new Throwable());
                continue;
            }
            if (testCookies[index].getValue().equals(testCookies[index - 1].getValue()))
            {
                valueChanged = false;
                break;
            }
        }
        return valueChanged;
    }

    /**
     * Takes a transaction, looks for set cookies, tests to see if the cookie values are changing, then adds them the list of known session IDs if they are
     * 
     * @param transaction
     * @param source
     *            Execution source
     * @throws InterruptedException
     */
    public void seedSessionIDDatabase(final StandardHttpTransaction transaction, final String source, final int testJobId) throws InterruptedScanException
    {
        int[] testTransactions = getSampleTransactions(transaction, source, testJobId);
        for (Cookie cookie : HttpUtils.getSetCookies(transaction))
        {
            if (isKnownSessionID(cookie.getName()))
            {
                cookieSamples.put(getCookieFingerprint(cookie), testTransactions);
            }
        }
    }

    public boolean stringContainsURLSessionID(final String htmlBody)
    {
        boolean contains = false;

        for (Pattern sessionIDPattern : Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs())
        {
            Pattern p = Pattern.compile("\\b" + sessionIDPattern.pattern() + "['\"\\s]*=", Pattern.CASE_INSENSITIVE);
            if (p.matcher(htmlBody).find())
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    public boolean transactionRequestContainsCookie(final StandardHttpTransaction transaction, final String cookieName)
    {
        boolean contains = false;

        for (Cookie cookie : transaction.getUsedCookies())
        {
            if (cookie.getName().equalsIgnoreCase(cookieName))
            {
                contains = true;
                break;
            }
        }
        return contains;
    }

}
