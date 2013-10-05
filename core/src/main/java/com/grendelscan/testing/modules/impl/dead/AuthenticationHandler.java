package com.grendelscan.testing.modules.impl.dead;
//package com.grendelscan.smashers.hidden;
//
//import java.util.Collection;
//
//import com.grendelscan.data.database.DataNotFoundException;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.grendelscan.requester.authentication.AuthenticationPackage;
//import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
//import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
//import com.grendelscan.scan.InterruptedScanException;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.smashers.utils.SessionIDTesting.SessionID;
//import com.grendelscan.smashers.utils.SessionIDTesting.SessionIDLocation;
//import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
//import com.grendelscan.smashers.AbstractSmasher;
//import com.grendelscan.smashers.types.ByAuthenticationPackageTest;
//
//public class AuthenticationHandler extends AbstractSmasher implements ByAuthenticationPackageTest
//{
//	private final static String SESSION_IDS_INITIALIZED = "session_ids_initialized";
//
//	@Override
//	public boolean alwaysEnabled()
//	{
//		return true;
//	}
//
//	@Override
//	public String getDescription()
//	{
//		return "Simple module to handle the authentication packages. And look for session IDs." +
//				"It will request them and send the results off for sessionID testing " +
//				"and spidering";
//	}
//
//	@Override
//	public TestModuleGUIPath getGUIDisplayPath()
//	{
//		return TestModuleGUIPath.HIDDEN;
//	}
//
//	@Override
//	public int getModuleNumber()
//	{
//		return 12;
//	}
//
//	@Override
//	public String getName()
//	{
//
//		return "AuthPackageHandler";
//	}
//
//
//	@Override
//	public boolean hidden()
//	{
//		return true;
//	}
//
//
//	@Override
//	public boolean isExperimental()
//	{
//		return false;
//	}
//
//	@Override
//	public void testAuthenticationPackage(AuthenticationPackage authenticationPackage) throws InterruptedScanException
//	{
//		handlePause_isRunning();
//		try
//		{
//			boolean initialized = false;
//			try
//			{
//				initialized = Scan.getInstance().getTestData().getBoolean(SESSION_IDS_INITIALIZED);
//			}
//			catch (DataNotFoundException e)
//			{
//				// no problem, it isn't initialized
//			}
//			
//			if (!initialized)
//			{
//				Scan.getInstance().getTestData().setBoolean(SESSION_IDS_INITIALIZED, true);
//				StandardHttpTransaction loginTransactionNoTesting = authenticationPackage.createAnyLoginTransaction();
//				loginTransactionNoTesting.getRequestOptions().followRedirects = false;
//				loginTransactionNoTesting.execute();
//
//				// If cookies were set, look for session IDs, but only once
//				if (loginTransactionNoTesting.getResponseWrapper().getHeaders().getHeaders("Set-Cookie").size() > 0)
//				{
//					SessionID.getInstance().seedSessionIDDatabase(loginTransactionNoTesting, "Module " + getModuleNumber());
//				}
//
//				SessionIDLocation sessionIDLocation = SessionID.getInstance().findSessionIDLocation(loginTransactionNoTesting);
//				Scan.getInstance().getCategorizers().getInitialAuthenticationCategorizer()
//						.analyzeAuthentication(loginTransactionNoTesting, sessionIDLocation);
//			}
//
//			Collection<StandardHttpTransaction> transactions = authenticationPackage.createLoginTransactions();
//			for (StandardHttpTransaction transaction : transactions)
//			{
//				transaction.setRequestOptions(requestOptions);
//			}
//			Scan.getInstance().getRequesterQueue().addTransactions(transactions);
//		}
//		catch (UnrequestableTransaction e)
//		{
//			LOGGER.warn(getName() + " request unrequestable (" + e.toString() + ")", e);
//		}
//	}
//
//}
