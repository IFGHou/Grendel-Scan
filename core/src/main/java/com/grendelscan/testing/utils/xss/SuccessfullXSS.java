/**
 * 
 */
package com.grendelscan.testing.utils.xss;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.utils.tokens.TokenContext;

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
