package com.grendelscan.tests.libraries.TokenTesting;

public class HtmlContext extends TokenContext
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String[]	possibleQuoteCharacters;

	public HtmlContext(String token, TokenContextType contextType, int outputTransactionID, int originatingTransactionID,
			String[] possibleQuoteCharacters)
	{
		super(token, contextType, outputTransactionID, originatingTransactionID);
		this.possibleQuoteCharacters = possibleQuoteCharacters;
	}

	public String[] getPossibleQuoteCharacters()
	{
		return possibleQuoteCharacters;
	}
}
