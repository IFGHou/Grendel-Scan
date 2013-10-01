/**
 * 
 */
package com.grendelscan.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptGroupElement;

import com.grendelscan.html.HtmlNodeWriter;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.dataHandling.containers.HtmlQueryContainer;
import com.grendelscan.requester.http.dataHandling.containers.TransactionContainer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.utils.collections.CollectionUtils;
/**
 * @author david
 *
 */
public class HtmlFormUtils
{
	private static final String[] MODIFABLE_INPUT_TAG_NAMES = new String[]{"INPUT", "TEXTAREA", "SELECT"};
	public static List<HTMLBaseInputElement> getModifiableQueryElements(HTMLFormElementImpl form)
	{
		List<HTMLBaseInputElement> elements = new ArrayList<HTMLBaseInputElement>(1);
		for(String name: MODIFABLE_INPUT_TAG_NAMES)
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
	

	private static final String[] ALL_INPUT_TAG_NAMES = new String[]{"INPUT", "TEXTAREA", "SELECT", "BUTTON"};
	public static List<HTMLBaseInputElement> getAllQueryElements(HTMLFormElementImpl form)
	{
		List<HTMLBaseInputElement> elements = new ArrayList<HTMLBaseInputElement>(1);
		for(String name: ALL_INPUT_TAG_NAMES)
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
	
	public static List<HTMLInputElementImpl> getInputElements(HTMLFormElementImpl form)
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
	
	public static StandardHttpTransaction submitForm(TransactionSource source, StandardHttpTransaction originalTransaction, HTMLFormElementImpl form, int testJobId) throws UnrequestableTransaction
	{
		Function onsubmit = form.getOnsubmit();
		if (onsubmit != null)
		{
			if (!Executor.executeFunction(form, onsubmit, null))
			{
				Log.info("The onsubmit handler failed, but we're submitting the form anyway");
			}
		}
		StandardHttpTransaction transaction = buildRequest(source, originalTransaction, form, testJobId);

		return transaction;
	}
	
	private static void buildQuery(StandardHttpTransaction transaction, HTMLFormElementImpl form)
	{
		ArrayList<FormInput> formInputs = new ArrayList<FormInput>();

		for (HTMLBaseInputElement element: getAllQueryElements(form))
		{
			formInputs.addAll(CollectionUtils.arrayToCollection(element.getFormInputs()));
		}

		for (FormInput input: formInputs)
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

	private static String buildAction(StandardHttpTransaction originalTransaction, HTMLFormElementImpl form) throws UnrequestableTransaction 
	{
		String action = URIStringUtils.cleanupWhitespace(form.getAction());
		try
		{
			if ((action == null) || action.isEmpty())
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
			Log.warn("Problem with form action (" + action + "): " + e.toString(), e);
			throw new UnrequestableTransaction("URL format problem" , e);
		}
		return action;
	}
	
	private static void setEncodingType(StandardHttpTransaction transaction, String method, HTMLFormElementImpl form)
	{
		if (method.equals(HttpPost.METHOD_NAME))
		{
			if (form.getEnctype() == null || form.getEnctype().isEmpty())
			{
				transaction.getRequestWrapper().getHeaders().addHeader(HttpHeaders.CONTENT_TYPE, 
						HttpConstants.ENCODING_APPLICATION_X_WWW_FORM_URLENCODED);
			}
			else if (form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_MULTIPART_FORM_DATA) ||
					form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_MULTIPART_FORM_DATA) ||
					form.getEnctype().equalsIgnoreCase(HttpConstants.ENCODING_APPLICATION_X_WWW_FORM_URLENCODED)
					)
			{
				transaction.getRequestWrapper().getHeaders().addHeader(HttpHeaders.CONTENT_TYPE, form.getEnctype().toLowerCase());
			}
			else
			{
				IllegalStateException e = new IllegalStateException("Unknown encoding type: " + form.getEnctype());
				Log.error(e.toString(), e);
				throw e;
			}
		}
	}
	
	private static StandardHttpTransaction buildRequest(TransactionSource source, StandardHttpTransaction originalTransaction, HTMLFormElementImpl form, int testJobId) throws UnrequestableTransaction 
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

	public static HTMLFormElementImpl findForm(Document doc, String formHash)
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

	public static String getFormHash(HTMLFormElement form)
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
	

}
