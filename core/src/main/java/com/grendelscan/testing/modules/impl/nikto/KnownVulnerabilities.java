package com.grendelscan.testing.modules.impl.nikto;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.ProtocolVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.ConfigChangeHandler;
import com.grendelscan.testing.modules.settings.MultiSelectOptionGroup;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByBaseUriTest;
import com.grendelscan.testing.modules.types.ByHostTest;
import com.grendelscan.testing.utils.nikto.Nikto;
import com.grendelscan.testing.utils.nikto.NiktoTest;

public class KnownVulnerabilities extends AbstractTestModule implements ByHostTest, ByBaseUriTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KnownVulnerabilities.class);
    HashMap<Character, SelectableOption> categoryOptions;
    Set<Character> enabledCategories;
    private final DatabaseBackedMap<String, Integer> findings;
    private final DatabaseBackedList<String> testedBaseUris; // this is because there can be overlap between a BaseUri and a server

    public KnownVulnerabilities()
    {
        requestOptions.validateUriFormat = false;
        requestOptions.handleSessions = false;

        Nikto.initialize();
        enabledCategories = new HashSet<Character>();

        /*
         * 0 - File Upload 1 - Interesting File / Seen in logs 2 - Misconfiguration / Default File 3 - Information Disclosure 4 - Injection (XSS/Script/HTML) 5 - Remote File Retrieval - Inside Web
         * Root 6 - Denial of Service 7 - Remote File Retrieval - Server Wide 8 - Command Execution / Remote Shell 9 - SQL Injection a - Authentication Bypass b - Software Identification g - Generic
         * (Don't rely on banner) x - Reverse Tuning Options (i.e., include all except specified)
         */

        SelectableOption categoryOption;
        MultiSelectOptionGroup categoryOptionsGroup = new MultiSelectOptionGroup("Nikto Categories", "Select which categories of Nikto tests should be run.", null);
        categoryOptions = new HashMap<Character, SelectableOption>();

        ConfigChangeHandler changeHandler = new ConfigChangeHandler()
        {
            @Override
            public void handleChange()
            {
                synchronized (enabledCategories)
                {
                    enabledCategories.clear();
                    for (char optionCode : categoryOptions.keySet())
                    {
                        if (categoryOptions.get(optionCode).isSelected())
                        {
                            enabledCategories.add(optionCode);
                        }
                    }
                }
            }
        };

        categoryOption = new SelectableOption("File uploads", true, "Looks for conditions that could allow an arbitrary file to be uploaded to the server.", changeHandler);
        categoryOptions.put('0', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Misc tests", true, "Looks for \"interesting files\" and sends requests that have been seen in web servers logs as part of unidentified attacks/recon.", changeHandler);
        categoryOptions.put('1', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Misconfiguration", true, "Tests for misconfiguration of software, and searches for default files that may help with fingerprinting or attacks.", changeHandler);
        categoryOptions.put('2', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Information disclosure", true, "Searches for information disclosure, such as internal/private IP addresses.", changeHandler);
        categoryOptions.put('3', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("XSS", true, "Tests for Cross Site Scripting (XSS), HTML injection, and similar attacks.", changeHandler);
        categoryOptions.put('4', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("File retrieval - web root", true, "Tests for remote file retrieval inside the web root.", changeHandler);
        categoryOptions.put('5', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("File retrieval - server wide", true, "Tests for remote file retrieval across the entire server.", changeHandler);
        categoryOptions.put('7', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Denial of Service", true, "Tests for known Denial of Service vulnerabilities. It should not result in a disruption to service.", changeHandler);
        categoryOptions.put('6', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Command injection", true, "Tests for command injection vulnerabilities that may result in a remote shell.", changeHandler);
        categoryOptions.put('8', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("SQL injection", true, "Tests for known SQL Injection vulnerabilities.", changeHandler);
        categoryOptions.put('9', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Authentication bypass", true, "Tests for authentication bypass vulnerabilities.", changeHandler);
        categoryOptions.put('a', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Software identification", true, "Attempts to identify what software is running on the targeted server.", changeHandler);
        categoryOptions.put('b', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        categoryOption = new SelectableOption("Generic", true, "Attempts to identify what software is running on the targeted server.", changeHandler);
        categoryOptions.put('g', categoryOption);
        categoryOptionsGroup.addOption(categoryOption);

        addConfigurationOption(categoryOptionsGroup);
        changeHandler.handleChange();

        findings = new DatabaseBackedMap<String, Integer>("nikto_host_to_finding_number_map");
        testedBaseUris = new DatabaseBackedList<String>("nikto_tested_base_uris");
    }

    @Override
    public String getDescription()
    {
        return "Runs the tests from the primary Nikto database (db_tests) against " + "all base URLs defined plus all servers discovered (and allowed) in the scan. Note that some " + "Nikto tests are prone to false positives. This tool has enhanced "
                        + "file-not-found detection (when \"automatic response code " + "overrides\" is enabled). That will help reduce some false " + "positives, but, as with all findings, Nikto results should be " + "manually confirmed.\n" + "\n"
                        + "If the base URI is not the root directory of the server, and there are no " + "manually entered URL white-lists that cover the root of the server, " + "the Nikto scans will be run against the base URL.\n" + "\n"
                        + Nikto.getInstance().getNiktoCredits();
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.NIKTO;
    }

    @Override
    public String getName()
    {
        return "General Nikto Tests";
    }

    private void initializeResponseCodeProfiles(final String baseUri, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        StandardHttpTransaction base = new StandardHttpTransaction(TransactionSource.NIKTO, testJobId);
        base.getRequestWrapper().setURI(baseUri, true);
        base.execute();
        base.getLogicalResponseCode();
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private boolean isTestEnabled(final String categoryCodes)
    {
        boolean enabled = false;
        char chars[] = new char[categoryCodes.length()];
        categoryCodes.getChars(0, categoryCodes.length(), chars, 0);
        for (char c : chars)
        {
            synchronized (enabledCategories)
            {
                if (enabledCategories.contains(c))
                {
                    enabled = true;
                    break;
                }
            }
        }
        return enabled;
    }

    private synchronized void logFinding(final String host, final String message)
    {
        if (findings.containsKey(host))
        {
            Finding finding = Scan.getInstance().getFindings().get(findings.get(host));
            finding.setLongDescription(finding.getLongDescription() + "<br><br>\n" + message);
        }
        else
        {
            String briefDescription = "The Nikto database found some matches";
            String longDescription = "The Nikto database has found matches on this server.<br><br>\n" + message;
            String impact = "The security impact varies between findings.";
            String recomendations = "Verify the accuracy of the findings, and investigate to determine " + "the best course of action. Usually, that involves installing software updates or " + "correcting a configuration mistake.";
            Finding finding = new Finding(null, getName(), FindingSeverity.MEDIUM, host, "Nikto findings", briefDescription, longDescription, impact, recomendations, "");
            findings.put(host, finding.getId());
            Scan.getInstance().getFindings().addFinding(finding);
        }
    }

    private void runTest(final String baseUri, final NiktoTest test, final int testJobId) throws InterruptedScanException
    {
        for (String testUri : test.constructTestUris(baseUri, testJobId))
        {
            handlePause_isRunning();
            StandardHttpTransaction testTransaction = new StandardHttpTransaction(TransactionSource.NIKTO, testJobId);
            testTransaction.getRequestWrapper().setMethod(test.getMethod());
            testTransaction.getRequestWrapper().setURI(URIStringUtils.escapeUri(testUri), true);
            testTransaction.getRequestWrapper().setVersion(new ProtocolVersion("HTTP", Nikto.getInstance().getMajorHttpVersion(), Nikto.getInstance().getMinorHttpVersion()));

            testTransaction.setRequestOptions(requestOptions);
            try
            {
                testTransaction.execute();
                if (test.matches(testTransaction, baseUri, testJobId))
                {
                    // We only need to analyze the transaction if it succeeds
                    // scan.getCategorizerQueue().addTransaction(testTransaction);
                    String message = "OSVDB-";
                    if (test.getOsvdbNumber() > 0)
                    {
                        message += test.getOsvdbNumber();
                    }
                    else
                    {
                        message += "unknown";
                    }
                    message += ": " + testTransaction.getRequestWrapper().getMethod() + " " + testTransaction.getRequestWrapper().getURI() + " : " + test.getMessage();
                    testTransaction.writeToDisk();
                    message += "<br>The transaction used for testing can be viewed " + HtmlUtils.makeLink(testTransaction.getSavedUrl(), "here") + ".<br><br>";
                    logFinding(testTransaction.getRequestWrapper().getHost(), message);
                }
            }
            catch (UnrequestableTransaction e)
            {
                LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByBaseUriTest#testByBaseUri(java.lang.String)
     */
    @Override
    public void testByBaseUri(final String baseUri, final int testJobId) throws InterruptedScanException
    {
        try
        {
            testUri(URIStringUtils.getDirectoryUri(baseUri), testJobId);
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Weird problem with URI parsing: " + e.toString(), e);
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error("Problem requesting base URI for nikto tests: " + e.toString(), e);
        }
    }

    @Override
    public void testByServer(final int transactionID, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        String baseUri;
        try
        {
            baseUri = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString());
            if (Scan.getScanSettings().getUrlFilters().isUriAllowed(baseUri))
            {
                testUri(URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString()), testJobId);
            }
            else
            {
                LOGGER.info("Nikto is skipping the tests for " + baseUri);
            }
        }
        catch (URISyntaxException e1)
        {
            LOGGER.error("Very strange URL problem: " + e1.toString(), e1);
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.error("Problem requesting base URI for nikto tests: " + e.toString(), e);
        }
    }

    private void testUri(final String baseUri, final int testJobId) throws InterruptedScanException, UnrequestableTransaction
    {
        synchronized (testedBaseUris)
        {
            if (testedBaseUris.contains(baseUri))
            {
                LOGGER.info("Nikto already tested " + baseUri);
                return;
            }
            testedBaseUris.add(baseUri);
        }
        initializeResponseCodeProfiles(baseUri, testJobId);
        Nikto.getInstance().initializeIndexPHP(baseUri, testJobId);
        Set<NiktoTest> tests = Nikto.getInstance().getNiktoTests();
        for (NiktoTest test : tests)
        {
            handlePause_isRunning();
            if (isTestEnabled(test.getCategories()))
            {
                runTest(baseUri, test, testJobId);
            }
        }
    }
}
