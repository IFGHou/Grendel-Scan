package com.grendelscan.tests.testModules.hidden;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataTest;

public class TokenSubmitter extends TestModule implements ByRequestDataTest
{

	public TokenSubmitter()
	{
		requestOptions.followRedirects = true;
		requestOptions.testRedirectTransactions = true;
		requestOptions.testTransaction = true;
	}
	
	
	@Override
	public String getDescription()
	{
		return "Sends a random token into each query parameter. Not very useful on its own, "
				+ "but it helps track output & output validation (XSS, etc).";
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
	 * @see
	 * com.grendelscan.tests.testTypes.ByHttpQueryParameterTest#testByQueryParameter
	 * (com.grendelscan.requester.http.payloads.QueryParameter)
	 */
	@Override
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException
	{
		handlePause_isRunning();
		StandardHttpTransaction originalTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionId);
		StandardHttpTransaction testTransaction = originalTransaction.cloneFullRequest(TransactionSource.MISC_TEST, testJobId);
		MutableData newTestData = (MutableData) DataContainerUtils.resolveReferenceCousin(testTransaction, datum);
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
			Log.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
		}
	}




}
