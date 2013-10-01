package com.grendelscan.smashers.miscellaneous;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.RegexUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.responseCompare.HttpResponseScoreUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.settings.ConfigChangeHandler;
import com.grendelscan.smashers.settings.TextOption;
import com.grendelscan.smashers.types.ByFileTest;

public class PlaintextHTTP extends AbstractSmasher implements ByFileTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaintextHTTP.class);
    private final DatabaseBackedMap<String, Integer> findings;
    private final TextOption whitelistConfigOption;
    private final Object patternLock = new Object();
    private Pattern[] whitelistPatterns;
    private final DatabaseBackedMap<String, Integer> failedRequestsPerServer;

    public PlaintextHTTP()
    {
        ConfigChangeHandler changeHandler = new ConfigChangeHandler()
        {

            @Override
            public void handleChange()
            {
                initPatterns();
            }
        };

        requestOptions.ignoreRestrictions = true; // because HTTP is probably not on the list
        whitelistConfigOption = new TextOption("URL regex whitelist", ".*", "Each line is a regular expression that will be compared against the base URL.", true, changeHandler);
        addConfigurationOption(whitelistConfigOption);
        initPatterns();

        findings = new DatabaseBackedMap<String, Integer>("plaintext_http_host_to_finding_number_map");
        failedRequestsPerServer = new DatabaseBackedMap<String, Integer>("plaintext_http_failed_requests_per_server");
    }

    @Override
    public String getDescription()
    {
        return "Tests to see if pages originally requested over HTTPS are accessible over plaintext HTTP. The " + "regular expression whitelist will determine which pages are tested. The regex is compared " + "to the base URL, but ignores any query. Each "
                        + "line contains a new regex. Any invalid regex formats will be " + "skipped, so be careful. Note that the default is to test all pages.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
    }

    @Override
    public String getName()
    {
        return "Insecure transmission";
    }

    void initPatterns()
    {
        synchronized (patternLock)
        {
            whitelistPatterns = RegexUtils.stringToPatterns(whitelistConfigOption.getValue());
        }
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private synchronized void report(final StandardHttpTransaction transaction)
    {
        transaction.writeToDisk();
        String host = transaction.getRequestWrapper().getHost();
        String url = URIStringUtils.getFileUri(transaction.getRequestWrapper().getAbsoluteUriString());
        if (findings.containsKey(host))
        {
            Finding finding = Scan.getInstance().getFindings().get(findings.get(host));
            String longDescription = finding.getLongDescription();
            longDescription += "<a href=\"" + transaction.getSavedUrl() + "\">" + url + "</a><br>\n";
            finding.setLongDescription(longDescription);
        }
        else
        {
            String title = "Insecure transmission detected";
            String shortDescription = "At least one page normally transmitted over HTTPS is accessible via plaintext HTTP.";
            String longDescription = "At least one page normally transmitted over HTTPS is accessible via plaintext HTTP.<br>" + "<br>" + "The vulnerable URL(s) are listed below. The links are to the insecure transaction.<br>";
            longDescription += "<a href=\"" + transaction.getSavedUrl() + "\">" + url + "</a><br>\n";
            String impact = "If sensitive data (authentication credentials, session IDs, etc) is involved, " + "it can be viewed or modified by an attacker that is " + "able to view network traffic, or able to launch a man-in-the-middle (MITM) attack.";
            String recomendations = "Determine if the transaction in question contains sensitive information. If " + "it does, configure the web or application server to only allow the transaction " + "over HTTPS.";
            String references = "";
            Finding finding = new Finding(null, getName(), FindingSeverity.LOW, url, title, shortDescription, longDescription, impact, recomendations, references);
            Scan.getInstance().getFindings().addFinding(finding);
            findings.put(host, finding.getId());
        }
    }

    private boolean shouldBeTested(final String url)
    {
        boolean test = false;

        synchronized (patternLock)
        {
            for (Pattern white : whitelistPatterns)
            {
                if (white.matcher(url).find())
                {
                    test = true;
                    break;
                }
            }
        }
        return test;
    }

    @Override
    public void testByFile(final int transactionID, final int testJobId) throws InterruptedScanException
    {

        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);

        String host = originalTransaction.getRequestWrapper().getHost();
        int oldFailed = failedRequestsPerServer.containsKey(host) ? failedRequestsPerServer.get(host) : 0;

        if (oldFailed >= 5)
        {
            LOGGER.debug("Skipping plaintext http test for " + host + " because failed request threshold met");
            return;
        }

        // We're not interested in HTTP, redirects, or non-html responses
        if (!originalTransaction.getRequestWrapper().isSecure() || !shouldBeTested(URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI())) || originalTransaction.getLogicalResponseCode() != 200
                        || !MimeUtils.isHtmlMimeType(originalTransaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            return;
        }

        StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        testTransaction.setRequestOptions(requestOptions);
        testTransaction.getRequestWrapper().setURI("http" + testTransaction.getRequestWrapper().getAbsoluteUriString().substring(5), true);
        try
        {
            testTransaction.execute();
            if (testTransaction.getLogicalResponseCode() == 200 && HttpResponseScoreUtils.scoreResponseMatch(originalTransaction, testTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), originalTransaction.getRedirectChildId() > 0) < 95)
            {
                report(testTransaction);
                // vulnerableUrls.put(URIStringUtils.getBaseUri(originalTransaction.getRequestWrapper().getURI()),
                // testTransaction.getId());
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error(getName() + " request unrequestable (" + testTransaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
        }

        if (!testTransaction.isSuccessfullExecution())
        {
            failedRequestsPerServer.put(host, oldFailed + 1);
        }
    }

}
