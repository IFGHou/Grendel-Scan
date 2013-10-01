package com.grendelscan.smashers.utils.tokens;

public class HtmlContext extends TokenContext
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String[] possibleQuoteCharacters;

    public HtmlContext(final String token, final TokenContextType contextType, final int outputTransactionID, final int originatingTransactionID, final String[] possibleQuoteCharacters)
    {
        super(token, contextType, outputTransactionID, originatingTransactionID);
        this.possibleQuoteCharacters = possibleQuoteCharacters;
    }

    public String[] getPossibleQuoteCharacters()
    {
        return possibleQuoteCharacters;
    }
}
