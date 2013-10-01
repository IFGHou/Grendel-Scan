/**
 * 
 */
package com.grendelscan.commons.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.cobra_grendel.html.FormInput;
import org.cobra_grendel.html.domimpl.HTMLBaseInputElement;
import org.cobra_grendel.html.domimpl.HTMLButtonElementImpl;
import org.cobra_grendel.html.domimpl.HTMLFormElementImpl;
import org.cobra_grendel.html.domimpl.HTMLInputElementImpl;
import org.cobra_grendel.html.domimpl.HTMLOptionElementImpl;
import org.cobra_grendel.html.domimpl.HTMLSelectElementImpl;
import org.cobra_grendel.html.domimpl.HTMLTextAreaElementImpl;
import org.cobra_grendel.html.js.Executor;
import org.mozilla.javascript.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptGroupElement;

import com.grendelscan.commons.FileUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.collections.CollectionUtils;
import com.grendelscan.commons.http.HttpConstants;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.dataHandling.containers.HtmlQueryContainer;
import com.grendelscan.commons.http.dataHandling.containers.TransactionContainer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;

/**
 * @author david
 * 
 */
public class HtmlFormUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlFormUtils.class);
    private static final String[] MODIFABLE_INPUT_TAG_NAMES = new String[] { "INPUT", "TEXTAREA", "SELECT" };
    private static final String[] ALL_INPUT_TAG_NAMES = new String[] { "INPUT", "TEXTAREA", "SELECT", "BUTTON" };

    private static String buildAction(final StandardHttpTransaction originalTransaction, final HTMLFormElementImpl form) throws UnrequestableTransaction
    {
        String action = URIStringUtils.cleanupWhitespace(form.getAction());
        try
        {
            if (action == null || action.isEmpty())
            {
                action = originalTransaction.getRequestWrapper().getURI();
            }
            if (!URIStringUtils.isAbsolute(action))
            {
                URI base = new URI(form.getBaseURI()); // Need to use this instead of the document URL. May not be the same
                action = base.resolve(action).toASCIIString();
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.warn("Problem with form action (" + action + "): " + e.toString(), e);
            throw new UnrequestableTransaction("URL format problem", e);
        }
        return action;
    }

    private static void buildQuery(final StandardHttpTransaction transaction, final HTMLFormElementImpl form)
    {
        ArrayList<FormInput> formInputs = new ArrayList<FormInput>();

        for (HTMLBaseInputElement element : getAllQueryElements(form))
        {
            formInputs.addAll(CollectionUtils.arrayToCollection(element.getFormInputs()));
        }

        for (FormInput input : formInputs)
        {
            java.io.File file = input.getFileValue();
            String value;
            if (file == null)
            {
                value = input.getTextValue();
            }
            else
            {
                value = FileUtils.readFile(file.getAbsolutePath());
            }

            TransactionContainer transactionContainer = transaction.getTransactionContainer();
            HtmlQueryContainer<?> htmlQueryContainer = transactionContainer.getOrCreateHtmlQueryContainer();
            htmlQueryContainer.addParameter(input.getName(), value);
        }
    }

    private static StandardHttpTransaction buildRequest(final TransactionSource source, final StandardHttpTransaction originalTransaction, final HTMLFormElementImpl form, final int testJobId) throws UnrequestableTransaction
    {
        String method = form.getMethod().toUpperCase();
        if (method == null || method.isEmpty())
        {
            method = "GET";
        }
        String action = buildAction(originalTransaction, form);
        StandardHttpTransaction transaction = originalTransaction.cloneForReferer(source, testJobId);
        transaction.getRequestWrapper().setMethod(method);
        transaction.getRequestWrapper().setURI(action, true);
        setEncodingType(transaction, method, form);
        buildQuery(transaction, form);
        return transaction;
    }

    public static HTMLFormElementImpl findForm(final Document doc, final String formHash)
    {
        NodeList forms = doc.getElementsByTagName("FORM");
        for (int index = 0; index < forms.getLength(); index++)
        {
            HTMLFormElementImpl form = (HTMLFormElementImpl) forms.item(index);
            if (getFormHash(form).equals(formHash))
            {
                return form;
            }
        }
        return null;
    }

    public static List<HTMLBaseInputElement> getAllQueryElements(final HTMLFormElementImpl form)
    {
        List<HTMLBaseInputElement> elements = new ArrayList<HTMLBaseInputElement>(1);
        for (String name : ALL_INPUT_TAG_NAMES)
        {
            NodeList nodes = form.getElementsByTagName(name);
            for (int index = 0; index < nodes.getLength(); index++)
            {
                HTMLBaseInputElement element = (HTMLBaseInputElement) nodes.item(index);
                elements.add(element);
            }
        }
        return elements;
    }

    public static String getFormHash(final HTMLFormElement form)
    {
        List<Class<? extends HTMLElement>> formElements = new ArrayList<Class<? extends HTMLElement>>();
        formElements.add(HTMLButtonElementImpl.class);
        formElements.add(HTMLInputElementImpl.class);
        formElements.add(HTMLSelectElementImpl.class);
        formElements.add(HTMLTextAreaElementImpl.class);
        formElements.add(HTMLBaseInputElement.class);
        formElements.add(HTMLFormElementImpl.class);
        formElements.add(HTMLOptGroupElement.class);
        formElements.add(HTMLOptionElementImpl.class);

        return StringUtils.md5Hash(HtmlNodeWriter.write(form, true, formElements));
    }

    public static List<HTMLInputElementImpl> getInputElements(final HTMLFormElementImpl form)
    {
        List<HTMLInputElementImpl> elements = new ArrayList<HTMLInputElementImpl>(1);
        NodeList nodes = form.getElementsByTagName("INPUT");
        for (int index = 0; index < nodes.getLength(); index++)
        {
            HTMLInputElementImpl element = (HTMLInputElementImpl) nodes.item(index);
            if (element.getName() == null || element.getName().isEmpty())
            {
                continue; // Can't use
            }
            elements.add(element);
        }
        return elements;
    }

    public static List<HTMLBaseInputElement> getModifiableQueryElements(final HTMLFormElementImpl form)
    {
        List<HTMLBaseInputElement> elements = new ArrayList<HTMLBaseInputElement>(1);
        for (String name : MODIFABLE_INPUT_TAG_NAMES)
        {
            NodeList nodes = form.getElementsByTagName(name);
            for (int index = 0; index < nodes.getLength(); index++)
            {
                HTMLBaseInputElement element = (HTMLBaseInputElement) nodes.item(index);
                elements.add(element);
            }
        }
        return elements;
    }

    private static void setEncodingType(final StandardHttpTransaction transaction, final String method, final HTMLFormElementImpl form)
    {
        if (method.equals(HttpPost.METHOD_NAME))
        {
            if (form.getEnctype() == null || form.getEnctype().isEmpty())
            {
                transaction.getRequestWrapper().getHeaders().addHeader(HttpHeaders.CONTENT_TYPE, HttpConstants.ENCODING_APPLICATION_X_WWW_FORM_URLENCODED);
            }
            else if (form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_MULTIPART_FORM_DATA) || form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_MULTIPART_FORM_DATA)
                            || form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_APPLICATION_X_WWW_FORM_URLENCODED))
            {
                transaction.getRequestWrapper().getHeaders().addHeader(HttpHeaders.CONTENT_TYPE, form.getEnctype().toLowerCase());
            }
            else
            {
                IllegalStateException e = new IllegalStateException("Unknown encoding type: " + form.getEnctype());
                LOGGER.error(e.toString(), e);
                throw e;
            }
        }
    }

    public static StandardHttpTransaction submitForm(final TransactionSource source, final StandardHttpTransaction originalTransaction, final HTMLFormElementImpl form, final int testJobId) throws UnrequestableTransaction
    {
        Function onsubmit = form.getOnsubmit();
        if (onsubmit != null)
        {
            if (!Executor.executeFunction(form, onsubmit, null))
            {
                LOGGER.info("The onsubmit handler failed, but we're submitting the form anyway");
            }
        }
        StandardHttpTransaction transaction = buildRequest(source, originalTransaction, form, testJobId);

        return transaction;
    }

}
