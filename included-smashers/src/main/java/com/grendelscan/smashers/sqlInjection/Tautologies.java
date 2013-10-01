package com.grendelscan.smashers.sqlInjection;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.commons.http.responseCompare.HttpResponseScoreUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.ByRequestDataLocationTest;
import com.grendelscan.smashers.utils.SQLInjection;

public class Tautologies extends AbstractSmasher implements ByRequestDataLocationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Tautologies.class);

    @Override
    public String getDescription()
    {
        return "Injects SQL tautologies (such as \"or 1=1 �\") and anti-tautologies " + "to test for SQL injection. If new pages are accessed through the " + "attack, they are processed for new testing and or spidering. The "
                        + "test strings are defined in conf/sql_injection.conf.";
    }

    @Override
    public String getExperimentalText()
    {
        return "Although this module can find SQL injection vulnerabilities, the false-positive " + "rate can be high. More testing is required to tune the heuristics that identify " + "a vulnerable response.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SQL_INJECTION;
    }

    @Override
    public String getName()
    {
        return "SQL Tautologies";
    }

    @Override
    public boolean isExperimental()
    {
        return true;
    }

    private void report(final StandardHttpTransaction transaction, final String parameterName, final String tautology, final StandardHttpTransaction tautologyTransaction, final String antiTautology, final StandardHttpTransaction antiTautologyTransaction)
    {
        tautologyTransaction.writeToDisk();
        antiTautologyTransaction.writeToDisk();
        String briefDescription = "SQL injection found with tautology";
        String longDescription = "A SQL tautology (something that is always true, such as 1=1) was injected into the \"" + parameterName + "\" parameter, and appears to have been successful. The tautology was (without the external quotes) \""
                        + HtmlUtils.makeLink(tautologyTransaction.getSavedUrl(), tautology) + "\" and the anti-tautology was \"" + HtmlUtils.makeLink(antiTautologyTransaction.getSavedUrl(), antiTautology) + "\".";

        Finding event = new Finding(null, getName(), FindingSeverity.HIGH, transaction.getRequestWrapper().getURI(), "SQL Injection Detected", briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(),
                        SQLInjection.getSQLInjectionRecomendations(), SQLInjection.getSQLInjectionReferences());
        Scan.getInstance().getFindings().addFinding(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByHttpQueryParameterTest#testByQueryParameter (int, com.grendelscan.commons.http.payloads.QueryParameter)
     */
    @Override
    public void testByRequestData(final int transactionId, final DataReferenceChain chain, final int testJobId) throws InterruptedScanException
    {
        boolean binary = false;
        handlePause_isRunning();
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);

        ByteData datum;
        try
        {
            datum = (ByteData) DataContainerUtils.resolveReferenceChain(transaction.getTransactionContainer(), chain);
        }
        catch (ClassCastException exception)
        {
            throw new IllegalStateException("Problem following reference chain (" + chain.toString() + ")", exception);
        }

        if (!MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            binary = true;
        }

        boolean hit = false;
        if (new String(DataUtils.getBytes(datum)).matches("\\d+"))
        {
            try
            {
                hit = testTautologies(transaction, datum, SQLInjection.getNumericTautologies(), binary, testJobId);
                handlePause_isRunning();
                if (!hit)
                {
                    hit = testTautologies(transaction, datum, SQLInjection.getStringTautologies(), binary, testJobId);
                }
            }
            catch (UnrequestableTransaction e)
            {
                LOGGER.error(getName() + " request unrequestable (" + e.toString() + ")", e);
            }
        }

    }

    private boolean testTautologies(final StandardHttpTransaction transaction, final Data datum, final Map<String, String> tautologies, final boolean binary, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        boolean hit = false;
        for (String tautology : tautologies.keySet())
        {
            handlePause_isRunning();
            if (testTautologyPair(transaction, datum, tautology, tautologies.get(tautology), binary, testJobId))
            {
                hit = true;
                break;
            }
        }
        return hit;
    }

    private boolean testTautologyPair(final StandardHttpTransaction transaction, final Data datum, final String tautology, final String antiTautology, final boolean binary, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        boolean hit = false;
        RequestOptions testRequestOptions = requestOptions.clone();
        testRequestOptions.followRedirects = transaction.getRedirectChildId() > 0;
        StandardHttpTransaction firstTautologyTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        ByteData firstTautologyData = (ByteData) DataContainerUtils.resolveReferenceChain(firstTautologyTransaction.getTransactionContainer(), datum.getReferenceChain());
        firstTautologyData.setBytes(ArrayUtils.addAll(DataUtils.getBytes(datum), tautology.getBytes()));
        firstTautologyTransaction.setRequestOptions(testRequestOptions);
        firstTautologyTransaction.execute();

        handlePause_isRunning();
        // Run an identical test to see if they differ (some page elements may
        // be random). Use the difference score as
        // the basis for the taut / anti-taut score
        StandardHttpTransaction secondTautologyTransaction = firstTautologyTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        secondTautologyTransaction.execute();

        handlePause_isRunning();

        // float tautThreshold =
        // StringUtils.scoreStringDifferenceIgnoreCase(firstTautologyTransaction.getStrippedResponseText(),
        // secondTautologyTransaction.getStrippedResponseText(), 100) *
        // SQLInjection.getTautologyThreshold();
        double tautThreshold = HttpResponseScoreUtils.scoreResponseMatch(firstTautologyTransaction, secondTautologyTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) * SQLInjection.getTautologyThreshold();

        StandardHttpTransaction antiTautologyTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);

        Data antiTautologyData = DataContainerUtils.resolveReferenceChain(antiTautologyTransaction.getTransactionContainer(), datum.getReferenceChain());
        firstTautologyData.setBytes(ArrayUtils.addAll(DataUtils.getBytes(datum), antiTautology.getBytes()));

        antiTautologyTransaction.setRequestOptions(testRequestOptions);
        antiTautologyTransaction.execute();

        handlePause_isRunning();
        int score;
        if (binary)
        {
            if (Arrays.areEqual(firstTautologyTransaction.getResponseWrapper().getBody(), antiTautologyTransaction.getResponseWrapper().getBody()))
            {
                score = 100;
            }
            else
            {
                score = 0;
            }
        }
        else
        {
            // score =
            // StringUtils.scoreStringDifferenceIgnoreCase(firstTautologyTransaction.getStrippedResponseText(),
            // antiTautologyTransaction.getStrippedResponseText(), 100);
            score = HttpResponseScoreUtils.scoreResponseMatch(firstTautologyTransaction, antiTautologyTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects);
        }
        if (score < tautThreshold)
        {
            hit = true;
            // If the attack was successful, have it tested. It may lead to
            // other pages.
            Scan.getInstance().getCategorizerQueue().addTransaction(secondTautologyTransaction);
            report(transaction, datum.getReferenceChain().toString(), tautology, firstTautologyTransaction, antiTautology, antiTautologyTransaction);
        }

        return hit;
    }

}
