package com.grendelscan.testing.modules.impl.dead;

// package com.grendelscan.smashers;
// import java.net.URISyntaxException;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import com.grendelscan.requester.TransactionSource;
// import com.grendelscan.requester.authentication.AuthenticationPackage;
// import com.grendelscan.commons.http.factories.HttpTransactionFactory;
// import com.grendelscan.commons.http.transactions.AbstractHttpTransaction;
// import com.grendelscan.smashers.types.ByAuthenticationPackageTest;
// import com.grendelscan.commons.Debug;
// import com.grendelscan.commons.HttpTransactionUtils;
// import com.grendelscan.commons.StringUtils;
//
// public class Module0023 extends AbstractSmasher implements
// ByAuthenticationPackageTest
// {
// private int loggedOutCompareThreshold = 85;
// @Override
// public String getDescription()
// {
// return "If logged out detection is enabled, builds a profile " +
// "of what a logged out session looks like.";
// }
//
// @Override
// public TestModuleGUIPath getGUIDisplayPath()
// {
// return TestModuleGUIPath.HIDDEN;
// }
//
// @Override
// public int getModuleNumber()
// {
// return 23;
// }
//
// @Override
// public String getName()
// {
// return "Logged out profile builder";
// }
//
// @Override
// public void initialize()
// {
// }
//
// public void testAuthenticationPackage(AuthenticationPackage
// authenticationPackage)
// {
// if (scan.getScanSettings().isUseLoggedOutDetection())
// {
// getErrorPageText(authenticationPackage);
// }
// }
//
//
// private void getErrorPageText(AuthenticationPackage authenticationPackage)
// {
// AbstractHttpTransaction loginTransaction =
// authenticationPackage.createAnyLoginTransaction();
// loginTransaction.execute("Module 23", false);
// for (String stateNames: loginTransaction.getSessionStates().keySet())
// {
// authenticationPackage.getSessionIDNames().add(stateNames);
// }
//
// String text = null;
// String logoutUri = scan.getScanSettings().getLogoutUri();
// if (logoutUri != null && !logoutUri.equals(""))
// {
// try
// {
// AbstractHttpTransaction nonAuthTransaction =
// HttpTransactionFactory.createTransaction(scan, "GET", logoutUri,
// loginTransaction.getId(), TransactionSource.AUTHENTICATION);
// nonAuthTransaction.execute("Module 23", false);
// text = nonAuthTransaction.getStrippedResponseText();
// }
// catch (URISyntaxException e)
// {
// Debug.errDebug("Problem with logout URI: " + e.toString(), e);
// }
// }
//
// if (text == null && loginTransaction.getRedirectRequestTransactionID() > 0)
// {
// AbstractHttpTransaction nonAuthTransaction =
// loginTransaction.getReferer().clone(TransactionSource.AUTHENTICATION);
// nonAuthTransaction.removeAllAuthentication();
// nonAuthTransaction.execute("Module 23", false);
// text = nonAuthTransaction.getStrippedResponseText();
// }
//
// if (text == null)
// {
// Pattern p = Pattern.compile("href\\s*=\\s*['\"]?((?:/|" +
// loginURIStringUtils.getHostUri(transaction.getRequestWrapper().getURI()) +
// ")[\\s'\"]+)", Pattern.CASE_INSENSITIVE);
// Matcher m =
// p.matcher(logintransaction.getResponseWrapper().getBodyAsString());
// while (m.find())
// {
// text = checkLoggedOutSuitibility(loginTransaction, m.group(1));
// if (text != null || !scan.isRunning())
// {
// break;
// }
// }
// }
// authenticationPackage.setLoggedOutPageText(text);
// }
//
// private String checkLoggedOutSuitibility(AbstractHttpTransaction
// referingTransaction, String uri)
// {
// String text = null;
// try
// {
// // Get two samples of the authenticated request
// AbstractHttpTransaction testAuthTransaction1 =
// HttpTransactionFactory.createTransaction(scan, "GET", uri,
// referingTransaction.getId(), TransactionSource.AUTHENTICATION);
// testAuthTransaction1.execute("Module 23", false);
// if (!scan.isRunning())
// {
// return text;
// }
// AbstractHttpTransaction testAuthTransaction2 =
// HttpTransactionFactory.createTransaction(scan, "GET", uri,
// referingTransaction.getId(), TransactionSource.AUTHENTICATION);
// testAuthTransaction2.execute("Module 23", false);
// if (!scan.isRunning())
// {
// return text;
// }
//
// // If there is page stability...
// // if
// (StringUtils.scoreStringDifferenceIgnoreCase(testAuthTransaction1.getStrippedResponseText(),
// testAuthTransaction2.getStrippedResponseText(), 100) >
// loggedOutCompareThreshold)
// if (HttpTransactionUtils.scoreResponseMatch(testAuthTransaction1,
// testAuthTransaction2, 100) > loggedOutCompareThreshold)
// {
// // Get the same url, but without the cookies, etc
// AbstractHttpTransaction testNonAuthTransaction =
// HttpTransactionFactory.createTransaction(scan, "GET", uri, 0,
// TransactionSource.AUTHENTICATION);
// // Get rid of any session states. This should only be params, since cookies
// won't propigate without a referer
// testNonAuthTransaction.removeAllAuthentication();
// testNonAuthTransaction.execute("Module 23", false);
//
// /*
// * If the two transactions are significantly different, assume that one is an
// error
// * message because of no session ID provided.
// */
//
// // if
// (StringUtils.scoreStringDifferenceIgnoreCase(testAuthTransaction1.getStrippedResponseText(),
// testNonAuthTransaction.getStrippedResponseText(), 100) <
// loggedOutCompareThreshold)
// if (HttpTransactionUtils.scoreResponseMatch(testAuthTransaction1,
// testNonAuthTransaction, 100) < loggedOutCompareThreshold)
// {
// text = testNonAuthTransaction.getStrippedResponseText();
// }
// }
// }
// catch (URISyntaxException e)
// {
// // An invalid URI was provided. Just ignore it and continue
// }
//
// return text;
// }
//
// @Override
// public boolean alwaysEnabled()
// {
// return true;
// }
//
// @Override
// public boolean hidden()
// {
// return true;
// }
//
// @Override
// public boolean isExperimental()
// {
// return true;
// }
//
//
// }
