/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

/**
 * @author david
 *
 */
public class TransactionDataReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final boolean body;
	
	
	@Override
	public TransactionDataReference clone()
	{
		return new TransactionDataReference(body);
	}


	public TransactionDataReference(boolean body)
	{
		this.body = body;
	}


	public final boolean isBody()
	{
		return body;
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return body ? "Is the body query string" : "Is the URL query string";
	}

}