package com.grendelscan.tests.testModules.dead;
//package com.grendelscan.tests.testModules.hidden;
//
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.tests.libraries.nikto.Nikto;
//import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
//import com.grendelscan.tests.testModules.TestModule;
//import com.grendelscan.tests.testTypes.ByFileTest;
//import com.grendelscan.tests.testTypes.ByResponseHeaderTest;
//
//public class ServerHeaders extends TestModule implements ByResponseHeaderTest
//{
//
//	@Override
//	public String getDescription()
//	{
//		return "Records \"Server\" and \"X-Powered-By\" header values in the Nikto library";
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
//		return 16;
//	}
//
//	@Override
//	public String getName()
//	{
//		return "\"Server\" and \"X-Powered-By\" header recording";
//	}
//
//	@Override
//	public String[] getResponseHeaders()
//	{
//		String[] headers = { "Server", "X-Powered-By" };
//		return headers;
//	}
//
//	@Override
//	public boolean hidden()
//	{
//		return false;
//	}
//
//	@Override
//	public void initialize()
//	{
//		Nikto.initialize();
//	}
//
//	@Override
//	public boolean isExperimental()
//	{
//		return false;
//	}
//
//	@Override
//	public void testByResponseHeader(int transactionID, String responseHeaderName)
//	{
//		if (!Scan.getInstance().getTesterQueue().handlePause_isRunning())
//		{
//			return;
//		}
//		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
//		Nikto.getInstance().addServerHeader(transaction);
//	}
//
//
//}
