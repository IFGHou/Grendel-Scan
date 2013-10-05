/**
 * 
 */
package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cobra_grendel.html.domimpl.HTMLFormElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.grendelscan.categorizers.tokens.StringToken;
import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlFormUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.ByHtmlFormTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHtmlFormTest;

/**
 * @author david
 * 
 */
public class ByHtmlFormCategorizer extends ByTokenCategorizer<StringToken>
{

    /**
     * @param categoryTestClass
     */
    public ByHtmlFormCategorizer()
    {
        super(ByHtmlFormTest.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.ByTokenCategorizer#getTokens(com.grendelscan.commons.http.transactions.StandardHttpTransaction)
     */
    @SuppressWarnings("unused")
    @Override
    protected List<StringToken> getTokens(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        List<StringToken> tokens = new ArrayList<StringToken>(1);
        if (MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            Document doc = transaction.getResponseWrapper().getResponseDOM();
            NodeList nodes = doc.getElementsByTagName("FORM");
            for (int index = 0; index < nodes.getLength(); index++)
            {
                handlePause_isRunning();
                HTMLFormElementImpl form = (HTMLFormElementImpl) nodes.item(index);
                tokens.add(new StringToken(HtmlFormUtils.getFormHash(form)));
            }
        }
        return tokens;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.ByTokenCategorizer#makeTestJobs(com.grendelscan.commons.http.transactions.StandardHttpTransaction, com.grendelscan.smashers.AbstractSmasher,
     * com.grendelscan.categorizers.tokens.Token)
     */
    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, final StringToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>(1);
        jobs.add(new ByHtmlFormTestJob(module.getClass(), transaction.getId(), token.getToken()));
        return jobs;
    }

}
