package com.grendelscan.smashers.architecture;

import java.util.Collection;
import java.util.HashSet;

import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.hidden.TokenSubmitter;
import com.grendelscan.smashers.types.ByOutputContextTest;
import com.grendelscan.smashers.utils.tokens.TokenContext;
import com.grendelscan.smashers.utils.tokens.TokenContextType;
import com.grendelscan.smashers.utils.tokens.TokenContextTypeUtils;

public class InputOutputFlows extends AbstractSmasher implements ByOutputContextTest
{
    private Finding finding;
    private static final String FINDING_NAME = "input_output_flow_finding_number";

    @Override
    public String getDescription()
    {
        return "Each query parameter will be individually seeded with a token. This module will " + "report on what tokens are used in output, even if it is in a future request. "
                        + "The tokens are the same as what is used for XSS and CRLF injection testing. " + "Parameters can be removed from scope by identifying it as a \"forbidden " + "parameter\" in the scan setup.";
    }

    @Override
    public TokenContextType[] getDesiredContexts()
    {
        return TokenContextTypeUtils.getAllContexts();
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.ARCHITECTURE;
    }

    @Override
    public String getName()
    {
        return "Input / Output flows";
    }

    @Override
    public Class<? extends AbstractSmasher>[] getPrerequisites()
    {
        return new Class[] { TokenSubmitter.class };
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    private synchronized void logFinding(final Collection<TokenContext> contexts)
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
            String table = "<table>\n";
            table += "<tr style=\"font-weight: bold;\"><td>Input URL</td><td>Input parameter name</td><td></tr>\n" + "<tr style=\"font-weight: bold;\"><td>Output URL</td><td>Output context</td></tr>\n"
                            + "<tr style=\"font-weight: bold;\"><td>&nbsp;</td></tr>\n";

            String shortDescript = "Input / output data flows were documented";
            String longDescription = "Random tokens were submitted into all query parameters. The table " + "below lists cases were tokens were observed in output from the server. The links are " + "to the actual transaction.<br>\n<br>\n" + table;
            finding = new Finding(null, getName(), FindingSeverity.INFO, "Multiple", "Input / output flows", shortDescript, longDescription, "These data flows could be vulnerable to attacks such as XSS or CRLF injection", "Further testing may be desirable",
                            "");
            finding.setLongDescriptionFooter("</table>\n");
            Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
            Scan.getInstance().getFindings().addFinding(finding);
        }

        TokenContext firstContext = contexts.toArray(new TokenContext[1])[0];
        StandardHttpTransaction inputTransaction = Scan.getInstance().getTransactionRecord().getTransaction(firstContext.getOriginatingTransactionID());
        StandardHttpTransaction outputTransaction = Scan.getInstance().getTransactionRecord().getTransaction(firstContext.getOutputTransactionID());

        inputTransaction.writeToDisk();
        outputTransaction.writeToDisk();

        String outputText;
        if (inputTransaction.getId() == outputTransaction.getId())
        {
            outputText = "Same as input";
        }
        else
        {
            outputText = HtmlUtils.makeLink(outputTransaction.getSavedUrl(), URIStringUtils.getFileUri(outputTransaction.getRequestWrapper().getURI()));
        }

        HashSet<TokenContextType> types = new HashSet<TokenContextType>();
        for (TokenContext context : contexts)
        {
            types.add(context.getContextType());
        }
        StringBuilder contextText = new StringBuilder();
        for (TokenContextType type : types)
        {
            contextText.append(type.getDescription() + "</br>\n");
        }

        String text = finding.getLongDescription() + "<tr><td>" + HtmlUtils.makeLink(inputTransaction.getSavedUrl(), URIStringUtils.getFileUri(inputTransaction.getRequestWrapper().getURI())) + "</td><td>"
                        + firstContext.getRequestDatum().getReferenceChain().toString() + "</td><td></tr>\n" + "<tr><td>" + outputText + "</td><td>" + contextText.toString() + "</td></tr>\n" + "<tr><td>&nbsp;</td></tr>\n";

        finding.setLongDescription(text);
    }

    @Override
    public void testByOutputContext(final Collection<TokenContext> contexts, final int testJobId)
    {
        logFinding(contexts);
    }
}
