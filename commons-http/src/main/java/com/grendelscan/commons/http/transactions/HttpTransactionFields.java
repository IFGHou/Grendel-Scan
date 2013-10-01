package com.grendelscan.commons.http.transactions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;

import com.grendelscan.commons.http.CookieJar;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.dataHandling.containers.TransactionContainer;
import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;

public abstract class HttpTransactionFields implements Serializable
{

    private static transient long currentSecond;
    private static final transient Object idLock = new Object();
    private static transient Integer lastID = 0;
    private static transient int perSecondTransactionCount;
    private static transient final Object perSecondTransactionCountLock = new Object();
    private static final transient Pattern queryParameterPattern = Pattern.compile("([^=&]++)=([^&]*+)");
    private static final transient long serialVersionUID = -3126496659368918711L;
    private static transient int totalExecutions = 0;
    private static final transient Integer totalExecutionsLock = 1;

    public static final long getCurrentSecond()
    {
        return currentSecond;
    }

    public static final Object getIdlock()
    {
        return idLock;
    }

    public static final Integer getLastID()
    {
        return lastID;
    }

    public static final int getPerSecondTransactionCount()
    {
        return perSecondTransactionCount;
    }

    public static final Pattern getQueryparameterpattern()
    {
        return queryParameterPattern;
    }

    public static final long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public static final Integer getTotalExecutions()
    {
        return totalExecutions;
    }

    public static final Integer getTotalexecutionslock()
    {
        return totalExecutionsLock;
    }

    public static final void incPerSecondTransactionCount()
    {
        synchronized (perSecondTransactionCountLock)
        {
            perSecondTransactionCount++;
        }
    }

    public synchronized static final void incTotalExecutions()
    {
        totalExecutions++;
    }

    public static final void setCurrentSecond(final long currentSecond)
    {
        HttpTransactionFields.currentSecond = currentSecond;
    }

    public static void setLastID(final int lastID)
    {
        synchronized (idLock)
        {
            HttpTransactionFields.lastID = lastID;
        }
    }

    public static final void setLastID(final Integer lastID)
    {
        HttpTransactionFields.lastID = lastID;
    }

    public static final void setPerSecondTransactionCount(final int perSecondTransactionCount)
    {
        synchronized (perSecondTransactionCountLock)
        {
            HttpTransactionFields.perSecondTransactionCount = perSecondTransactionCount;
        }
    }

    private boolean authenticated;
    private int authenticationPackageID;
    private CookieJar cookieJar;
    private int id;
    private boolean loginTransaction;
    private int redirectChildId;
    private int redirectCount;
    private int redirectParentId;
    private int refererId;
    private int predecessorId;
    private int requestDepth;
    private RequestOptions requestOptions;
    private long requestSentTime;
    private HttpRequestWrapper requestWrapper;
    private long responseRecievedTime;
    // private boolean saved;
    private TransactionSource source;
    private boolean successfullExecution;
    private String username = "";
    private boolean writtenToDisk;
    protected transient CookieJar childrensCookieJar;
    protected transient CookieOrigin cookieOrigin;
    protected int logicalResponseCode;
    protected HttpResponseWrapper responseWrapper;
    protected boolean unrequestable;
    protected transient boolean loadedFromDisk;
    protected Set<String> sessionNames;
    private int testJobId;
    private int[] refererChain;
    protected transient TransactionContainer transactionContainer;

    /**
     * For deserialization
     */
    protected HttpTransactionFields()
    {
    }

    protected HttpTransactionFields(final TransactionSource source, final int jobNumber)
    {
        testJobId = jobNumber;
        if (source == null)
        {
            throw new IllegalArgumentException("Source cannot be null");
        }
        synchronized (idLock)
        {
            id = ++lastID;
        }
        requestWrapper = new HttpRequestWrapper(id);
        this.source = source;
        cookieJar = new CookieJar();
    }

    protected void cloneForReferer(final HttpTransactionFields target)
    {
        cloneForSessionReuse(target);
        target.setRequestDepth(requestDepth + 1);
        target.refererId = id;
        if (refererChain == null)
        {
            target.refererChain = new int[] { id };
        }
        else
        {
            target.refererChain = Arrays.copyOf(refererChain, refererChain.length + 1);
            target.refererChain[refererChain.length] = id;
        }
    }

    protected void cloneForSessionReuse(final HttpTransactionFields target)
    {
        target.setAuthenticationPackageID(authenticationPackageID);
        target.setCookieJar(getChildrensCookieJar());
        target.setAuthenticated(isAuthenticated() || isLoginTransaction());
        target.predecessorId = id;
        target.setUsername(username);
        if (refererChain != null)
        {
            target.refererChain = Arrays.copyOf(refererChain, refererChain.length);
        }
    }

    /**
     * Doesn't copy the response
     */
    protected void cloneFullRequest(final HttpTransactionFields target)
    {
        target.setAuthenticationPackageID(authenticationPackageID);
        target.setCookieOrigin(getCookieOrigin());
        target.setCookieJar(getCookieJar().clone());
        target.setLoginTransaction(loginTransaction);
        target.setAuthenticated(authenticated);
        target.refererId = refererId;
        target.predecessorId = id;
        target.setRequestDepth(requestDepth);
        target.setUsername(username);
        target.requestWrapper = requestWrapper.clone(target.getId());
        target.requestOptions = requestOptions.clone();
        if (refererChain != null)
        {
            target.refererChain = Arrays.copyOf(refererChain, refererChain.length);
        }
        if (sessionNames != null)
        {
            target.sessionNames = new HashSet<String>(sessionNames);
        }
    }

