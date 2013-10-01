package com.grendelscan.smashers.webServerConfiguration;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.ByHostTest;
import com.grendelscan.smashers.utils.tokens.TokenTesting;

public class XST extends AbstractSmasher implements ByHostTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XST.class);

    @Override
    public String getDescription()
    {
        return "Tests for cross-site tracing (XST), which is an attack that can steal cookies using the " + "TRACE or TRACK debug method.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.WEB_SERVER_CONFIGURATION;
    }

    @Override
    public String getName()
    {
        return "XST";
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private void logFinding(final StandardHttpTransaction transaction)
    {
        String title = "HTTP debug method enabled";
        String briefDescription = "The TRACE/TRACK method was enabled.";
        String longDescription = "The TRACE/TRACK method was enabled on the servers listed below:<br><br>";

        String firstHost = "";
        transaction.writeToDisk();
        try
        {
            firstHost = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString());
            longDescription += "&nbsp;&nbsp;&nbsp;&nbsp;" + HtmlUtils.makeLink(transaction.getSavedUrl(), URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString())) + "<br>";
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }

        String impact = "An attack known as Cross Site Tracing (XST) leverages this method to steal cookies. " + "Once a session key in a cookie is obtained by an attacker, he can hijack a legitimate user’s session.";
        String recomendations = "Disable HTTP TRACE/TRACK on all web servers: <ul>" + "<li>IIS: http://www.microsoft.com/technet/prodtechnol/WindowsServer2003/Library/IIS/d779ee4e-5cd1-4159-b098-66c10c5a3314.mspx?mfr=true</li>"
                        + "<li>Apache: http://httpd.apache.org/docs/2.0/mod/core.html#traceenable</li></ul>";
        String references = "http://www.owasp.org/index.php/Testing_for_HTTP_Methods_and_XST";

        Finding event = new Finding(null, getName(), FindingSeverity.LOW, firstHost, title, briefDescription, longDescription, impact, recomendations, references);
        Scan.getInstance().getFindings().addFinding(event);
    }

    @Override
    public void testByServer(final int transactionID, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        try
        {
            if (testMethod(transaction, "TRACE", testJobId) || testMethod(transaction, "TRACK", testJobId))
            {
                logFinding(transaction);
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error(getName() + " request unrequestable (" + e.toString() + ")", e);
        }
    }

    private boolean testMethod(final StandardHttpTransaction transaction, final String method, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        StandardHttpTransaction test = transaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        test.getRequestWrapper().setMethod(method);
        String tokenName = TokenTesting.getInstance().generateToken();
        String tokenValue = TokenTesting.getInstance().generateToken();

        test.getRequestWrapper().getHeaders().addHeader(tokenName, tokenValue);
        test.setRequestOptions(requestOptions);
        test.execute();
        if (new String(test.getResponseWrapper().getBody()).contains(tokenName + ": " + tokenValue))
        {
            test.writeToDisk();
            return true;
        }
        return false;
    }

}
