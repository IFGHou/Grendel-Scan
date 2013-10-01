/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

/**
 * @author david
 *
 */
public class NumberedListDataReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private int index;

	public NumberedListDataReference(int index)
	{
		this.index = index;
	}
	
	@Override public NumberedListDataReference clone()
	{
		return this;
	}

	public int getIndex()
	{
		return index;
	}

	public final void setIndex(int index)
	{
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Index #" + index;
	}

}
