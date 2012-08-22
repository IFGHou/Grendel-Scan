package com.grendelscan.GUI.http.transactionTable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URISyntaxException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.utils.URIStringUtils;

public class TransactionSummary
{
//	private static StringCache		stringCache;
//	static
//	{
//		stringCache = new StringCache();
//	}

	private final String						method, host, path, query, source, reason;		// Cached
																			// values
	private PropertyChangeSupport	propertyChangeSupport	= new PropertyChangeSupport(
																	this);
	// private int resultText;
	private long					time;
	private int						transactionID, responseCode;

	public TransactionSummary(String method, String host, String path, String query, String source, long time, int transactionID, int responseCode, String reason)
	{
//		this.method = stringCache.addValue(method);
//		this.host = stringCache.addValue(host);
//		this.path = stringCache.addValue(path);
//		this.query = stringCache.addValue(query);
//		this.source = stringCache.addValue(source);
//		this.reason = stringCache.addValue(reason);
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

	public TransactionSummary(StandardHttpTransaction transaction)
	{
		updateSummary(transaction);
		transactionID = transaction.getId();
//		method = stringCache.addValue(transaction.getRequestWrapper().getMethod());
//		host = stringCache.addValue(transaction.getRequestWrapper().getHost());
//		source = stringCache.addValue(transaction.getSource().getText());
//		reason = stringCache.addValue(transaction.getRequestOptions().reason);

		method = transaction.getRequestWrapper().getMethod();
		host = transaction.getRequestWrapper().getHost();
		source = transaction.getSource().getText();
		reason = transaction.getRequestOptions().reason;
		try
		{
//			path = stringCache.addValue(transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI()));
//			query = stringCache.addValue(URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()));

			path = transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI());
			query = URIStringUtils.getQuery(transaction.getRequestWrapper().getURI());
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing: " + e.toString(), e);
			Log.error(ise.toString(), ise);
			throw ise;
		}
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
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

	public int getTransactionID()
	{
		return transactionID;
	}

	// public String getResultText()
	// {
	// return stringCache.getString(resultText);
	// }

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public final void setResponseCode(int responseCode)
	{
		propertyChangeSupport.firePropertyChange("responseCode", this.responseCode, this.responseCode = responseCode);
	}

	public final void setTime(long time)
	{
		propertyChangeSupport.firePropertyChange("time", this.time, this.time = time);
	}

	public void updateSummary(StandardHttpTransaction transaction)
	{
		if (transaction.isSuccessfullExecution())
		{
			setResponseCode(transaction.getResponseWrapper().getStatusLine().getStatusCode());
			setTime(transaction.getRequestSentTime());
		}
		// resultText =
		// stringCache.addValue(transaction.getResponseWrapper().getStatusLine().toString());
	}

	public final String getReason()
	{
		return reason;
	}

}
