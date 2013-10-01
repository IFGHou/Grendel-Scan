/*
 * FormBased.java
 * 
 * Created on September 13, 2007, 9:29 PM
 * 
 * To change this template, choose Tools | Template Manager and open the
 * template in the editor.
 */

package com.grendelscan.requester.authentication;



import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;

/**
 * 
 * @author Administrator
 */
public class FormBasedAuthentication extends AuthenticationPackage
{

	protected StandardHttpTransaction loginTemplate;
	protected String userParameterName, passwordParameterName, method, postQuery;
	protected String uri;

	/**
	 * 
	 * @param sharedComponents
	 * @param loginTemplate
	 * @param userParameterName
	 * @param passwordParameterName
	 */
	public FormBasedAuthentication(StandardHttpTransaction loginTemplate, String userParameterName,
	        String passwordParameterName)
	{
		this.loginTemplate = loginTemplate;
		this.userParameterName = userParameterName;
		this.passwordParameterName = passwordParameterName;

	}

	public FormBasedAuthentication(String uri, String method, String postQuery, String userParameterName,
	        String passwordParameterName)
	{
		this.uri = uri;
		this.method = method;
		this.postQuery = postQuery;
		this.userParameterName = userParameterName;
		this.passwordParameterName = passwordParameterName;

	}

	
	/**
	 * This returns an HttpTransaction that represents a login
	 */
	@Override
	public StandardHttpTransaction createLoginTransaction(String username, String password, int jobId)
	{
		StandardHttpTransaction transaction = getLoginTemplate(jobId).cloneFullRequest(TransactionSource.AUTHENTICATION, jobId);
		transaction.setAuthenticated(true);
		DataContainerUtils.getFirstNamedContanerByName(transaction.getTransactionContainer(), userParameterName).setValue(username.getBytes());
		DataContainerUtils.getFirstNamedContanerByName(transaction.getTransactionContainer(), passwordParameterName).setValue(password.getBytes());
		transaction.setLoginTransaction(true);
		return transaction;
	}


	public StandardHttpTransaction getLoginTemplate(int jobId)
	{
		if (loginTemplate == null)
		{
            loginTemplate = new StandardHttpTransaction(TransactionSource.AUTHENTICATION, jobId);
            loginTemplate.getRequestWrapper().setURI(uri, true);
			if (method.equals("POST"))
			{
				loginTemplate.getRequestWrapper().setBody(postQuery.getBytes());
			}
		}
		return loginTemplate;
	}

	public String getPasswordParameterName()
	{
		return passwordParameterName;
	}

	public String getUserParameterName()
	{
		return userParameterName;
	}

	public String getMethod()
    {
    	return method;
    }

	public String getPostQuery()
    {
    	return postQuery;
    }

	public String getUri()
    {
    	return uri;
    }

	public final void setUserParameterName(String userParameterName)
	{
		this.userParameterName = userParameterName;
	}

	public final void setPasswordParameterName(String passwordParameterName)
	{
		this.passwordParameterName = passwordParameterName;
	}

	public final void setMethod(String method)
	{
		this.method = method;
	}

	public final void setPostQuery(String postQuery)
	{
		this.postQuery = postQuery;
	}

	public final void setUri(String uri)
	{
		this.uri = uri;
	}

}
