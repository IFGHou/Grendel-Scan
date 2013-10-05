package com.grendelscan.testing.modules.impl.sessionManagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.responseCompare.HttpResponseScoreUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByHttpQueryTest;

public class AuthenticationBypass extends AbstractTestModule implements ByHttpQueryTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationBypass.class);
    private final SelectableOption getTransactionOption;
    private final SelectableOption postTransactionOption;

    public AuthenticationBypass()
    {
        postTransactionOption = new SelectableOption("POST", false, "Check to test POST transactions. This can be dangerous.", null);
        addConfigurationOption(postTransactionOption);
        getTransactionOption = new SelectableOption("GET", true, "Check to test POST transactions.", null);
        addConfigurationOption(getTransactionOption);
    }

    @Override
    public String getDescription()
    {
        return "This module will report pages / queries that appear " + "to be vulnerable to authentication bypass. Note that " + "testing POSTs can be dangerous because the " + "transaction has to be repeated to test the result.";
    }

    @Override
    public String getExperimentalText()
    {
        return "This module requires more testing to assure its accuracy.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SESSION_MANAGEMENT;
    }

    @Override
    public String getName()
    {
        return "Authentication enforcement";
    }

    @Override
    public boolean isExperimental()
    {
        return true;
    }

    private void logFinding(final StandardHttpTransaction originalTransaction, final StandardHttpTransaction testTransaction)
    {
        String title = "Authentication bypass detected";
        String shortDesc = "An authentication bypass vulnerability may have been detected";
        String longDesc = "An authentication bypass vulnerability may have been detected. Some authenticated " + "requests were successfully executed without providing a session ID. The table below contains " + "the relevant transactions:\n" + "<table>"
                        + "<tr style=\"font-weight: bold\"><td>Original Transaction</td><td>Unauthenticated transaction</td><tr>\n";

        String firstUrl = URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI());
        originalTransaction.writeToDisk();
        testTransaction.writeToDisk();
        longDesc += "<tr><td>" + HtmlUtils.makeLink(originalTransaction.getSavedUrl(), String.valueOf(originalTransaction.getId())) + "<td>";
        longDesc += "<td>" + HtmlUtils.makeLink(testTransaction.getSavedUrl(), String.valueOf(testTransaction.getId())) + "<td></tr>\n";
        longDesc += "</table>\n";

        String recomendations = "These results could be false positives if the content is intended to be accessible " + "by unauthenticated users. A manual review should be performed to determine the accuracy.";
        String impact = "If the data presented to authenticated users is sensitive, it could be disclosed to an attacker.";
        String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Top_10_2007-A7");
        Finding event = new Finding(null, getName(), FindingSeverity.HIGH, firstUrl, title, shortDesc, longDesc, impact, recomendations, references);
        Scan.getInstance().getFindings().addFinding(event);
    }

    private void testAuthenticationEnforcement(final StandardHttpTransaction transaction, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction testTransaction = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        // testTransaction.removeAllAuthentication();
        testTransaction.setRequestOptions(requestOptions);
        try
        {
            testTransaction.execute();
            if (HttpResponseScoreUtils.scoreResponseMatch(transaction, testTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), requestOptions.followRedirects) < 95)
            {
                logFinding(transaction, testTransaction);
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable: " + e.toString());
        }
    }

    @Override
    public void testByQuery(final int transactionID, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);

        String method = transaction.getRequestWrapper().getMethod();
        if ((postTransactionOption.isSelected() && method.equals("POST") || getTransactionOption.isSelected() && method.equals("GET")) && transaction.isAuthenticated())
        {
            testAuthenticationEnforcement(transaction, testJobId);
        }
    }

}
