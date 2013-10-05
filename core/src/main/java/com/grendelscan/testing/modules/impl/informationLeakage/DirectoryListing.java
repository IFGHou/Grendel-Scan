package com.grendelscan.testing.modules.impl.informationLeakage;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByDirectoryTest;

public class DirectoryListing extends AbstractTestModule implements ByDirectoryTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryListing.class);
    private static Pattern titlePattern = Pattern.compile("<title(.+?)</title>", Pattern.CASE_INSENSITIVE);
    private final String[] basicPatterns;
    private final String[] titlePatterns;
    private Finding finding;
    private static final String FINDING_NAME = "directory_listing_finding_number";

    public DirectoryListing()
    {
        basicPatterns = ConfigurationManager.getStringArray("directory_indexing_patterns.basic");
        titlePatterns = ConfigurationManager.getStringArray("directory_indexing_patterns.title");
        requestOptions.followRedirects = true;
        requestOptions.testRedirectTransactions = true;
    }

    @Override
    public String getDescription()
    {
        return "Checks for directory listing in all directories discovered by Grendel-Scan.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.INFORMATION_LEAKAGE;
    }

    @Override
    public String getName()
    {
        return "Directory listings";
    }

    private boolean isDirectoryListing(final StandardHttpTransaction testTransaction)
    {
        boolean directoryListing = false;
        if (MimeUtils.isWebTextMimeType(testTransaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            String escapedDirectory = null;
            try
            {
                escapedDirectory = Pattern.quote(URIStringUtils.getDirectory(testTransaction.getRequestWrapper().getURI()));
            }
            catch (URISyntaxException e)
            {
                IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
                LOGGER.error(e.toString(), e);
                throw ise;
            }
            String content = new String(testTransaction.getResponseWrapper().getBody());
            for (String rawPattern : basicPatterns)
            {
                Pattern pattern = Pattern.compile(rawPattern.replace("%%dir%%", escapedDirectory), Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(content).find())
                {
                    directoryListing = true;
                    break;
                }
            }

            if (!directoryListing)
            {
                Matcher m = titlePattern.matcher(content);
                if (m.find())
                {
                    String titleText = m.group(1);

                    for (String rawPattern : titlePatterns)
                    {
                        Pattern pattern = Pattern.compile(rawPattern.replace("%%dir%%", escapedDirectory), Pattern.CASE_INSENSITIVE);
                        if (pattern.matcher(titleText).find())
                        {
                            directoryListing = true;
                            break;
                        }
                    }
                }
            }
        }
        return directoryListing;
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private synchronized void logFinding(final String url)
    {
        try
        {
            if (finding == null)
            {
                finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME));
            }
            else
            {
                finding.setLongDescription(finding.getLongDescription() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + HtmlUtils.makeLink(url) + "<br>\n");
            }
        }
        catch (DataNotFoundException e)
        {
            String title = "Directory content listing detected";
            String shortDescription = "At least one directory was found supporting content listing.";
            String longDescription = "At least one directory was found supporting content listing." + "The vulnerable directories(s) are listed below:<br>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + HtmlUtils.makeLink(url) + "<br>\n";

            String impact = "Directory listings can be used to explore web site content that would " + "otherwise be unknown to an attacker.";
            String recomendations = "Disable directory content listing on all web servers.";
            String references = "";

            finding = new Finding(null, getName(), FindingSeverity.INFO, url, title, shortDescription, longDescription, impact, recomendations, references);
            Scan.getInstance().getFindings().addFinding(finding);
            Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
        }

    }

    @Override
    public void testByDirectory(final int transactionID, final String directory, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        try
        {
            StandardHttpTransaction testTransaction;
            if (directory.equals(originalTransaction.getRequestWrapper().getURI()))
            {
                testTransaction = originalTransaction;
            }
            else
            {
                testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
                testTransaction.getRequestWrapper().setMethod("GET");
                testTransaction.getRequestWrapper().setURI(directory, true);
                testTransaction.setRequestOptions(requestOptions);
                testTransaction.execute();
            }
            if (isDirectoryListing(testTransaction))
            {
                logFinding(testTransaction.getRequestWrapper().getAbsoluteUriString());
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.debug(getName() + " request unrequestable (" + e.toString() + ")", e);
        }
    }

}
