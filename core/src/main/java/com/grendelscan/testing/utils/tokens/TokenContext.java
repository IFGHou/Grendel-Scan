package com.grendelscan.testing.utils.tokens;

import java.io.Serializable;

import com.grendelscan.commons.http.dataHandling.data.Data;

public abstract class TokenContext implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected TokenContextType contextType;
    protected final Data requestDatum;
    protected final int originatingTransactionID;
    protected final int outputTransactionID;
    protected final String token;

    public TokenContext(final String token, final TokenContextType contextType, final int outputTransactionID, final int originatingTransactionID)
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

    public final int getOriginatingTransactionID()
    {
        return originatingTransactionID;
    }

    public int getOutputTransactionID()
    {
        return outputTransactionID;
    }

    public final Data getRequestDatum()
    {
        return requestDatum;
    }

    public String getToken()
    {
        return token;
    }

    public boolean isSingleTransaction()
    {
        return originatingTransactionID == outputTransactionID;
    }

}
