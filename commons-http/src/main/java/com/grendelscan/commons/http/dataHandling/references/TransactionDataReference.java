/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author david
 * 
 */
public class TransactionDataReference implements DataReference
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final boolean body;
    public static final TransactionDataReference REQUEST_BODY = new TransactionDataReference(true);
    public static final TransactionDataReference URL_QUERY = new TransactionDataReference(false);

    private TransactionDataReference(final boolean body)
    {
        this.body = body;
    }

    @Override
    public TransactionDataReference clone()
    {
        return new TransactionDataReference(body);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return body ? "Is the body query string" : "Is the URL query string";
    }

    public final boolean isBody()
    {
        return body;
    }

}
