package com.grendelscan.smashers.fileEnumeration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.settings.ConfigChangeHandler;
import com.grendelscan.smashers.settings.SelectableOption;
import com.grendelscan.smashers.settings.SingleSelectOptionGroup;
import com.grendelscan.smashers.types.ByDirectoryTest;

public class FileEnumerator extends AbstractSmasher implements ByDirectoryTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileEnumerator.class);
    // private List<String> discoveredNames;
    private final static Object listLock = new Object();
    private List<String> fileNames;
    private List<String> fileSuffixes;
    private final SelectableOption smallNameOption, mediumNameOption, largeNameOption, smallExtensionOption, mediumExtensionOption, largeExtensionOption;
    private Finding finding;
    private final static String FINDING_NAME = "file_enumerator_finding_number";

    public FileEnumerator()
    {
        ConfigChangeHandler changeHandler = new ConfigChangeHandler()
        {
            @Override
            public void handleChange()
            {
                initializeLists();
            }
        };
        SingleSelectOptionGroup nameOptionsGroup = new SingleSelectOptionGroup("Name list size", "Select the name list size to use.", changeHandler);
        addConfigurationOption(nameOptionsGroup);

        smallNameOption = new SelectableOption("Small (20 filenames)", false, "", changeHandler);
        nameOptionsGroup.addOption(smallNameOption);
        mediumNameOption = new SelectableOption("Medium (40 filenames)", true, "", changeHandler);
        nameOptionsGroup.addOption(mediumNameOption);
        largeNameOption = new SelectableOption("Large (64 filenames)", false, "", changeHandler);
        nameOptionsGroup.addOption(largeNameOption);

        SingleSelectOptionGroup extensionOptionsGroup = new SingleSelectOptionGroup("Extension list size", "Select the extesion list size to use.", changeHandler);
        addConfigurationOption(extensionOptionsGroup);

        smallExtensionOption = new SelectableOption("Small (10 extensions)", true, "", changeHandler);
        extensionOptionsGroup.addOption(smallExtensionOption);
        mediumExtensionOption = new SelectableOption("Medium (25 extensions)", false, "", changeHandler);
        extensionOptionsGroup.addOption(mediumExtensionOption);
        largeExtensionOption = new SelectableOption("Large (63 extensions)", false, "", changeHandler);
        extensionOptionsGroup.addOption(largeExtensionOption);
        initializeLists();
        requestOptions.followRedirects = true;
    }

    @Override
    public String getDescription()
    {
        return "This module will look for common file names in all directories " + "and sub-directories observed during the scan. If you pick long " + "name and extension lists, this can take a very long time, so "
                        + "only enable this module if you're willing to wait. \n" + "\n" + "The word lists are defined in conf/file_enumeration.conf.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.FILE_ENUMERATION;
    }

    @Override
    public String getName()
    {
        return "File enumerator";
    }

    // Intentionally default visibility
    void initializeLists()
    {
        synchronized (listLock)
        {
            fileSuffixes = ConfigurationManager.getList("file_enumeration.small_extension_list");
            if (mediumExtensionOption.isSelected() || largeExtensionOption.isSelected())
            {
                fileSuffixes.addAll(ConfigurationManager.getList("file_enumeration.medium_extension_list"));
            }
            if (largeExtensionOption.isSelected())
            {
                fileSuffixes.addAll(ConfigurationManager.getList("file_enumeration.large_extension_list"));
            }

            fileNames = ConfigurationManager.getList("file_enumeration.small_name_list");
            if (mediumNameOption.isSelected() || largeNameOption.isSelected())
            {
                fileNames.addAll(ConfigurationManager.getList("file_enumeration.medium_name_list"));
            }
            if (largeNameOption.isSelected())
            {
                fileNames.addAll(ConfigurationManager.getList("file_enumeration.large_name_list"));
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
            finding = new Finding(null, getName(), FindingSeverity.INFO, uri, "Files discovered", "Files were discovered by guessing the name", "The following files were discovered by guessing the name: <br>",
                            "If these files were not linked to by the website, they could lead an attacker to unintended areas.", "Review the contents of the files and remove them if they aren't required.", "");
            Scan.getInstance().getFindings().addFinding(finding);
            Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
        }

        finding.setLongDescription(finding.getLongDescription() + uri + "<br/>\n");
    }

    @Override
    public void testByDirectory(final int transactionID, final String directory, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        List<String> tmpFileNames = new ArrayList<String>(fileNames.size());
        List<String> tmpFileSuffixes = new ArrayList<String>(fileSuffixes.size());
        synchronized (listLock)
        {
            tmpFileNames.addAll(fileNames);
            tmpFileSuffixes.addAll(fileSuffixes);
        }

        for (String name : tmpFileNames)
        {
            for (String suffix : tmpFileSuffixes)
            {
                handlePause_isRunning();
                String uri;
                uri = directory + name + "." + suffix;
                if (Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
                {
                    try
                    {
                        StandardHttpTransaction testTransaction = originalTransaction.cloneForSessionReuse(TransactionSource.ENUMERATION, testJobId);
                        testTransaction.getRequestWrapper().setURI(uri, true);
                        testTransaction.setRequestOptions(requestOptions);
                        testTransaction.execute();
                        if (HttpUtils.fileExists(testTransaction.getLogicalResponseCode()))
                        {
                            Scan.getInstance().getCategorizerQueue().addTransaction(testTransaction);
                            logFinding(uri);
                        }
                    }
                    catch (UnrequestableTransaction e)
                    {
                        LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
                    }
                }
                else
                {
                    LOGGER.debug(uri + " cannot be requested by file enumerator");
                }
            }
        }
    }

}
