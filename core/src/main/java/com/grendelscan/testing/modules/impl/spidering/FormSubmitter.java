package com.grendelscan.testing.modules.impl.spidering;

import java.util.HashSet;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.cobra_grendel.html.domimpl.HTMLBaseInputElement;
import org.cobra_grendel.html.domimpl.HTMLFormElementImpl;
import org.cobra_grendel.html.domimpl.HTMLInputElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.collections.CollectionUtils;
import com.grendelscan.commons.html.HtmlFormUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;
import com.grendelscan.testing.modules.types.ByHtmlFormTest;
import com.grendelscan.testing.utils.spidering.SpiderConfig;

/*
 * TODO: Textarea and other complex inputs may be broken, submit empty fields, radio nodes may be broken
 */
public class FormSubmitter extends AbstractTestModule implements ByHtmlFormTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FormSubmitter.class);
    public static String formResponsePrefix = "formResponse";
    private final String defaultText;
    private final SelectableOption getMethodOption;
    private final SelectableOption postMethodOption;

    public FormSubmitter()
    {
        addConfigurationOption(SpiderConfig.ignoredParameters);
        addConfigurationOption(SpiderConfig.spiderStyle);
        getMethodOption = new SelectableOption("GET method", true, "Requests forms that use the GET method", null);
        postMethodOption = new SelectableOption("POST method", false, "Requests forms that use the POST method", null);
        addConfigurationOption(getMethodOption);
        addConfigurationOption(postMethodOption);
        requestOptions.testTransaction = true;
        requestOptions.reason = "Form submitter";
        defaultText = ConfigurationManager.getString("default_form_values.default_text");
    }

    private void fillOutForm(final HTMLFormElementImpl form)
    {
        HashSet<String> radioNodes = new HashSet<String>();
        for (HTMLBaseInputElement input : HtmlFormUtils.getModifiableQueryElements(form))
        {

            // Begin initialize attributes
            String name = input.getAttribute("name");
            if (name == null || name.equals(""))
            {
                // If there is no name, it can't be used in a submission, so
                // skip it
                continue;
            }

            int length;
            String lengthStr = input.getAttribute("length");
            try
            {
                length = Integer.valueOf(lengthStr);
            }
            catch (NumberFormatException e)
            {
                length = 0;
            }

            String type = input.getAttribute("type");
            if (type == null || type.equals(""))
            {
                type = "text";
            }

            // End initialize attributes

            // Handle based on input type
            if (type.equalsIgnoreCase("radio") && !radioNodes.contains(name))
            {
                // If this is the first radio button of the group, pick it
                radioNodes.add(name);
            }
            // if the value wasn't already found in the tag, and the parameter
            // isn't ignored
            else if ((input.getValue() == null || input.getValue().isEmpty()) && !CollectionUtils.containsStringIgnoreCase(SpiderConfig.ignoredParameters.getReadOnlyData(), name))
            {
                if (type.equalsIgnoreCase("text") || type.equalsIgnoreCase("textarea"))
                {

                    String value = ConfigurationManager.getString("default_form_values." + name, name + " " + defaultText);
                    if (length > 0)
                    {
                        // We're truncating because this is only for spidering,
                        // not for testing
                        value = value.substring(0, length);
                    }
                    input.setValue(value);
                }

                else if (type.equalsIgnoreCase("password"))
                {
                    input.setValue(ConfigurationManager.getString("default_form_values.password", name + "password"));
                }

                else if (type.equalsIgnoreCase("checkbox"))
                {
                    ((HTMLInputElementImpl) input).setChecked(Math.random() < 0.5);
                }

                else if (type.equalsIgnoreCase("hidden"))
                {
                    // The server might be expecting a blank hidden field, so do nothing
                }
            }
        }

    }

    @Override
    public String getDescription()
    {
        return "Fills out a form with guessed data to continue spidering. The data " + "is based on the input element type and the name of the query " + "parameter. The values are defined in conf/default_form_values.conf. "
                        + "Enable this module ONLY if you are familiar with the application. " + "For example, if there is a form that deletes content, this module " + "won't know that it's dangerous. The risk can be mitigated by "
                        + "using the URL blacklist feature, and by only using the GET method.";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SPIDER;
    }

    @Override
    public String getName()
    {
        return "Form baseline";
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByHtmlFormTest#testByHtmlForm(int, org.w3c.dom.html2.HTMLFormElement, int)
     */
    @Override
    public void testByHtmlForm(final int transactionID, final String formHash, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        Document dom = transaction.getResponseWrapper().getResponseDOMClone();
        HTMLFormElementImpl form = HtmlFormUtils.findForm(dom, formHash);
        String method = form.getMethod();
        if (method != null && method.equalsIgnoreCase(HttpPost.METHOD_NAME))
        {
            if (!postMethodOption.isSelected())
            {
                LOGGER.debug("Skipping this form, POST not enabled");
                return;
            }
            method = HttpPost.METHOD_NAME; // to make sure it's all upper case.
        }
        else
        {
            if (!getMethodOption.isSelected())
            {
                LOGGER.debug("Skipping this form, GET not enabled");
                return;
            }
            method = HttpGet.METHOD_NAME;
        }

        StandardHttpTransaction newTransaction;
        try
        {
            fillOutForm(form);
            newTransaction = HtmlFormUtils.submitForm(TransactionSource.SPIDER, transaction, form, testJobId);
            if (newTransaction != null)
            {
                handlePause_isRunning();
                newTransaction.setRequestOptions(requestOptions);
                Scan.getInstance().getRequesterQueue().addTransaction(newTransaction);
            }
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.trace("Form action is not requestable", e);
        }

    }
}
