package com.grendelscan.commons.http.responseCompare;

public class ResponseCompareScore
{
    private int maxScore = 0;
    private int score = 0;

    double getPercentage()
    {
        if (score < 0)
        {
            return 0;
        }
        return (double) score / (double) maxScore;
    }

    void incMaxScore(final int inc)
    {
        maxScore += inc;
    }

    void incScore(final int inc)
    {
        score += inc;
    }
}
