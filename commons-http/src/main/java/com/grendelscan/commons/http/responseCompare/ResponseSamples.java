/**
 * 
 */
package com.grendelscan.commons.http.responseCompare;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

/**
 * @author david
 * 
 */
public class ResponseSamples
{
    private final DatabaseBackedList<StandardHttpTransaction> samples;
    private int threshold;
    private boolean followRedirects;
    private final int baseTemplateTransactionId;

    public ResponseSamples(final String name, final int baseTemplateTransactionId)
    {
        samples = new DatabaseBackedList<StandardHttpTransaction>("response-code-samples--" + name);
        threshold = 85;
        followRedirects = false;
        this.baseTemplateTransactionId = baseTemplateTransactionId;
    }

    public void addNewSample(final StandardHttpTransaction sample)
    {
        synchronized (samples)
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
            for (StandardHttpTransaction t2 : samples)
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

    public int getSampleCount()
    {
        return samples.size();
    }

    public final int getThreshold()
    {
        return threshold;
    }

    public final boolean isFollowRedirects()
    {
        return followRedirects;
    }

    public boolean matchesSamples(final StandardHttpTransaction target)
    {
        synchronized (samples)
        {
            if (!target.isSuccessfullExecution())
            {
                throw new IllegalArgumentException("Transaction must be executed to compare the response");
            }

            for (StandardHttpTransaction sample : samples)
            {
                if (HttpResponseScoreUtils.scoreResponseMatch(target, sample, 100, Scan.getScanSettings().isParseHtmlDom(), followRedirects) >= threshold)
                {
                    return true;
                }
            }
            return false;
        }
    }

    public final void setFollowRedirects(final boolean followRedirects)
    {
        this.followRedirects = followRedirects;
    }

    public final void setThreshold(final int threshold)
    {
        this.threshold = threshold;
    }

}
