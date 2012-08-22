package com.grendelscan.tests.libraries.TokenTesting;

import org.apache.http.Header;

public class HttpHeaderContext extends TokenContext
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Header	contextHeader;

	public HttpHeaderContext(String token, TokenContextType contextType, int outputTransactionID, Header contextHeader, int originatingTransactionID)
	{
		super(token, contextType, outputTransactionID, originatingTransactionID);
		this.contextHeader = contextHeader;
	}

	public Header getContextHeader()
	{
		return contextHeader;
	}

	public void setContextHeader(Header contextHeader)
	{
		this.contextHeader = contextHeader;
	}

}
