/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

/**
 * @author david
 *
 */
public class UrlPathDataReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final boolean directory;
	
	public UrlPathDataReference(boolean directory)
	{
		this.directory = directory;
	}
	
	@Override public UrlPathDataReference clone()
	{
		return new UrlPathDataReference(directory);
	}

	public final boolean isDirectory()
	{
		return directory;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return directory ? "Is the directory component" : "Is the filename component";
	}
}
