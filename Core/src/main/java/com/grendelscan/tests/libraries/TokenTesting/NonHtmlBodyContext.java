package com.grendelscan.tests.libraries.TokenTesting;

public class NonHtmlBodyContext extends TokenContext
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String	mimeType;

	public NonHtmlBodyContext(String token, TokenContextType contextType, int outputTransactionID, int originatingTransactionID, String mimeType)
	{
		super(token, contextType, outputTransactionID, originatingTransactionID);
		this.mimeType = mimeType;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

}
