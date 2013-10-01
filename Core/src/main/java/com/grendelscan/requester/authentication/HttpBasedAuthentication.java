///*
// * AuthenticationBundle.java
// *
// * Created on September 13, 2007, 9:16 PM
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package com.grendelscan.requester.authentication;
//
//
//import java.util.List;
//
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
///**
// *
// * @author Administrator
// */
//public class HttpBasedAuthentication extends AuthenticationPackage
//{
//	private StandardHttpTransaction loginTemplate;
//	public HttpBasedAuthentication()
//    {
//    }
//
//	@Override
//    public StandardHttpTransaction createAnyLoginTransaction()
//    {
//	    // TODO Auto-generated method stub
//	    return null;
//    }
//
//	@Override
//    public StandardHttpTransaction createLoginTransaction(String username)
//    {
//		StandardHttpTransaction transaction = null;
////		if (getLoginTemplate() != null)
////		{
////			transaction = getLoginTemplate().clone(TransactionSource.AUTHENTICATION);
////			transaction.setAuthenticated(true);
//////			transaction.getRequest().
////			scan.getHttpClient().setAuthSchemes(authSchemeRegistry)
////			transaction.setLoginTransaction(true);
////		}
//	    return transaction;
//    }
//	
//	private StandardHttpTransaction getLoginTemplate()
//	{
//		return null;
////		if (loginTemplate == null)
////		{
////			if (Scan.getScanSettings().getReadOnlyBaseURIs().size() > 0)
////			{
////				URI uri = Scan.getScanSettings().getReadOnlyBaseURIs().get(0);
////	            try
////                {
////	                loginTemplate = new StandardHttpTransaction(TransactionSource.AUTHENTICATION);
////	                loginTemplate.getRequestWrapper().setURI(uri);
////	                
////                }
////                catch (URISyntaxException e)
////                {
////    	            Log.error("Odd problem with the uri when creating a login template: " + e.toString(), e);
////                }
////			}
////		}
////		return loginTemplate;
//	}
//
//	@Override
//    public List<StandardHttpTransaction> createLoginTransactions()
//    {
//	    // TODO Auto-generated method stub
//	    return null;
//    }
//    
//    
//    /** Creates a new instance of AuthenticationBundle */
//    
//}
//
//
