package com.grendelscan.testing.utils.tokens;

import org.apache.http.Header;

public class HttpHeaderContext extends TokenContext
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private Header contextHeader;

    public HttpHeaderContext(final String token, final TokenContextType contextType, final int outputTransactionID, final Header contextHeader, final int originatingTransactionID)
    {
        super(token, contextType, outputTransactionID, originatingTransactionID);
        this.contextHeader = contextHeader;
    }

    public Header getContextHeader()
    {
        return contextHeader;
    }

    public void setContextHeader(final Header contextHeader)
    {
        this.contextHeader = contextHeader;
    }

}
