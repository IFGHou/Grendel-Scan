package com.grendelscan.testing.modules.impl.sqlInjection;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.commons.http.responseCompare.HttpResponseScoreUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByRequestDataLocationTest;
import com.grendelscan.testing.utils.SQLInjection;

public class SingleQuoteQuery extends AbstractTestModule implements ByRequestDataLocationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleQuoteQuery.class);

    @Override
    public String getDescription()
    {
        return "Appends a single quote onto a parameter and looks for a SQL " + "error message in the response. The SQL error patterns are " + "defined in conf/sql_injection.conf. Currently, errors from "
                        + "IBM DB2, Microsoft Access, Microsoft SQL Server, MySQL, " + "Oracle, PostgreSQL, and generic ODBC & OLE/DB errors are " + "detected.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SQL_INJECTION;
    }

    @Override
    public String getName()
    {
        return "Error-based SQL Injection";
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private void recordErrorMessageFinding(final StandardHttpTransaction transaction, final String parameterName, final String platform)
    {
        transaction.writeToDisk();
        String briefDescription = "A SQL error message was detected";
        String longDescription = "When a single quote (') was appended to the parameter listed below, a SQL " + "error message was returned. This could indicate a SQL injection vulnerability.<br>\n<br>\n" + "URL: "
                        + URIStringUtils.getFileUri(transaction.getRequestWrapper().getURI()) + "<br>\n" + "Parameter name: " + parameterName + "<br>\n" + "Platform: " + platform + "<br>\n" + "Transaction: "
                        + HtmlUtils.makeLink(transaction.getSavedUrl(), String.valueOf(transaction.getId())) + "<br>\n" + "<br>\n";
        Finding event = new Finding(null, getName(), FindingSeverity.HIGH, "See description", "Possible SQL Injection", briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(), SQLInjection.getSQLInjectionRecomendations(),
                        SQLInjection.getSQLInjectionReferences());
        Scan.getInstance().getFindings().addFinding(event);
    }

    private void recordQuoteFinding(final StandardHttpTransaction oneQuoteTransaction, final StandardHttpTransaction twoQuoteTransaction, final String parameterName)
    {
        oneQuoteTransaction.writeToDisk();
        twoQuoteTransaction.writeToDisk();
        String briefDescription = "Possible SQL injection was detected";
        String longDescription = "A significant difference was noted between a single quote (') being appended to the parameter listed below, " + "and two single quotes being appended. This could indicate a SQL injection vulnerability.<br>\n<br>\n" + "URL: "
                        + URIStringUtils.getFileUri(oneQuoteTransaction.getRequestWrapper().getURI()) + "<br>\n" + "Parameter name: " + parameterName + "<br>\n" + "Transactions: "
                        + HtmlUtils.makeLink(oneQuoteTransaction.getSavedUrl(), String.valueOf(oneQuoteTransaction.getId())) + "<br>\n" + HtmlUtils.makeLink(twoQuoteTransaction.getSavedUrl(), String.valueOf(twoQuoteTransaction.getId())) + "<br>\n" + "<br>\n";
        Finding event = new Finding(null, getName(), FindingSeverity.HIGH, "See description", "Possible SQL Injection", briefDescription, longDescription, SQLInjection.getSQLInjectionImpact(), SQLInjection.getSQLInjectionRecomendations(),
                        SQLInjection.getSQLInjectionReferences());
        Scan.getInstance().getFindings().addFinding(event);
    }

    private StandardHttpTransaction runTestTransaction(final StandardHttpTransaction transaction, final ByteData queryDatum, final String attack, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        ByteData testTransactionQueryDatum = (ByteData) DataContainerUtils.resolveReferenceChain(testTransaction.getTransactionContainer(), queryDatum.getReferenceChain());

        testTransactionQueryDatum.setBytes(ArrayUtils.addAll(DataUtils.getBytes(queryDatum), attack.getBytes()));
        testTransaction.setRequestOptions(requestOptions);
        testTransaction.execute();
        return testTransaction;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByHttpQueryParameterTest#testByQueryParameter (com.grendelscan.commons.http.payloads.QueryParameter)
     */
    @Override
    public void testByRequestData(final int transactionId, final DataReferenceChain chain, final int testJobId) throws InterruptedScanException
    {
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

        RequestOptions testRequestOptions = requestOptions.clone();
        testRequestOptions.followRedirects = transaction.getRedirectChildId() > 0;
        handlePause_isRunning();

        try
        {
            StandardHttpTransaction oneQuoteTransaction = runTestTransaction(transaction, datum, "'", testJobId);

            if (MimeUtils.isWebTextMimeType(oneQuoteTransaction.getResponseWrapper().getHeaders().getMimeType()))
            {
                String bodyText = oneQuoteTransaction.getResponseWrapper().getStrippedResponseText();
                bodyText = bodyText.replaceAll("\\s++", " ");
                String platform = SQLInjection.findSQLErrorMessages(bodyText);
                if (platform != null)
                {
                    recordErrorMessageFinding(oneQuoteTransaction, datum.getReferenceChain().toString(), platform);
                    return;
                }
            }

            StandardHttpTransaction twoQuoteTransaction = runTestTransaction(transaction, datum, "''", testJobId);
            int score = HttpResponseScoreUtils.scoreResponseMatch(oneQuoteTransaction, twoQuoteTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects);

            if (score < 80)
            {
                recordQuoteFinding(oneQuoteTransaction, twoQuoteTransaction, datum.getReferenceChain().toString());
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error(getName() + " request unrequestable (" + e.toString() + ")", e);
            return;
        }
    }

}
