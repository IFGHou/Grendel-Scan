/**
 * 
 */
package com.grendelscan.smashers.spidering;

import java.util.regex.Pattern;

import org.cobra_grendel.html.domimpl.HTMLBaseInputElement;
import org.cobra_grendel.html.domimpl.HTMLFormElementImpl;
import org.cobra_grendel.html.domimpl.HTMLInputElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.grendelscan.commons.html.HtmlFormUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.ByHtmlFormTest;

/**
 * @author david
 * 
 */
public class AutoAuthentication extends AbstractSmasher implements ByHtmlFormTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoAuthentication.class);

    static final Pattern CHANGE_PASSWORD_URI_PATTERN = Pattern.compile("(?:cha?nge?)", Pattern.CASE_INSENSITIVE);

    static final Pattern CHANGE_PASSWORD_FIELD_PATTERN = Pattern.compile("(?:new|old|pre?v|rep|co?nfi?rm)", Pattern.CASE_INSENSITIVE);

    private static final Pattern USERNAME_FIELD_REGEX = Pattern.compile("(?:use?r|name)", Pattern.CASE_INSENSITIVE);

    public AutoAuthentication()
    {
        requestOptions.reason = "Auto Authentication";
        requestOptions.followRedirects = true;
        requestOptions.testTransaction = true;
        requestOptions.testRedirectTransactions = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.AbstractSmasher#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.AbstractSmasher#getGUIDisplayPath()
     */
    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.SPIDER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.AbstractSmasher#getName()
     */
    @Override
    public String getName()
    {
        return "Automatic Authentication Submitter";
    }

    @Override
    public boolean hidden()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.AbstractSmasher#isExperimental()
     */
    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private boolean isPassword(final HTMLBaseInputElement input)
    {
        if (input instanceof HTMLInputElementImpl && ((HTMLInputElementImpl) input).getType().equalsIgnoreCase("password"))
        {
            return true;
        }

        return false;
    }

    private boolean isUsername(final String name)
    {
        return USERNAME_FIELD_REGEX.matcher(name).find();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByHtmlFormTest#testByHtmlForm(int, org.w3c.dom.html2.HTMLElement, java.lang.String, int)
     */
    @Override
    public void testByHtmlForm(final int transactionID, final String formHash, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        if (originalTransaction.isLoginTransaction() || originalTransaction.isAuthenticated())
        {
            return;
        }
        Document dom = originalTransaction.getResponseWrapper().getResponseDOMClone();
        HTMLFormElementImpl form = HtmlFormUtils.findForm(dom, formHash);
        String uri = originalTransaction.getRequestWrapper().getURI().toString();
        if (CHANGE_PASSWORD_URI_PATTERN.matcher(uri).find())
        {
            return;
        }

        int passwordCount = 0;
        for (HTMLInputElementImpl input : HtmlFormUtils.getInputElements(form))
        {
            if (input.getType() != null && input.getType().equalsIgnoreCase("password"))
            {
                passwordCount++;
            }
            if (CHANGE_PASSWORD_FIELD_PATTERN.matcher(input.getName()).find())
            {
                return;
            }
            if (passwordCount > 1)
            {
                return; // If there's more than one password field, it's probably a change password prompt
            }
        }
        if (passwordCount == 1)
        {
            for (String username : Scan.getScanSettings().getReadOnlyAuthenticationCredentials().keySet())
            {
                boolean setPassword = false;
                boolean setUser = false;
                String password = Scan.getScanSettings().getReadOnlyAuthenticationCredentials().get(username);
                try
                {
                    for (HTMLBaseInputElement input : HtmlFormUtils.getInputElements(form))
                    {
                        if (isUsername(input.getName()))
                        {
                            setUser = true;
                            input.setValue(username);
                        }
                        else if (isPassword(input))
                        {
                            setPassword = true;
                            input.setValue(password);
                        }
                    }
                    if (setUser && setPassword)
                    {
                        StandardHttpTransaction login = HtmlFormUtils.submitForm(TransactionSource.AUTHENTICATION, originalTransaction, form, testJobId);
                        login.setRequestOptions(requestOptions);
                        login.setUsername(username);
                        login.setLoginTransaction(true);
                        Scan.getInstance().getRequesterQueue().addTransaction(login);
                    }
                }
                catch (UnrequestableTransaction e)
                {
                    LOGGER.warn("Problem with login form: " + e.toString());
                }
            }
        }
    }

}
