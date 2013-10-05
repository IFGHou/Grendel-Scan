package com.grendelscan.testing.modules.impl.miscellaneous;

import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableHttpHeader;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.impl.hidden.TokenSubmitter;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByRepeatableOutputContextTest;
import com.grendelscan.testing.utils.tokens.HttpHeaderContext;
import com.grendelscan.testing.utils.tokens.TokenContext;
import com.grendelscan.testing.utils.tokens.TokenContextType;
import com.grendelscan.testing.utils.tokens.TokenContextTypeUtils;
import com.grendelscan.testing.utils.tokens.TokenTesting;

public class CRLFInjection extends AbstractTestModule implements ByRepeatableOutputContextTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CRLFInjection.class);

    /**
     * 
     * @param transactions
     * @return 0 for no match, 1 for a found header name, 2 for a found header name and value
     */
    private int checkSuccess(final StandardHttpTransaction transactions[], final String headerName, final String headerValue)
    {
        List<SerializableHttpHeader> headers = transactions[0].getResponseWrapper().getHeaders().getReadOnlyHeaders();
        int match = 0;
        for (Header header : headers)
        {
            if (header.getName().equalsIgnoreCase(headerName))
            {
                if (header.getValue().toUpperCase().startsWith(headerValue.toUpperCase()))
                {
                    match = 2;
                    break;
                }
                match = 1;
            }
        }

        return match;
    }

    private void crlfReport(final StandardHttpTransaction transaction, final HttpHeaderContext context, final String attackString, final String headerName, final String headerValue, final StandardHttpTransaction attackTransaction)
    {
        attackTransaction.writeToDisk();
        String longDescription = "The query parameter named " + context.getRequestDatum().getReferenceChain().toString() + " on " + transaction.getRequestWrapper().getURI() + " appears to be vulnerable to carriage "
                        + " return / line feed (CRLF) injection attacks. An attacker can use this " + "to craft arbitrary response headers, and, in the right circumstance, " + "craft arbitrary response bodies.<br>" + "<br>"
                        + "The attack was performed using an HTTP " + transaction.getRequestWrapper().getMethod().toUpperCase() + " command. When the attack string \"" + HtmlUtils.escapeHTML(attackString)
                        + "\" was supplied as the parameter value, it was placed in the \"" + context.getContextHeader().getName() + "\" header. The CRLF allowed a new " + "header named \"" + headerName + "\" to be inserted with the value " + "\""
                        + headerValue + "\".<br><br>The transaction used for testing can be viewed " + HtmlUtils.makeLink(attackTransaction.getSavedUrl(), "here") + ".";
        if (transaction.getRequestWrapper().getMethod().equalsIgnoreCase("GET"))
        {
            String url = transaction.getRequestWrapper().getURI();
            longDescription += "<br><br>This attack can be duplicated by visiting the following URL: " + HtmlUtils.makeLink(url);
        }

        String impact = "";

        String recomendation = "Insert the user input into the header without unescaping the " + "values (e.g. %0d -> \\x0d)";
        if (context.getContextHeader().getName().equalsIgnoreCase("location"))
        {
            recomendation += "";
        }

        Finding event = new Finding(null, getName(), FindingSeverity.MEDIUM, transaction.getRequestWrapper().getURI(), "CRLF Injection", "CRLF injection found.", longDescription, impact, recomendation, "");
        Scan.getInstance().getFindings().addFinding(event);
    }

    @Override
    public String getDescription()
    {
        return "Tests for CRLF injection into HTTP headers from HTTP query " + "parameters. If the destination is a \"Location\" header " + "(which is most common), redirection to an arbitrary site " + "will also be tested.";
    }

    @Override
    public TokenContextType[] getDesiredRepeatableContexts()
    {
        return TokenContextTypeUtils.getHttpHeaderContexts();
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
    }

    @Override
    public String getName()
    {
        return "CRLF Injection";
    }

    @Override
    public Class<? extends AbstractTestModule>[] getPrerequisites()
    {
        return new Class[] { TokenSubmitter.class };
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private void locationReport(final StandardHttpTransaction transaction, final HttpHeaderContext context, final String attackString)
    {
        String longDescription = "The query parameter named " + context.getRequestDatum().getReferenceChain().toString() + " on " + transaction.getRequestWrapper().getURI() + " allows for arbitrary location headers to be " + "defined in the result.\n" + "\n"
                        + "The attack was performed using an HTTP " + transaction.getRequestWrapper().getMethod().toUpperCase() + " command. When the attack string \"" + attackString
                        + "\" was supplied as the parameter value, it was placed in the \"location\" " + "header.\n";
        if (transaction.getRequestWrapper().getMethod().equalsIgnoreCase("GET"))
        {
            String url = transaction.getRequestWrapper().getURI();
            longDescription += "This attack can be duplicated by visiting the following URL: " + HtmlUtils.makeLink(url);
        }

        String impact = "location blah blah blah.";

        String recomendation = "filter blah blah blah";

        Finding event = new Finding(null, getName(), FindingSeverity.LOW, transaction.getRequestWrapper().getURI(), "Location Header Override", "location header...", longDescription, impact, recomendation, "");
        Scan.getInstance().getFindings().addFinding(event);
    }

    @Override
    public void testByRepeatableOutputContext(final Collection<TokenContext> contexts, final int testJobId) throws InterruptedScanException
    {
        handlePause_isRunning();
        try
        {
            HttpHeaderContext headerContext = (HttpHeaderContext) contexts.toArray(new TokenContext[0])[0];
            if (testCRLFInjection(headerContext, testJobId))
            {
                return;
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
        }

        for (TokenContext context : contexts)
        {
            HttpHeaderContext headerContext = (HttpHeaderContext) context;
            if (headerContext.getContextHeader().getName().equalsIgnoreCase("location"))
            {
                testLocation(headerContext, testJobId);
                return;
            }
        }
    }

    private boolean testCRLFInjection(final HttpHeaderContext headerContext, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        handlePause_isRunning();
        String headerName = TokenTesting.getInstance().generateToken();
        String headerValue = TokenTesting.getInstance().generateToken();

        String attackString = "blah\r\n" + headerName + ":" + headerValue;
        StandardHttpTransaction transactions[] = TokenTesting.getInstance().duplicateTokenTest(headerContext, attackString, getName(), TransactionSource.MISC_TEST, testJobId);
        handlePause_isRunning();
        int match = checkSuccess(transactions, headerName, headerValue);
        if (match > 0)
        {
            crlfReport(transactions[0], headerContext, attackString, headerName, headerValue, transactions[0]);
        }

        return match == 2 ? true : false;
    }

    private void testLocation(final HttpHeaderContext headerContext, final int testJobId) throws InterruptedScanException
    {
        String location = "http://www." + TokenTesting.getInstance().generateToken() + ".com";
        StandardHttpTransaction transactions[];
        try
        {
            transactions = TokenTesting.getInstance().duplicateTokenTest(headerContext, location, getName(), TransactionSource.MISC_TEST, testJobId);
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
            return;
        }

        if (checkSuccess(transactions, "Location", location) == 2)
        {
            locationReport(transactions[0], headerContext, location);
        }
    }

}
