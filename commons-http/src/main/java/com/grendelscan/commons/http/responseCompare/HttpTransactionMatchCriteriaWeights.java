package com.grendelscan.commons.http.responseCompare;

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
    private final Map<String, Integer> tagCounts;

    public HttpTransactionMatchCriteriaWeights()
    {
        tagCounts = Collections.synchronizedMap(new HashMap<String, Integer>(1));
        initDefaults();
    }

    public int getLocationHeader()
    {
        return locationHeader;
    }

    public int getMimeType()
    {
        return mimeType;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public int getSetCookieName()
    {
        return setCookieName;
    }

    public Map<String, Integer> getTagCounts()
    {
        return tagCounts;
    }

    public int getTextNodes()
    {
        return textNodes;
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

    public void setLocationHeader(final int locationHeader)
    {
        this.locationHeader = locationHeader;
    }

    public void setMimeType(final int mimeType)
    {
        this.mimeType = mimeType;
    }

    public void setResponseCode(final int responseCode)
    {
        this.responseCode = responseCode;
    }

    public void setSetCookieName(final int cookieName)
    {
        setCookieName = cookieName;
    }

    public void setTextNodes(final int textNodes)
    {
        this.textNodes = textNodes;
    }

    // public int getMaxScore(AbstractHttpTransaction transactionA, AbstractHttpTransaction transactionB)
    // {
    // int score = 0;
    // score += responseCode;
    // score += locationHeader;
    // score += setCookieName;
    // score += mimeType;
    // if (transaction.getResponseWrapper().getBody().length > 0 && MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
    // {
    // score += textNodes;
    // for (Integer value: tagCounts.values())
    // {
    // score += value;
    // }
    // }
    // return score;
    // }

}
