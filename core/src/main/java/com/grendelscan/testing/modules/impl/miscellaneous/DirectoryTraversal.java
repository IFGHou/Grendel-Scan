package com.grendelscan.testing.modules.impl.miscellaneous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;
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

public class DirectoryTraversal extends AbstractTestModule implements ByRequestDataLocationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryTraversal.class);
    private final String antiPattern;
    private final int antiPatternThreshold;
    private final String patterns[];
    private final int patternThreshold;

    public DirectoryTraversal()
    {
        antiPattern = ConfigurationManager.getString("directory_traversal.antipattern");
        patterns = ConfigurationManager.getStringArray("directory_traversal.patterns");
        antiPatternThreshold = ConfigurationManager.getInt("directory_traversal.antipattern_threshold");
        patternThreshold = ConfigurationManager.getInt("directory_traversal.pattern_threshold");
    }

    @Override
    public String getDescription()
    {
        return "Tests for directory traversal vulnerabilities in query parameters. This is done " + "by inserting relative path strings. For example, if the default parameter value "
                        + "is 'asdf', then '.asdf' is request. If this causes a different result page, " + "then './asdf', '.\\asdf', etc are requested. If they result in the same response "
                        + "as the original request, then a directory traversal attack may be possible.";
    }

    @Override
    public String getExperimentalText()
    {

        return "While this module can discover directory traversal vulnerabilities, more testing " + "is required to reduce false positives and false negatives.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
    }

    @Override
    public String getName()
    {
        return "Directory traversal";
    }

    @Override
    public boolean isExperimental()
    {
        return true;
    }

    private void report(final StandardHttpTransaction originalTransaction, final StandardHttpTransaction testTransaction, final StandardHttpTransaction antitestTransaction, final String parameterName, final String originalValue, final String antiValue,
                    final String testValue)
    {
        originalTransaction.writeToDisk();
        testTransaction.writeToDisk();
        String title = "Directory traversal vulnerability";
        String shortDescription = "A possible directory traversal vulnerability was detected.";
        String longDescription = "A possible directory traversal vulnerability was detected in the \"" + parameterName + "\" parameter of " + URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI()) + ". The original value "
                        + "of the parameter was \"" + originalValue + "\". When the value of \"" + antiValue + "\" was used instead (" + antitestTransaction.getSavedUrl() + "), it appears that a different page was returned, perhaps an "
                        + "error message. When a value of \"" + testValue + "\" was used, it appears that " + "the response matched the original request. This implies that the parameter is "
                        + "vulnerable to directory traversal attacks. However, this test may be prone to " + "false positives, so further investigation is recommended. The original transaction " + "can be viewed <a href=\"" + originalTransaction.getSavedUrl()
                        + "\">here</a> and " + "the transaction used for testing can be viewed " + "<a href=\"" + testTransaction.getSavedUrl() + "\">here</a>.";
        String impact = "Depending on the function of the page, directory traversal attacks might be " + "used to read or execute arbitrary files already on the server.";
        String recomendations = "Confirm that this is a directory traversal vulnerability. If it is, at the very " + "least, modify the application to sanitize input, blocking any non-alphanumeric "
                        + "characters. Ideally, a user should never be able to directly reference a file " + "by name in a query parameter.";
        String references = "";
        Finding event = new Finding(null, getName(), FindingSeverity.HIGH, URIStringUtils.getFileUri(originalTransaction.getRequestWrapper().getURI()), title, shortDescription, longDescription, impact, recomendations, references);
        Scan.getInstance().getFindings().addFinding(event);
    }

    @Override
    public void testByRequestData(final int transactionId, final DataReferenceChain chain, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);

        // DataReferenceChain chain
        ByteData datum;
        try
        {
            datum = (ByteData) DataContainerUtils.resolveReferenceChain(originalTransaction.getTransactionContainer(), chain);
        }
        catch (ClassCastException exception)
        {
            throw new IllegalStateException("Problem following reference chain (" + chain.toString() + ")", exception);
        }

        String oldValue = new String(DataUtils.getBytes(datum));
        /**
         * We don't check response types later because we assume that if the original response is good, the rest probably will be too. Nothing bad will happen if they aren't, it's just wasted time.
         */
        if (oldValue.equals(""))
        {
            return;
        }

        StandardHttpTransaction antitestTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        String antiValue = antiPattern.replace("%%value%%", oldValue);
        ByteData antiTestData = (ByteData) DataContainerUtils.resolveReferenceChain(antitestTransaction.getTransactionContainer(), datum.getReferenceChain());
        antiTestData.setBytes(antiValue.getBytes());
        RequestOptions testRequestOptions = requestOptions.clone();
        testRequestOptions.followRedirects = originalTransaction.hasRedirectResponse();
        antitestTransaction.setRequestOptions(testRequestOptions);
        try
        {
            antitestTransaction.execute();
            if (HttpResponseScoreUtils.scoreResponseMatch(originalTransaction, antitestTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < antiPatternThreshold)
            {
                for (String pattern : patterns)
                {
                    StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
                    ByteData testData = (ByteData) DataContainerUtils.resolveReferenceChain(testTransaction.getTransactionContainer(), datum.getReferenceChain());
                    String testValue = pattern.replace("%%value%%", oldValue);
                    testData.setBytes(testValue.getBytes());
                    testTransaction.setRequestOptions(testRequestOptions);
                    try
                    {
                        testTransaction.execute();
                    }
                    catch (UnrequestableTransaction e)
                    {
                        LOGGER.warn("Dir traversal request unrequestable (" + testTransaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
                        continue;
                    }
                    if (HttpResponseScoreUtils.scoreResponseMatch(originalTransaction, testTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < patternThreshold
                                    && HttpResponseScoreUtils.scoreResponseMatch(antitestTransaction, testTransaction, 100, Scan.getScanSettings().isParseHtmlDom(), testRequestOptions.followRedirects) < antiPatternThreshold)
                    {
                        report(originalTransaction, testTransaction, antitestTransaction, datum.getReferenceChain().toString(), oldValue, antiValue, testValue);
                        break;
                    }

                }
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
        }
    }

}
