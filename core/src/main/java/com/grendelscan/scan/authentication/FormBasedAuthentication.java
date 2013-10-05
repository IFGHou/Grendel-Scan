/*
 * FormBased.java
 * 
 * Created on September 13, 2007, 9:29 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.scan.authentication;

import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;

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
    public FormBasedAuthentication(final StandardHttpTransaction loginTemplate, final String userParameterName, final String passwordParameterName)
    {
        this.loginTemplate = loginTemplate;
        this.userParameterName = userParameterName;
        this.passwordParameterName = passwordParameterName;

    }

    public FormBasedAuthentication(final String uri, final String method, final String postQuery, final String userParameterName, final String passwordParameterName)
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
    public StandardHttpTransaction createLoginTransaction(final String username, final String password, final int jobId)
    {
        StandardHttpTransaction transaction = getLoginTemplate(jobId).cloneFullRequest(TransactionSource.AUTHENTICATION, jobId);
        transaction.setAuthenticated(true);
        DataContainerUtils.getFirstNamedContanerByName(transaction.getTransactionContainer(), userParameterName).setValue(username.getBytes());
        DataContainerUtils.getFirstNamedContanerByName(transaction.getTransactionContainer(), passwordParameterName).setValue(password.getBytes());
        transaction.setLoginTransaction(true);
        return transaction;
    }

    public StandardHttpTransaction getLoginTemplate(final int jobId)
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

    public String getMethod()
    {
        return method;
    }

    public String getPasswordParameterName()
    {
        return passwordParameterName;
    }

    public String getPostQuery()
    {
        return postQuery;
    }

    public String getUri()
    {
        return uri;
    }

    public String getUserParameterName()
    {
        return userParameterName;
    }

    public final void setMethod(final String method)
    {
        this.method = method;
    }

    public final void setPasswordParameterName(final String passwordParameterName)
    {
        this.passwordParameterName = passwordParameterName;
    }

    public final void setPostQuery(final String postQuery)
    {
        this.postQuery = postQuery;
    }

    public final void setUri(final String uri)
    {
        this.uri = uri;
    }

    public final void setUserParameterName(final String userParameterName)
    {
        this.userParameterName = userParameterName;
    }

}
