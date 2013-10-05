package com.grendelscan.testing.modules.impl.fileEnumeration;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.data.findings.Finding;
import com.grendelscan.scan.data.findings.FindingSeverity;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.ConfigChangeHandler;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.SingleSelectOptionGroup;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByDirectoryTest;

public class DirectoryEnumerator extends AbstractTestModule implements ByDirectoryTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryEnumerator.class);
    private List<String> directoryNames;
    // private List<String> discoveredNames;
    private final Object listLock = new Object();
    private final SelectableOption smallNameOption, mediumNameOption, largeNameOption, extraLargeNameOption;

    private Finding finding;
    private final static String FINDING_NAME = "directory_enumerator_finding_number";

    public DirectoryEnumerator()
    {
        ConfigChangeHandler changeHandler = new ConfigChangeHandler()
        {
            @Override
            public void handleChange()
            {
                initializeList();
            }
        };
        SingleSelectOptionGroup nameOptionsGroup = new SingleSelectOptionGroup("Name list size", "Select the name list size to use.", changeHandler);
        addConfigurationOption(nameOptionsGroup);

        smallNameOption = new SelectableOption("Small (100 directory names)", false, "", changeHandler);
        nameOptionsGroup.addOption(smallNameOption);
        mediumNameOption = new SelectableOption("Medium (300 directory names)", true, "", changeHandler);
        nameOptionsGroup.addOption(mediumNameOption);
        largeNameOption = new SelectableOption("Large (500 directory names)", false, "", changeHandler);
        nameOptionsGroup.addOption(largeNameOption);
        extraLargeNameOption = new SelectableOption("Extra large (819 directory names)", false, "", changeHandler);
        nameOptionsGroup.addOption(extraLargeNameOption);
        initializeList();
    }

    @Override
    public String getDescription()
    {
        return "Looks for common directory names in all directories and sub-directories " + "observed during the scan. This can take a while, so only enable " + "this module if you're willing to wait";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.FILE_ENUMERATION;
    }

    @Override
    public String getName()
    {
        return "Directory enumerator";
    }

    // Intentionally default visibility
    void initializeList()
    {
        synchronized (listLock)
        {
            directoryNames = ConfigurationManager.getList("file_enumeration.small_directory_list");
            if (mediumNameOption.isSelected() || largeNameOption.isSelected() || extraLargeNameOption.isSelected())
            {
                directoryNames.addAll(ConfigurationManager.getList("file_enumeration.medium_directory_list"));
            }
            if (largeNameOption.isSelected() || extraLargeNameOption.isSelected())
            {
                directoryNames.addAll(ConfigurationManager.getList("file_enumeration.large_directory_list"));
            }
            if (extraLargeNameOption.isSelected())
            {
                directoryNames.addAll(ConfigurationManager.getList("file_enumeration.extra_large_directory_list"));
            }
        }
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private synchronized void logFinding(final String uri)
    {
        try
        {
            if (finding == null)
            {
                finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME));
            }
        }
        catch (DataNotFoundException e)
        {
            finding = new Finding(null, getName(), FindingSeverity.INFO, uri, "Directories discovered", "Directories were discovered by guessing the name", "The following directories were discovered by guessing the name: <br>",
                            "If these directories were not linked to by the website, they could lead an attacker to unintended areas.", "Review the contents of the directories and remove them if they aren't required.", "");
            Scan.getInstance().getFindings().addFinding(finding);
            Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
        }

        finding.setLongDescription(finding.getLongDescription() + uri + "<br/>\n");
    }

    @Override
    public void testByDirectory(final int transactionID, final String directory, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        synchronized (listLock)
        {
            for (String newDirectory : directoryNames)
            {
                handlePause_isRunning();
                String uri;
                try
                {
                    uri = URIStringUtils.getHostUriWithoutTrailingSlash(originalTransaction.getRequestWrapper().getAbsoluteUriString()) + directory + newDirectory + "/";
                    if (Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
                    {
                        StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.ENUMERATION, testJobId);
                        testTransaction.getRequestWrapper().setURI(uri, true);
                        testTransaction.setRequestOptions(requestOptions);
                        if (HttpUtils.fileExists(testTransaction.getLogicalResponseCode()))
                        {
                            Scan.getInstance().getCategorizerQueue().addTransaction(testTransaction);
                            logFinding(testTransaction.getRequestWrapper().getAbsoluteUriString());
                            LOGGER.info("The " + getName() + " test module found a directory: " + testTransaction.getRequestWrapper().getAbsoluteUriString());
                        }
                    }
                    else
                    {
                        LOGGER.debug(uri + " cannot be requested by directory enumerator");
                    }
                }
                catch (URISyntaxException e)
                {
                    LOGGER.error("Weird problem with uri parsing: " + e.toString(), e);
                }
            }
        }
    }
}
