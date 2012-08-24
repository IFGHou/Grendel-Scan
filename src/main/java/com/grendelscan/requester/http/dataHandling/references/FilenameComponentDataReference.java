/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

/**
 * @author david
 *
 */
public class FilenameComponentDataReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private boolean name;

	public FilenameComponentDataReference(boolean name)
	{
		this.name = name;
	}

	public final boolean isName()
	{
		return name;
	}
	
	@Override
	public FilenameComponentDataReference clone()
	{
		return new FilenameComponentDataReference(name);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return name ? "Is the file name" : "Is the file extension";
	}

}
