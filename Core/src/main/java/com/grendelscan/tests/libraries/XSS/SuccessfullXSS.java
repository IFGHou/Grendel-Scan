/**
 * 
 */
package com.grendelscan.tests.libraries.XSS;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;

/**
 * @author david
 *
 */
public class SuccessfullXSS
{
	public StandardHttpTransaction transaction;
	public TokenContext context;
	public String attackString;
	public String token;
}
