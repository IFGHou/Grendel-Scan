package com.grendelscan.requester;

import java.io.Serializable;

import org.apache.http.protocol.HttpContext;

public class RequestOptions implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public String reason = "Unknown reason";
	public boolean ignoreRestrictions = false;
	public boolean testTransaction = true;
	public boolean testRedirectTransactions = true;
	public boolean tokenSubmission = false;
	public HttpContext context;
	public boolean followRedirects = false;
	public boolean validateUriFormat = true;
	public boolean useCache = false;
	public boolean handleSessions = true;
	
	@Override
	public RequestOptions clone()
	{
		RequestOptions clone = new RequestOptions();
		clone.handleSessions = handleSessions;
		clone.useCache = useCache;
		clone.reason = "Clone of " + reason;
		clone.ignoreRestrictions = ignoreRestrictions;
		clone.testTransaction = testTransaction;
		clone.context = context;
		clone.followRedirects = followRedirects;
		clone.validateUriFormat = validateUriFormat;
		clone.testRedirectTransactions = testRedirectTransactions;
		clone.tokenSubmission = tokenSubmission;
		return clone;
	}
}
