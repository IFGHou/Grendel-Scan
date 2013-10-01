/**
 * 
 */
package com.grendelscan.commons.http.responseCompare;

import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;

/**
 * @author david
 *
 */
public class ResponseSamples
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSamples.class);
	private final DatabaseBackedList<StandardHttpTransaction> samples;
	private int threshold;
	private boolean followRedirects;
	private final int baseTemplateTransactionId; 
	
	public ResponseSamples(String name, int baseTemplateTransactionId)
	{
		samples = new DatabaseBackedList<StandardHttpTransaction>("response-code-samples--" + name);
		threshold = 85;
		followRedirects = false;
		this.baseTemplateTransactionId = baseTemplateTransactionId;
	}

	public int getSampleCount()
	{
		return samples.size();
	}
	
	public void addNewSample(StandardHttpTransaction sample)
	{
		synchronized(samples)
		{
			if (baseTemplateTransactionId > 0)
			{
				StandardHttpTransaction base = Scan.getInstance().getTransactionRecord().getTransaction(baseTemplateTransactionId);
				if (HttpResponseScoreUtils.scoreResponseMatch(sample, base, 100, true, true) >= threshold)
				{
					return; // transaction too similar to base
				}
			}
			boolean uniqueResponse = false;
			for (StandardHttpTransaction t2: samples)
			{
				if (HttpResponseScoreUtils.scoreResponseMatch(sample, t2, 100, true, true) < threshold)
				{
					uniqueResponse = true;
					break;
				}
			}
			if (uniqueResponse || samples.size() == 0)
			{
				samples.add(sample);
			}
		}
	}

	public boolean matchesSamples(StandardHttpTransaction target)
	{
		synchronized(samples)
		{
			if (!target.isSuccessfullExecution())
			{
				throw new IllegalArgumentException("Transaction must be executed to compare the response");
			}
			
			for (StandardHttpTransaction sample: samples)
			{
				if (HttpResponseScoreUtils.scoreResponseMatch(target, sample, 100, Scan.getScanSettings().isParseHtmlDom(), followRedirects) >= threshold)
				{
					return true;
				}
			}
			return false;
		}
	}

	public final int getThreshold()
	{
		return threshold;
	}

	public final void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}

	public final boolean isFollowRedirects()
	{
		return followRedirects;
	}

	public final void setFollowRedirects(boolean followRedirects)
	{
		this.followRedirects = followRedirects;
	}
	
}