    /**
     * 
     * @return The authentication package associated with the transaction's request. By default, this is the same as the referer's authentication package.
     */
    public final int getAuthenticationPackageID()
    {
        return authenticationPackageID;
    }

    public CookieJar getChildrensCookieJar()
    {
        if (childrensCookieJar == null)
        {
            childrensCookieJar = new CookieJar();
            childrensCookieJar.addCookies(getCookieJar());
            if (getResponseWrapper() != null)
            {
                for (Cookie cookie : HttpUtils.getSetCookies(this))
                {
                    childrensCookieJar.addCookie(cookie);
                }
            }
        }

        return childrensCookieJar;
    }

    public final CookieJar getCookieJar()
    {
        return cookieJar;
    }

    public abstract CookieOrigin getCookieOrigin();

    public final int getId()
    {
        return id;
    }

    public final int getPredecessorId()
    {
        return predecessorId;
    }

    public final int getRedirectChildId()
    {
        return redirectChildId;
    }

    public final int getRedirectCount()
    {
        return redirectCount;
    }

    public final int getRedirectParentId()
    {
        return redirectParentId;
    }

    public final int[] getRefererChain()
    {
        return refererChain;
    }

    public final int getRefererId()
    {
        return refererId;
    }

    public final int getRequestDepth()
    {
        return requestDepth;
    }

    public final RequestOptions getRequestOptions()
    {
        return requestOptions;
    }

    public final long getRequestSentTime()
    {
        return requestSentTime;
    }

    public final HttpRequestWrapper getRequestWrapper()
    {
        return requestWrapper;
    }

    public final long getResponseRecievedTime()
    {
        return responseRecievedTime;
    }

    public final HttpResponseWrapper getResponseWrapper()
    {
        return responseWrapper;
    }

    public final Set<String> getSessionStateNames()
    {
        if (sessionNames == null)
        {
            sessionNames = new HashSet<String>(1);
        }
        return sessionNames;
    }

    public final TransactionSource getSource()
    {
        return source;
    }

    public final int getTestJobId()
    {
        return testJobId;
    }

    public synchronized TransactionContainer getTransactionContainer()
    {
        if (transactionContainer == null)
        {
            transactionContainer = new TransactionContainer(getId());
        }
        return transactionContainer;
    }

    public final String getUsername()
    {
        return username;
    }

    public final boolean isAuthenticated()
    {
        return authenticated;
    }

    public final boolean isLoginTransaction()
    {
        return loginTransaction;
    }

    public final boolean isSuccessfullExecution()
    {
        return successfullExecution;
    }

    public final boolean isUnrequestable()
    {
        return unrequestable;
    }

    public final boolean isWrittenToDisk()
    {
        return writtenToDisk;
    }

    public final void setAuthenticated(final boolean authenticated)
    {
        this.authenticated = authenticated;
    }

    public final void setAuthenticationPackageID(final int authenticationPackageID)
    {
        this.authenticationPackageID = authenticationPackageID;
    }

    public final void setChildrensCookieJar(final CookieJar childrensCookieJar)
    {
        this.childrensCookieJar = childrensCookieJar;
    }

    public final void setCookieJar(final CookieJar cookieJar)
    {
        this.cookieJar = cookieJar;
    }

    public final void setCookieOrigin(final CookieOrigin cookieOrigin)
    {
        this.cookieOrigin = cookieOrigin;
    }

    public final void setId(final int id)
    {
        this.id = id;
    }

    public final void setLoadedFromDisk(final boolean loadedFromDisk)
    {
        this.loadedFromDisk = loadedFromDisk;
    }

    public final void setLogicalResponseCode(final int logicalResponseCode)
    {
        this.logicalResponseCode = logicalResponseCode;
    }

    public final void setLoginTransaction(final boolean loginTransaction)
    {
        this.loginTransaction = loginTransaction;
    }

    public final void setRedirectChildId(final int redirectChildId)
    {
        this.redirectChildId = redirectChildId;
    }

    public final void setRedirectCount(final int redirectCount)
    {
        this.redirectCount = redirectCount;
    }

    public final void setRedirectParentId(final int redirectParentId)
    {
        this.redirectParentId = redirectParentId;
    }

    public final void setRequestDepth(final int requestDepth)
    {
        this.requestDepth = requestDepth;
    }

    public final void setRequestOptions(final RequestOptions requestOptions)
    {
        this.requestOptions = requestOptions;
    }

    public final void setRequestSentTime(final long requestSentTime)
    {
        this.requestSentTime = requestSentTime;
    }

    public final void setRequestWrapper(final HttpRequestWrapper requestWrapper)
    {
        this.requestWrapper = requestWrapper;
    }

    public final void setResponseRecievedTime(final long responseRecievedTime)
    {
        this.responseRecievedTime = responseRecievedTime;
    }

    public final void setSuccessfullExecution(final boolean successfullExecution)
    {
        this.successfullExecution = successfullExecution;
    }

    public final void setUnrequestable(final boolean unrequestable)
    {
        this.unrequestable = unrequestable;
    }

    public final void setUsername(final String username)
    {
        this.username = username;
    }

    public final void setWrittenToDisk(final boolean writtenToDisk)
    {
        this.writtenToDisk = writtenToDisk;
    }

}
