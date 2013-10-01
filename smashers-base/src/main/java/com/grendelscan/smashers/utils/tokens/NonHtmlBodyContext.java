package com.grendelscan.smashers.utils.tokens;

public class NonHtmlBodyContext extends TokenContext
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String mimeType;

    public NonHtmlBodyContext(final String token, final TokenContextType contextType, final int outputTransactionID, final int originatingTransactionID, final String mimeType)
    {
        super(token, contextType, outputTransactionID, originatingTransactionID);
        this.mimeType = mimeType;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(final String mimeType)
    {
        this.mimeType = mimeType;
    }

}
