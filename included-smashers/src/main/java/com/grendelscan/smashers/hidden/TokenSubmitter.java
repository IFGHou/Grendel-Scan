package com.grendelscan.smashers.hidden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestModuleGUIPath;
import com.grendelscan.smashers.types.ByRequestDataLocationTest;
import com.grendelscan.smashers.utils.tokens.TokenTesting;

public class TokenSubmitter extends AbstractSmasher implements ByRequestDataLocationTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSubmitter.class);

    public TokenSubmitter()
    {
        requestOptions.followRedirects = true;
        requestOptions.testRedirectTransactions = true;
        requestOptions.testTransaction = true;
        requestOptions.tokenSubmission = true;
    }

    @Override
    public String getDescription()
    {
        return "Sends a random token into each query parameter. Not very useful on its own, " + "but it helps track output & output validation (XSS, etc).";
    }

    @Override
    public TestModuleGUIPath getGUIDisplayPath()
    {
        return TestModuleGUIPath.HIDDEN;
    }

    @Override
    public String getName()
    {
        return "Token submitter";
    }

    @Override
    public boolean hidden()
    {
        return true;
    }

    @Override
    public boolean isExperimental()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.smashers.types.ByHttpQueryParameterTest#testByQueryParameter (com.grendelscan.commons.http.payloads.QueryParameter)
     */
    @Override
    public void testByRequestData(final int transactionId, final DataReferenceChain chain, final int testJobId) throws InterruptedScanException
    {
        handlePause_isRunning();
        StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);
        ByteData datum;
        try
        {
            datum = (ByteData) DataContainerUtils.resolveReferenceChain(originalTransaction.getTransactionContainer(), chain);
        }
        catch (ClassCastException exception)
        {
            throw new IllegalStateException("Problem following reference chain (" + chain.toString() + ")", exception);
        }

        StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
        ByteData newTestData = (ByteData) DataContainerUtils.resolveReferenceChain(testTransaction.getTransactionContainer(), datum.getReferenceChain());
        testTransaction.setRequestOptions(requestOptions);
        String token = TokenTesting.getInstance().generateToken();
        newTestData.setBytes(token.getBytes());
        TokenTesting.getInstance().recordTokenTest(token, newTestData);
        handlePause_isRunning();
        try
        {
            testTransaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
        }
    }

}
