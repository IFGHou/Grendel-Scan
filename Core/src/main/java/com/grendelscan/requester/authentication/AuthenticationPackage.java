/*
 * AuthenticationBundle.java
 *
 * Created on September 13, 2007, 9:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.grendelscan.requester.authentication;

import java.util.ArrayList;
import java.util.List;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
/**
 *
 * @author Administrator
 */
public abstract class AuthenticationPackage 
{
    protected List<String> sessionIDNames;
    protected String loggedOutPageText;
//    protected Map<String, String> credentials;
    protected final int id;
    private static int lastID;
    private final static Object lastIDLock = new Object();
    
    /** Creates a new instance of AuthenticationBundle */
    public AuthenticationPackage() 
    {
		sessionIDNames = new ArrayList<String>(1);
//		credentials = new HashMap<String, String>(1);
		synchronized(lastIDLock)
		{
			id = ++lastID;
		}
    }
    
    public abstract StandardHttpTransaction createLoginTransaction(String username, String password, int jobId);
 
//    public void addCredentialSet(String username, String password)
//    {
//    	credentials.put(username, password);
//    	Scan.getScanSettings().updateSettingsFile();
//    }


//	public Map<String, String> getReadOnlyCredentials()
//    {
//    	return new HashMap<String, String>(credentials);
//    }

	public String getLoggedOutPageText()
    {
    	return loggedOutPageText;
    }

	public void setLoggedOutPageText(String loggedOutPageText)
    {
    	this.loggedOutPageText = loggedOutPageText;
    }

	public List<String> getSessionIDNames()
    {
    	return sessionIDNames;
    }

//	public final void setCredentials(Map<String, String> credentials)
//	{
//		this.credentials = credentials;
//		Scan.getScanSettings().updateSettingsFile();
//	}

	public static final void setLastID(int lastID)
	{
		AuthenticationPackage.lastID = lastID;
	}

	public final int getId()
	{
		return id;
	}
	
}


