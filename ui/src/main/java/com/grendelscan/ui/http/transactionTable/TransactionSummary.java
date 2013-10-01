package com.grendelscan.ui.http.transactionTable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

public class TransactionSummary
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSummary.class);

    // private static StringCache stringCache;
    // static
    // {
    // stringCache = new StringCache();
    // }

    private final String method, host, path, query, source, reason; // Cached
    // values
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    // private int resultText;
    private long time;
    private final int transactionID;

    private int responseCode;

    public TransactionSummary(final StandardHttpTransaction transaction)
    {
        updateSummary(transaction);
        transactionID = transaction.getId();
        // method = stringCache.addValue(transaction.getRequestWrapper().getMethod());
        // host = stringCache.addValue(transaction.getRequestWrapper().getHost());
        // source = stringCache.addValue(transaction.getSource().getText());
        // reason = stringCache.addValue(transaction.getRequestOptions().reason);

        method = transaction.getRequestWrapper().getMethod();
        host = transaction.getRequestWrapper().getHost();
        source = transaction.getSource().getText();
        reason = transaction.getRequestOptions().reason;
        try
        {
            // path = stringCache.addValue(transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI()));
            // query = stringCache.addValue(URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()));

            path = transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI());
            query = URIStringUtils.getQuery(transaction.getRequestWrapper().getURI());
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing: " + e.toString(), e);
            LOGGER.error(ise.toString(), ise);
            throw ise;
        }
    }

    public TransactionSummary(final String method, final String host, final String path, final String query, final String source, final long time, final int transactionID, final int responseCode, final String reason)
    {
        // this.method = stringCache.addValue(method);
        // this.host = stringCache.addValue(host);
        // this.path = stringCache.addValue(path);
        // this.query = stringCache.addValue(query);
        // this.source = stringCache.addValue(source);
        // this.reason = stringCache.addValue(reason);
        this.method = method;
        this.host = host;
        this.path = path;
        this.query = query;
        this.source = source;
        this.reason = reason;
        this.time = time;
        this.transactionID = transactionID;
        this.responseCode = responseCode;
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public String getHost()
    {
        return host;
    }

    public String getMethod()
    {
        return method;
    }

    public String getPath()
    {
        return path;
    }

    public String getQuery()
    {
        return query;
    }

    public final String getReason()
    {
        return reason;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public String getSource()
    {
        return source;
    }

    public long getTime()
    {
        return time;
    }

    // public String getResultText()
    // {
    // return stringCache.getString(resultText);
    // }

    public int getTransactionID()
    {
        return transactionID;
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public final void setResponseCode(final int responseCode)
    {
        propertyChangeSupport.firePropertyChange("responseCode", this.responseCode, this.responseCode = responseCode);
    }

    public final void setTime(final long time)
    {
        propertyChangeSupport.firePropertyChange("time", this.time, this.time = time);
    }

    public void updateSummary(final StandardHttpTransaction transaction)
    {
        if (transaction.isSuccessfullExecution())
        {
            setResponseCode(transaction.getResponseWrapper().getStatusLine().getStatusCode());
            setTime(transaction.getRequestSentTime());
        }
        // resultText =
        // stringCache.addValue(transaction.getResponseWrapper().getStatusLine().toString());
    }

}
