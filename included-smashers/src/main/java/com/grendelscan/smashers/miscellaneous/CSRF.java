package com.grendelscan.smashers.miscellaneous;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.cobra_grendel.html.domimpl.HTMLFormElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.commons.RegexUtils;
import com.grendelscan.commons.html.HtmlFormUtils;
import com.grendelscan.commons.html.HtmlNodeUtilities;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.factories.UriFactory;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.settings.ConfigChangeHandler;
import com.grendelscan.smashers.settings.TextOption;
import com.grendelscan.smashers.types.ByHtmlFormTest;

public class CSRF extends AbstractSmasher implements ByHtmlFormTest
{
    private class inputComparator implements Comparator<HTMLElement>
    {
        @Override
        public int compare(final HTMLElement element1, final HTMLElement element2)
        {
            String n1 = element1.getAttribute("name");
            String n2 = element2.getAttribute("name");

            if (n1 == null)
            {
                n1 = "";
            }
            if (n2 == null)
            {
                n2 = "";
            }

            return n1.compareTo(n2);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CSRF.class);

    // private List<String> findings;
    private Finding finding;
    private static final String FINDING_NAME = "csrf_finding_number";
    // private final Set<String> testedForms;
    private final TextOption whitelistConfigOption;

    private Pattern whitelistPatterns[];
    private final Object patternLock = new Object();

