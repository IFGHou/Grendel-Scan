package com.grendelscan.scan.data.sessionState;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.cobra_grendel.html.domimpl.HTMLInputElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.formatting.encoding.UrlEncodingUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.commons.http.responseCompare.ResponseSamples;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

/**
 * We have to track session state so that logouts, etc can be handled.
 * 
 * @author David Byrne
 * 
 */
public class SessionState implements Serializable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionState.class);
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public static String buildSessionKey(final String username, final String paramName, final String domain)
    {
        return username + "~~~" + domain + "~~~" + paramName;
    }

    private final String username;
    private final String paramName;
    private String currentValue;
    private final int initialTransactionId;
    private final int secondTransactionId;
    private final String domain;
    private int loginTransactionId;
    private int firstAuthenticatedTransactionId;
    private boolean authenticated;
    private ResponseSamples deadSessionSamples;
    private boolean damagedSession;
    private static final int LOGGED_OUT_COMPARE_THRESHOLD = 85;
    private final Set<SessionLocation> locations;
    private DataReferenceChain bodyReferenceChain;
    private DataReferenceChain urlReferenceChain;
    private final RequestOptions deadSessionProfileBuildingRequestOptions;
    private final RequestOptions sessionReaquisitionRequestOptions;
    private final RequestOptions logoutTestRequestOptions;

    private final RequestOptions sessionHealthTestRequestOptions;

    public SessionState(final String paramName, final String domain, final String currentValue, final String username, final int initialTransaction, final int secondTransactionId, final SessionLocation location)
    {
        locations = new HashSet<SessionLocation>(1);
        locations.add(location);
        this.username = username;
        this.secondTransactionId = secondTransactionId;
        this.currentValue = currentValue;
        initialTransactionId = initialTransaction;
        this.paramName = paramName;
        this.domain = domain;

        deadSessionProfileBuildingRequestOptions = new RequestOptions();
        deadSessionProfileBuildingRequestOptions.reason = "Dead session profile building";
        deadSessionProfileBuildingRequestOptions.testRedirectTransactions = false;
        deadSessionProfileBuildingRequestOptions.testTransaction = false;
        deadSessionProfileBuildingRequestOptions.followRedirects = true;
        deadSessionProfileBuildingRequestOptions.handleSessions = false;

        sessionReaquisitionRequestOptions = new RequestOptions();
        sessionReaquisitionRequestOptions.reason = "Session reaquisition";
        sessionReaquisitionRequestOptions.testRedirectTransactions = false;
        sessionReaquisitionRequestOptions.testTransaction = false;
        sessionReaquisitionRequestOptions.followRedirects = true;
        sessionReaquisitionRequestOptions.handleSessions = false;

        logoutTestRequestOptions = new RequestOptions();
        logoutTestRequestOptions.reason = "Dead session profile building - logout";
        logoutTestRequestOptions.testRedirectTransactions = false;
        logoutTestRequestOptions.testTransaction = false;
        logoutTestRequestOptions.followRedirects = true;
        logoutTestRequestOptions.handleSessions = false;
        logoutTestRequestOptions.ignoreRestrictions = true;

        sessionHealthTestRequestOptions = new RequestOptions();
        sessionHealthTestRequestOptions.reason = "Session health test";
        sessionHealthTestRequestOptions.testRedirectTransactions = false;
        sessionHealthTestRequestOptions.testTransaction = false;
        sessionHealthTestRequestOptions.followRedirects = true;
        sessionHealthTestRequestOptions.handleSessions = false;
        sessionHealthTestRequestOptions.ignoreRestrictions = true;

    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof SessionState))
        {
            return false;
        }
        SessionState s1 = (SessionState) obj;
        return s1.username.equals(username) && s1.paramName.equals(paramName) && s1.domain.equalsIgnoreCase(domain);
    }

    public void generateDeadSessionProfile() throws InterruptedScanException
    {
        deadSessionSamples = new ResponseSamples(getSessionKey(), getFirstTransactionTemplate().getId());
        deadSessionSamples.setThreshold(LOGGED_OUT_COMPARE_THRESHOLD);
        deadSessionSamples.setFollowRedirects(true);

        processIncrementedSession(getFirstTransactionTemplate());
        if (firstAuthenticatedTransactionId > 0)
        {
            processLogoutPage();
        }
    }

    public final String getCurrentValue()
    {
        return currentValue;
    }

    public final int getFirstAuthenticatedTransactionId()
    {
        return firstAuthenticatedTransactionId;
    }

    private StandardHttpTransaction getFirstTransactionTemplate()
    {
        return Scan.getInstance().getTransactionRecord().getTransaction(firstAuthenticatedTransactionId > 0 ? firstAuthenticatedTransactionId : secondTransactionId);
    }

    public int getInitialTransaction()
    {
        return initialTransactionId;
    }

    public final int getLoginTransactionId()
    {
        return loginTransactionId;
    }

    private StandardHttpTransaction getReaquireTemplate()
    {
        return Scan.getInstance().getTransactionRecord().getTransaction(loginTransactionId > 0 ? loginTransactionId : initialTransactionId);
    }

    public final String getSessionIdName()
    {
        return paramName;
    }

    public String getSessionKey()
    {
        return buildSessionKey(username, paramName, domain);
    }

    // /**
    // * Force this session state onto the request in the specified transaction.
    // * @param transaction
    // */
    // public void addIntoTransaction(StandardHttpTransaction transaction)
    // {
    // removeFromTransaction(transaction);
    //
    // if (locations.contains(SessionLocation.COOKIE))
    // {
    // transaction.addCookie(new SerializableBasicCookie(paramName, currentValue));
    // return;
    // }
    // if (locations.contains(SessionLocation.URL_SESSION_ID))
    // {
    // String uri = transaction.getRequestWrapper().getURI();
    // try
    // {
    // uri = URIStringUtils.replaceSession(uri, currentValue);
    // transaction.getRequestWrapper().setURI(uri, false);
    // return;
    // }
    // catch (URISyntaxException e)
    // {
    // LOGGER.error("Weird problem updating session ID in URL: " + e.toString(), e);
    // }
    // }
    //
    // if (locations.contains(SessionLocation.URL_QUERY))
    // {
    // String uri = transaction.getRequestWrapper().getURI();
    // try
    // {
    // if (URIStringUtils.getQuery(uri).isEmpty())
    // {
    // uri += "?";
    // }
    // uri += UrlEncodingUtils.encodeForParam(paramName) + "=" +
    // UrlEncodingUtils.encodeForParam(currentValue);
    //
    // transaction.getRequestWrapper().setURI(uri, false);
    // return;
    // }
    // catch (URISyntaxException e)
    // {
    // LOGGER.error("Weird problem updating session ID in URL: " + e.toString(), e);
    // }
    // }
    //
    // if (locations.contains(SessionLocation.BODY))
    // {
    // Data b = transaction.getBodyData();
    // if (b instanceof DataContainer)
    // {
    // DataContainer container = (DataContainer) DataContainerUtils.resolveReferenceChain(transaction, bodyReferenceChain);
    //
    // container.addChild(child)
    // // container.addChild(child)
    // // }
    // // AbstractDataContainer<?> bodyPayload = PayloadParsers.getBodyPayload(transaction);
    // // if (bodyPayload instanceof UrlEncodedPostPayload)
    // // {
    // // UrlEncodedPostPayload b = (UrlEncodedPostPayload) bodyPayload;
    // // b.addParameter(paramName, currentValue);
    // }
    // }
    //
    // }

    public String getUsername()
    {
        return username;
    }

    private String getValueFromDOM(final StandardHttpTransaction transaction)
    {
        NodeList inputs = transaction.getResponseWrapper().getResponseDOM().getElementsByTagName("INPUT");
        for (int i = 0; i < inputs.getLength(); i++)
        {
            HTMLInputElementImpl input = (HTMLInputElementImpl) inputs.item(i);
            if (input.getName().equals(paramName))
            {
                if (input.getForm().getMethod().equalsIgnoreCase("POST"))
                {
                    locations.add(SessionLocation.BODY);
                }
                else
                {
                    locations.add(SessionLocation.URL_QUERY);
                }
                return input.getValue();
            }
        }
        return "";
    }

    public String getValueFromTransaction(final StandardHttpTransaction transaction)
    {
        for (SerializableBasicCookie cookie : HttpUtils.getSetCookies(transaction))
        {
            if (cookie.getName().equals(paramName))
            {
                locations.add(SessionLocation.COOKIE);
                return cookie.getValue();
            }
        }

        Header location = transaction.getResponseWrapper().getHeaders().getFirstHeader(HttpHeaders.LOCATION);
        if (location != null)
        {
            String value = parseUrl(location.getValue());
            if (!value.isEmpty())
            {
                return value;
            }
        }

        String body = new String(transaction.getResponseWrapper().getBody());
        Pattern p = Pattern.compile("(;?)" + Pattern.quote(paramName) + "=([^&\\x00-\\x20'\"?#]+)");
        Matcher m = p.matcher(body);
        if (m.matches())
        {
            if (m.group(1).equals(";"))
            {
                locations.add(SessionLocation.URL_SESSION_ID);
            }
            else
            {
                locations.add(SessionLocation.URL_QUERY);
            }
            return m.group(1);
        }

        String value = getValueFromDOM(transaction);
        if (!value.isEmpty())
        {
            return value;
        }

        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return getSessionKey().hashCode();
    }

    public final boolean isAuthenticated()
    {
        return authenticated;
    }

    public boolean isSessionHealthy() throws InterruptedScanException
    {
        if (damagedSession)
        {
            return true; // because we should just leave it alone
        }
        StandardHttpTransaction testSessionTransaction = getFirstTransactionTemplate().cloneFullRequest(TransactionSource.AUTHENTICATION, -1);
        testSessionTransaction.setRequestOptions(sessionHealthTestRequestOptions);
        try
        {
            testSessionTransaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error("Very weird problem trying test session. Assuming it's healthy: " + e.toString(), e);
            return true;
        }

        if (deadSessionSamples.matchesSamples(testSessionTransaction))
        {
            reaquireSession();
            if (damagedSession)
            {
                return true; // Because we have to pretend.
            }
            return false;
        }
        return true;
    }

    private String parseUrl(final String url)
    {
        String value;
        try
        {
            value = parseUrlQueryFormat(URIStringUtils.getSession(url));
            if (!value.isEmpty())
            {
                locations.add(SessionLocation.URL_SESSION_ID);
                return value;
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Weird problem with parsing URL: " + e.toString(), e);
        }

        try
        {
            value = parseUrlQueryFormat(URIStringUtils.getQuery(url));
            if (!value.isEmpty())
            {
                locations.add(SessionLocation.URL_QUERY);
                return value;
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Weird problem with parsing URL: " + e.toString(), e);
        }
        return "";
    }

    private String parseUrlQueryFormat(final String url)
    {
        Pattern p = Pattern.compile(Pattern.quote(paramName) + "=([^&?#]+)");
        Matcher m = p.matcher(url);
        if (m.matches())
        {
            return UrlEncodingUtils.decodeUrl(m.group(1));
        }
        return "";
    }

    private void processIncrementedSession(final StandardHttpTransaction templateTransaction) throws InterruptedScanException
    {
        StandardHttpTransaction[] incrementedTransactions = new StandardHttpTransaction[5];
        String newValue = currentValue;

        for (int i = 0; i < incrementedTransactions.length; i++)
        {
            incrementedTransactions[i] = templateTransaction.cloneFullRequest(TransactionSource.AUTHENTICATION, -1);
            SessionStates.getInstance().addIdToProfileBuilding(getSessionKey(), incrementedTransactions[i].getId());
            incrementedTransactions[i].setRequestOptions(deadSessionProfileBuildingRequestOptions);
            newValue = StringUtils.rotXWithHexSafety(newValue, 1);
            updateTransaction(incrementedTransactions[i], newValue);
            try
            {
                incrementedTransactions[i].execute();
            }
            catch (UnrequestableTransaction e)
            {
                LOGGER.error("Couldn't process incremented transaction for session state: " + e.toString(), e);
                return;
            }
            deadSessionSamples.addNewSample(incrementedTransactions[i]);
        }
    }

    private void processLogoutPage() throws InterruptedScanException
    {
        String logoutUri = Scan.getScanSettings().getLogOutUri();
        if (logoutUri == null || logoutUri.isEmpty())
        {
            LOGGER.debug("No logout URI provided");
            return;
        }
        StandardHttpTransaction firstAuthenticatedTransaction = Scan.getInstance().getTransactionRecord().getTransaction(firstAuthenticatedTransactionId);
        StandardHttpTransaction logoutRequest = firstAuthenticatedTransaction.cloneForSessionReuse(TransactionSource.AUTHENTICATION, -1);
        SessionStates.getInstance().addIdToProfileBuilding(getSessionKey(), logoutRequest.getId());
        logoutRequest.getRequestWrapper().setURI(logoutUri, true);
        logoutRequest.setRequestOptions(logoutTestRequestOptions.clone());
        try
        {
            logoutRequest.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error("Very odd since we asked for restrictions to be ignored: " + e.toString(), e);
            return;
        }

        deadSessionSamples.addNewSample(logoutRequest);
        reaquireSession();
    }

    protected void reaquireSession() throws InterruptedScanException
    {
        StandardHttpTransaction reaquireTransaction = getReaquireTemplate().cloneFullRequest(TransactionSource.AUTHENTICATION, -1);
        reaquireTransaction.setRequestOptions(sessionReaquisitionRequestOptions);
        try
        {
            reaquireTransaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error("Session reaquisition couldn't be executed: " + e.toString(), e);
            damagedSession = true;
            return;
        }

        String newValue = getValueFromTransaction(reaquireTransaction);
        if (newValue.isEmpty())
        {
            LOGGER.info("Session reaquisition failed to find new value. This may mean that the old session ID has been re-activated.\n" + reaquireTransaction.toString(), new RuntimeException());
            damagedSession = !isSessionHealthy();
            if (damagedSession)
            {
                LOGGER.info("It looks like the session is still dead. How sad.");
            }
            else
            {
                LOGGER.info("Yep, it looks like the session is fine now");
            }
            return;
        }

        if (deadSessionSamples.matchesSamples(reaquireTransaction))
        {
            LOGGER.warn("Session is still dead after reaquisition: " + reaquireTransaction.toString(), new RuntimeException());
            damagedSession = true;
            return;
        }

        currentValue = newValue;
    }

    public void removeFromTransaction(final StandardHttpTransaction transaction)
    {
        transaction.removeCookie(paramName);

        for (NameValuePairDataContainer param : DataContainerUtils.getAllNamedContanersByName(transaction.getTransactionContainer(), paramName))
        {
            param.removeFromCollection();
        }

        String uri = transaction.getRequestWrapper().getURI();
        String urlSession;
        try
        {
            urlSession = URIStringUtils.getSession(uri);
            if (!urlSession.isEmpty() && urlSession.contains(paramName))
            {
                transaction.getRequestWrapper().setURI(URIStringUtils.replaceQuery(uri, ""), false);
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Weird problem removing session from URL: " + e.toString(), e);
        }
    }

    public final void setAuthenticated(final boolean authenticated)
    {
        this.authenticated = authenticated;
    }

    public final void setCurrentValue(final String currentValue)
    {
        this.currentValue = currentValue;
    }

    public final void setFirstAuthenticatedTransactionId(final int firstAuthenticatedTransactionId)
    {
        this.firstAuthenticatedTransactionId = firstAuthenticatedTransactionId;
    }

    public final void setLoginTransactionId(final int loginTransactionId)
    {
        this.loginTransactionId = loginTransactionId;
    }

    public void updateTransaction(final StandardHttpTransaction transaction)
    {
        updateTransaction(transaction, currentValue);
    }

    public void updateTransaction(final StandardHttpTransaction transaction, final String newValue)
    {
        SerializableBasicCookie cookie = transaction.getCookie(paramName);
        if (cookie != null)
        {
            locations.add(SessionLocation.COOKIE);
            cookie.setValue(newValue);
        }

        for (NameValuePairDataContainer container : DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
        {
            if (DataUtils.getBytes(container).equals(paramName))
            {
                container.setValue(newValue.getBytes());
                if (container.isDataAncestor(transaction.getTransactionContainer().getUrlQueryDataContainer()))
                {
                    if (urlReferenceChain == null)
                    {
                        urlReferenceChain = DataContainerUtils.getReferenceChain(container.getParent());
                    }
                    locations.add(SessionLocation.URL_QUERY);
                }
                else if (transaction.getTransactionContainer().getBodyData() instanceof DataContainer && container.isDataAncestor((DataContainer<?>) transaction.getTransactionContainer().getBodyData()))
                {
                    if (bodyReferenceChain == null)
                    {
                        bodyReferenceChain = DataContainerUtils.getReferenceChain(container.getParent());
                    }
                    locations.add(SessionLocation.BODY);
                }
            }
        }

        String uri = transaction.getRequestWrapper().getURI();
        String urlSession;
        try
        {
            urlSession = URIStringUtils.getSession(uri);
            if (!urlSession.isEmpty() && urlSession.contains(paramName))
            {
                transaction.getRequestWrapper().setURI(URIStringUtils.replaceQuery(uri, paramName + "=" + newValue), false);
                locations.add(SessionLocation.URL_SESSION_ID);
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Weird problem updating session ID in URL: " + e.toString(), e);
        }
    }

}
