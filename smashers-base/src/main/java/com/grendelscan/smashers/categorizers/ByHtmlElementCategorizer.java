package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.cobra_grendel.html.domimpl.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlNodeUtilities;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedSet;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.MultiSetTransactionCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByHtmlElementTestJob;
import com.grendelscan.smashers.types.ByHtmlElementTest;

public class ByHtmlElementCategorizer extends MultiSetTransactionCategorizer
{
    private final DatabaseBackedSet<String> elementHashes;

    public ByHtmlElementCategorizer()
    {
        super(ByHtmlElementTest.class);
        elementHashes = new DatabaseBackedSet<String>("html_element_categorizer_hashes", 500);
    }

    @Override
    public Map<AbstractSmasher, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();

        if (MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            Document doc = transaction.getResponseWrapper().getResponseDOM();

            for (Object tagnameObject : modulesByType.keySet())
            {
                String tagName = (String) tagnameObject;
                NodeList nodes = doc.getElementsByTagName(tagName);
                for (int index = 0; index < nodes.getLength(); index++)
                {
                    handlePause_isRunning();
                    HTMLElementImpl element = (HTMLElementImpl) nodes.item(index).cloneNode(true);
                    HtmlUtils.StripAllFamily(element);
                    String hash = HtmlNodeUtilities.getNodeHash(element, true);
                    if (!elementHashes.contains(hash))
                    {
                        elementHashes.add(hash);
                        for (AbstractSmasher module : modulesByType.get(tagName))
                        {
                            handlePause_isRunning();
                            ByHtmlElementTestJob testJob = new ByHtmlElementTestJob(module.getClass(), transaction.getId(), element, tagName);
                            addJobToCollection(testJob, module, tests);
                        }
                    }
                }
            }
        }
        return tests;
    }

    @Override
    public String[] getModuleTypes(final AbstractSmasher module)
    {
        ByHtmlElementTest test = (ByHtmlElementTest) module;
        return test.getHtmlElements();
    }

    @SuppressWarnings("unused")
    @Override
    /**
     * This isn't used by ByHtmlElementCategorizer because it implements its own analyze transaction
     */
    protected Set<String> getTransactionTypeStrings(final StandardHttpTransaction transaction)
    {
        throw new NotImplementedException();
    }

    @SuppressWarnings("unused")
    @Override
    /**
     * This isn't used by ByHtmlElementCategorizer because it implements its own analyze transaction
     */
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractSmasher module, final String type)
    {
        throw new NotImplementedException();
    }
}
