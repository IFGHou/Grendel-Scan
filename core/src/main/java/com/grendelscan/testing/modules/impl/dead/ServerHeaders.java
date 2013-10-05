package com.grendelscan.testing.modules.impl.dead;
//package com.grendelscan.smashers.hidden;
//
//import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.smashers.utils.nikto.Nikto;
//import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
//import com.grendelscan.smashers.AbstractSmasher;
//import com.grendelscan.smashers.types.ByFileTest;
//import com.grendelscan.smashers.types.ByResponseHeaderTest;
//
//public class ServerHeaders extends AbstractSmasher implements ByResponseHeaderTest
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
