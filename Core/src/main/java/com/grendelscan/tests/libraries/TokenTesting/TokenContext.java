package com.grendelscan.tests.libraries.TokenTesting;

import java.io.Serializable;

import com.grendelscan.requester.http.dataHandling.data.Data;

public abstract class TokenContext implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected TokenContextType			contextType;
	protected final Data	requestDatum;
	protected final int						originatingTransactionID;
	protected final int						outputTransactionID;
	protected final String					token;

	public TokenContext(String token, TokenContextType contextType, int outputTransactionID, int originatingTransactionID)
	{
		this.token = token;
		requestDatum = TokenTesting.getInstance().getTokenTest(token);
		this.contextType = contextType;
		this.outputTransactionID = outputTransactionID;
		this.originatingTransactionID = originatingTransactionID;
	}

	public TokenContextType getContextType()
	{
		return contextType;
	}

	public boolean isSingleTransaction()
	{
		return originatingTransactionID == outputTransactionID;
	}
	public int getOutputTransactionID()
	{
		return outputTransactionID;
	}


	public String getToken()
	{
		return token;
	}

	public final int getOriginatingTransactionID()
	{
		return originatingTransactionID;
	}

	public final Data getRequestDatum()
	{
		return requestDatum;
	}

}
