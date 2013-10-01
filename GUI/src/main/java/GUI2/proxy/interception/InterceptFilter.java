package com.grendelscan.GUI2.proxy.interception;


import com.grendelscan.requester.http.transactions.StandardHttpTransaction;

public abstract class InterceptFilter
{
	
	public InterceptFilter(boolean matches, boolean intercept)
    {
	    super();
	    this.matches = matches;
	    this.intercept = intercept;
    }
	public boolean isIntercept()
    {
    	return intercept;
    }
	public boolean isMatches()
    {
    	return matches;
    }
	protected boolean matches;
	protected boolean intercept;

	
	abstract public InterceptFilterLocation getLocation();
	abstract public String getDisplayText();
	abstract public boolean performAction(StandardHttpTransaction transaction);
	
	
}
