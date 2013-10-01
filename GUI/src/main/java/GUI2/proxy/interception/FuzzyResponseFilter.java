package com.grendelscan.GUI2.proxy.interception;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;

public class FuzzyResponseFilter extends InterceptFilter
{

	protected int threshold;

	protected StandardHttpTransaction baseTransaction;


	public FuzzyResponseFilter(boolean matches, boolean intercept, int threshold, StandardHttpTransaction baseTransaction)
    {
	    super(matches, intercept);
	    this.threshold = threshold;
	    this.baseTransaction = baseTransaction;
    }

	@Override
    public String getDisplayText()
    {
	    return "Transaction " + getTransactionID() + " @ " + threshold + "%";
    }

	@Override
    public InterceptFilterLocation getLocation()
    {
	    return InterceptFilterLocation.FUZZY_RESPONSE_COMPARE;
    }


	@Override
    public boolean performAction(StandardHttpTransaction transaction)
    {
		return isHit(transaction) == matches;
    }
	
	private boolean isHit(StandardHttpTransaction transaction)
	{
		int score = HttpResponseScoreUtils.scoreResponseMatch(baseTransaction, transaction, 99, 
				Scan.getScanSettings().isParseHtmlDom(), false);
		return score >= threshold;
	}

	public int getThreshold()
    {
    	return threshold;
    }
	
	public int getTransactionID()
	{
		return baseTransaction.getId();
	}

}
