/*
 * 
 * Created on September 15, 2007, 10:49 PM
 */

package com.grendelscan.testing.modules.impl.spidering;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.formatting.DataFormatException;
import com.grendelscan.commons.formatting.DataFormatType;
import com.grendelscan.commons.formatting.DataFormatUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByHttpResponseCodeTest;
import com.grendelscan.testing.utils.spidering.SpiderConfig;
import com.grendelscan.testing.utils.spidering.SpiderUtils;

/**
 * 
 * @author David Byrne
 */

public class UrlRegex extends AbstractTestModule implements ByHttpResponseCodeTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlRegex.class);

    private final Pattern urlPattern;

    public UrlRegex()
    {
        requestOptions.testTransaction = true;
        addConfigurationOption(SpiderConfig.ignoredParameters);
        addConfigurationOption(SpiderConfig.spiderStyle);

        urlPattern = Pattern.compile("(https?://[\\-a-z0-9.]+(?::\\d+)?(?:/[^\\x00-\\x20\\x7f\"#<>'\\\\]+))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    }

    @Override
    public String getDescription()
    {
        return "Searches all responses for simple, full URLs using a regex. " + "Enable this module ONLY if you are familiar with the application. " + "For example, if there is a link that deletes content, this module "
                        + "won't know that it's dangerous. The risk can be mitigated by " + "using the URL blacklist feature. Note that the HTTP standard " + "states that GET should only be used for requests that will not "
                        + "modify data. That doesn't mean that all applications comply.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SPIDER;
    }

    public String getLongName()
    {
        return getName();
    }

    @Override
    public String getName()
    {
        return "URL-Regex";
    }

    @Override
    public String[] getResponseCodes()
    {
        return new String[] { "200" };
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    @Override
    public void testByHttpResponseCode(final int transactionID, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        if (!MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
        {
            return;
        }
        String response = new String(transaction.getResponseWrapper().getBody());
        Matcher matcher = urlPattern.matcher(response);

        while (matcher.find())
        {
            handlePause_isRunning();
            String url;
            try
            {
                url = new String(DataFormatUtils.decodeData(matcher.group(1).getBytes(), DataFormatType.HTML_BASIC_ENTITIES));
            }
            catch (DataFormatException e1)
            {
                LOGGER.error("Problem decoding HTML (" + matcher.group(1) + "): " + e1.toString(), e1);
                continue;
            }
            try
            {
                if (SpiderUtils.getInstance().isUrlSpiderable(url, true))
                {
                    StandardHttpTransaction spiderTransaction = transaction.cloneForReferer(TransactionSource.SPIDER, testJobId);
                    spiderTransaction.getRequestWrapper().setURI(url, true);
                    spiderTransaction.setRequestOptions(requestOptions);
                    Scan.getInstance().getRequesterQueue().addTransaction(spiderTransaction);
                }
                else
                {
                    LOGGER.debug("URL is not requestable: " + url);
                }
            }
            catch (URISyntaxException e)
            {
                LOGGER.warn("Invalid URL discovered with regex (" + url + "): " + e.toString());
            }
        }
    }

}
