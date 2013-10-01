package com.grendelscan.utils.ResponseCompare;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpTransactionMatchCriteriaWeights
{
	private int responseCode;
	private int locationHeader;
	private int mimeType;
	private int setCookieName;
	private int textNodes;
	private Map<String, Integer> tagCounts;
	
	public int getResponseCode()
    {
    	return responseCode;
    }

	public void setResponseCode(int responseCode)
    {
    	this.responseCode = responseCode;
    }

	public int getLocationHeader()
    {
    	return locationHeader;
    }

	public void setLocationHeader(int locationHeader)
    {
    	this.locationHeader = locationHeader;
    }

	public int getMimeType()
    {
    	return mimeType;
    }

	public void setMimeType(int mimeType)
    {
    	this.mimeType = mimeType;
    }

	public int getSetCookieName()
    {
    	return setCookieName;
    }

	public void setSetCookieName(int cookieName)
    {
    	this.setCookieName = cookieName;
    }

	public int getTextNodes()
    {
    	return textNodes;
    }

	public void setTextNodes(int textNodes)
    {
    	this.textNodes = textNodes;
    }

	public Map<String, Integer> getTagCounts()
    {
    	return tagCounts;
    }

	public HttpTransactionMatchCriteriaWeights()
	{
		tagCounts = Collections.synchronizedMap(new HashMap<String, Integer>(1));
		initDefaults();
	}
	
	private void initDefaults()
	{
		/*
		 * The first group is low because the compare algorithm stops if they don't match.
		 */
		responseCode = 10; 
		mimeType = 10;
		
		
		locationHeader = 100;
		setCookieName = 100;
		textNodes = 50;

		tagCounts.put("APPLET", 50);
		tagCounts.put("OBJECT", 50);
		tagCounts.put("EMBED", 50);
		tagCounts.put("TABLE", 30);
		tagCounts.put("TR", 15);
		tagCounts.put("SCRIPT", 20);
		tagCounts.put("A", 10);
		tagCounts.put("LINK", 10);
		tagCounts.put("IMG", 10);
	}
	
//	public int getMaxScore(AbstractHttpTransaction transactionA, AbstractHttpTransaction transactionB)
//	{
//		int score = 0;
//		score += responseCode;
//		score += locationHeader;
//		score += setCookieName;
//		score += mimeType;
//		if (transaction.getResponseWrapper().getBody().length > 0 && MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
//		{
//			score += textNodes;
//			for (Integer value: tagCounts.values())
//			{
//				score += value;
//			}
//		}
//		return score;
//	}

}
