package com.grendelscan.smashers.fileEnumeration;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.ByFileTest;

public class BackupFiles extends AbstractSmasher implements ByFileTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BackupFiles.class);

    private final String[] filePrefixes;
    private final String[] fileSuffixes;

    public BackupFiles()
    {
        filePrefixes = ConfigurationManager.getStringArray("file_enumeration.backup_prefixes");
        fileSuffixes = ConfigurationManager.getStringArray("file_enumeration.backup_suffixes");
    }

    @Override
    public String getDescription()
    {
        return "Checks for backups of files observed during the scan. " + "The tested extensions are defined in conf/file_enumeration.conf. Unlike " + "the file and directory enumerators, this doesn't take too long.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.FILE_ENUMERATION;
    }

    @Override
    public String getName()
    {
        return "Backup file enumerator";
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    @Override
    public void testByFile(final int transactionID, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        // We only are interested in pages with web content
        if (!MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            return;
        }
        String filename;
        String hostUri;
        String dir;
        try
        {
            filename = URIStringUtils.getFilename(transaction.getRequestWrapper().getAbsoluteUriString());
            hostUri = URIStringUtils.getHostUriWithoutTrailingSlash(transaction.getRequestWrapper().getAbsoluteUriString());
            dir = URIStringUtils.getDirectory(transaction.getRequestWrapper().getAbsoluteUriString());
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }
        if (filename.equals("") && (dir.equals("") || dir.equals("/")))
        {
            return;
        }

        for (String suffix : fileSuffixes)
        {
            Scan.getInstance().getTesterQueue().handlePause_isRunning();
            testFile(hostUri + dir + filename + "." + suffix, transaction, testJobId);
        }

        for (String prefix : filePrefixes)
        {
            Scan.getInstance().getTesterQueue().handlePause_isRunning();
            testFile(hostUri + dir + prefix + filename, transaction, testJobId);
        }
    }

    private void testFile(final String uri, final StandardHttpTransaction referer, final int testJobId) throws InterruptedScanException
    {
        if (!Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
        {
            LOGGER.debug(uri + " cannot be requested by backup files modules");
            return;
        }
        try
        {
            StandardHttpTransaction testTransaction = referer.cloneFullRequest(TransactionSource.ENUMERATION, testJobId);
            testTransaction.getRequestWrapper().setURI(uri, true);
            testTransaction.setRequestOptions(requestOptions);
            testTransaction.execute();
            if (HttpUtils.fileExists(testTransaction.getLogicalResponseCode()))
            {
                Scan.getInstance().getCategorizerQueue().addTransaction(testTransaction);
                String longDescription = "A file that appears to be a backup of " + HtmlUtils.makeLink(referer.getRequestWrapper().getAbsoluteUriString()) + " has been located at "
                                + HtmlUtils.makeLink(testTransaction.getRequestWrapper().getAbsoluteUriString());
                String impact = "Backup files may be served up as text files by the web server, which could " + "reveal their source code. This could help an attacker to identify vulnerabilities " + "that would be difficult to find during blind attacks. ";
                String recomendations = "Remove all backup files from production web servers.";
                Finding event = new Finding(null, getName(), FindingSeverity.LOW, uri, "Backup file located", "A file that appears to be a backup of " + referer.getRequestWrapper().getAbsoluteUriString() + " has been located.", longDescription, impact,
                                recomendations, "");
                Scan.getInstance().getFindings().addFinding(event);
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn("Request unrequestable in " + getName() + " (" + uri + "): " + e.toString(), e);
        }

    }
}
