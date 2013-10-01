/*
 * AuthenticationBundle.java
 * 
 * Created on September 13, 2007, 9:16 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.scan.authentication;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

/**
 * 
 * @author Administrator
 */
public abstract class AuthenticationPackage
{
    public static final void setLastID(final int lastID)
    {
        AuthenticationPackage.lastID = lastID;
    }

    protected List<String> sessionIDNames;
    protected String loggedOutPageText;
    // protected Map<String, String> credentials;
    protected final int id;
    private static int lastID;

    private final static Object lastIDLock = new Object();

    /** Creates a new instance of AuthenticationBundle */
    public AuthenticationPackage()
    {
        sessionIDNames = new ArrayList<String>(1);
        // credentials = new HashMap<String, String>(1);
        synchronized (lastIDLock)
        {
            id = ++lastID;
        }
    }

    // public void addCredentialSet(String username, String password)
    // {
    // credentials.put(username, password);
    // Scan.getScanSettings().updateSettingsFile();
    // }

    // public Map<String, String> getReadOnlyCredentials()
    // {
    // return new HashMap<String, String>(credentials);
    // }

    public abstract StandardHttpTransaction createLoginTransaction(String username, String password, int jobId);

    public final int getId()
    {
        return id;
    }

    public String getLoggedOutPageText()
    {
        return loggedOutPageText;
    }

    // public final void setCredentials(Map<String, String> credentials)
    // {
    // this.credentials = credentials;
    // Scan.getScanSettings().updateSettingsFile();
    // }

    public List<String> getSessionIDNames()
    {
        return sessionIDNames;
    }

    public void setLoggedOutPageText(final String loggedOutPageText)
    {
        this.loggedOutPageText = loggedOutPageText;
    }

}