    public CSRF()
    {
        ConfigChangeHandler changeHandler = new ConfigChangeHandler()
        {
            @Override
            public void handleChange()
            {
                initializeWhitelist();
            }
        };
        whitelistConfigOption = new TextOption("Action regex whitelist", ".*", "Each line is a regular expression that will be compared against the form's action.", true, changeHandler);
        addConfigurationOption(whitelistConfigOption);
        try
        {
            finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME));
        }
        catch (DataNotFoundException e)
        {
            // no problem
        }
    }

    private String getAbsoluteAction(final HTMLElement element)
    {
        String action = element.getAttribute("action");
        String uri = "";
        if (action == null || action.equals(""))
        {
            action = element.getBaseURI();
        }
        try
        {
            uri = UriFactory.makeAbsoluteUri(action, element.getBaseURI()).toASCIIString();
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Invalid action format: " + e.toString(), e);
        }
        return uri;
    }

    @Override
    public String getDescription()
    {
        return "Tests for cross-site request forgery (CSRF) vulnerabilities in queries " + "based on HTML forms. Some queries forms (e.g. a search function) don't " + "need to be resistant to CSRF. The regular expression white-list is "
                        + "tested against the absolute URL version of the form's action. Each " + "line contains a new regex. Any invalid regex formats will be " + "skipped, so be careful. Note that the default is to test all forms.";
    }

    @Override
    public String getExperimentalText()
    {
        return "The CSRF module is particularly prone to false positive findings.";
    }

    private String getFormProfile(final HTMLElement element, final boolean useValues)
    {
        String profile = getAbsoluteAction(element) + "?";
        if (element.getTagName().equalsIgnoreCase("form"))
        {
            List<HTMLElement> inputs = HtmlNodeUtilities.getChildElements(element, "input");
            inputs.addAll(HtmlNodeUtilities.getChildElements(element, "textarea"));
            Collections.sort(inputs, new inputComparator());
            for (HTMLElement input : inputs)
            {
                String name = input.getAttribute("name");
                if (name != null && !name.equals(""))
                {
                    profile += name;
                    if (useValues)
                    {
                        String value = "";
                        if (profile.equalsIgnoreCase("textarea"))
                        {
                            Node child = input.getFirstChild();
                            if (child != null)
                            {
                                value = child.getNodeValue();
                            }
                        }
                        else
                        {
                            value = input.getAttribute("value");
                        }

                        if (value == null)
                        {
                            value = "";
                        }
                        profile += "=" + value;
                    }
                    profile += "&";
                }
            }
        }
        return profile.toUpperCase();
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.MISCELLANEOUS_ATTACKS;
    }

    @Override
    public String getName()
    {
        return "CSRF";
    }

    private String[] getParamNames(final HTMLElement element)
    {
        List<String> params = new ArrayList<String>(1);

        List<HTMLElement> inputs = HtmlNodeUtilities.getChildElements(element, "input");
        inputs.addAll(HtmlNodeUtilities.getChildElements(element, "textarea"));
        for (HTMLElement input : inputs)
        {
            String name = input.getAttribute("name");
            if (name != null && !name.equals(""))
            {
                params.add(name);
            }
        }
        return params.toArray(new String[0]);
    }

    // Intentionally given default visibility
    void initializeWhitelist()
    {
        synchronized (patternLock)
        {
            whitelistPatterns = RegexUtils.stringToPatterns(whitelistConfigOption.getValue());
        }
    }

    @Override
    public boolean isExperimental()
    {
        return true;
    }

    private synchronized void logFinding(final String findingDescription)
    {
        if (finding == null)
        {

            String title = "Potential CSRF detected";
            String shortDescription = "A cross-site request forgery (CSRF) vulnerability may have been identified.";
            String longDescription = "One or more cross-site request forgery (CSRF) vulnerabilities may have been identified. " + "CSRF allows an attacker to force a user to execute arbitrary commands "
                            + "against the vulnerable website. This is possible when the structure of the " + "command is predictable. If the command can be requested as a GET, then a "
                            + "simple IMG tag on an attack website can force the browser to send the " + "command. A POST request can be sent using some simple JavaScript. The " + "browser will send any cookies or authentication credentials associated "
                            + "with the targeted attack, because it has no way of knowing that the " + "request was not intentionally executed by the user.<br>" + "<br>" + "A list of queries that appear to be vulnerable to CSRF is below. "
                            + "A specific form may be found on multiple pages, but was only tested once.<br>" + "<br>";

            longDescription += findingDescription;

            String impact = "The impact varies depending on the nature of the command.";
            String recomendations = "If this query is used to execute a sensitive function " + "(e.g. creating a new user), then CSRF is a significant vulnerability. It " + "can be prevented by using a random token as a query parameter. Before "
                            + "executing the command, the application should confirm that the token sent by " + "the browser matches the original token sent by the application. Since an "
                            + "attacker couldn't know the random token (without a secondary vulnerability " + "like XSS), a CSRF attack is not possible.";
            String references = HtmlUtils.makeLink("http://www.owasp.org/index.php/Cross-Site_Request_Forgery");
            finding = new Finding(null, getName(), FindingSeverity.HIGH, "See description", title, shortDescription, longDescription, impact, recomendations, references);
            Scan.getInstance().getFindings().addFinding(finding);
            Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
        }
        else
        {
            finding.setLongDescription(finding.getLongDescription() + findingDescription);
        }
    }

    private void record(final StandardHttpTransaction originalTransaction, final String action, final String[] params)
    {
        originalTransaction.writeToDisk();
        String query = "";
        for (int i = 0; i < params.length; i++)
        {
            if (params.length > 1)
            {
                if (i == params.length - 1)
                {
                    query += ", and ";
                }
                else if (i > 0)
                {
                    query += ", ";
                }
            }
            query += "\"" + params[i] + "\"";
        }

        String description = "<b>Action</b>: " + action + "<br>" + "<b>Parameter names</b>: " + query + "<br>" + "<b>Original transaction</b>: " + HtmlUtils.makeLink(originalTransaction.getSavedUrl(), String.valueOf(originalTransaction.getId())) + "<br>"
                        + "<br>\n";
        logFinding(description);
    }

    private boolean shouldBeTested(final String action)
    {
        boolean test = false;
        if (whitelistPatterns == null || whitelistPatterns.length == 0)
        {
            return true;
        }
        for (Pattern white : whitelistPatterns)
        {
            if (white.matcher(action).find())
            {
                test = true;
                break;
            }
        }
        return test;
    }

    @Override
    public void testByHtmlForm(final int transactionID, final String formHash, final int testJobId) throws InterruptedScanException
    {
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        Document dom;
        dom = originalTransaction.getResponseWrapper().getResponseDOMClone();
        HTMLFormElementImpl form = HtmlFormUtils.findForm(dom, formHash);
        String action = getAbsoluteAction(form);

        if (!shouldBeTested(action))
        {
            return;
        }
        handlePause_isRunning();

        StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        testTransaction.setRequestOptions(requestOptions);
        try
        {
            testTransaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
            return;
        }
        for (HTMLElement candidate : HtmlNodeUtilities.getChildElements(testTransaction.getResponseWrapper().getResponseDOM(), "form"))
        {
            // If the form is identical
            if (getFormProfile(form, true).equals(getFormProfile(candidate, true)))
            {
                record(originalTransaction, action, getParamNames(form));
                break;
            }
        }
    }

}
