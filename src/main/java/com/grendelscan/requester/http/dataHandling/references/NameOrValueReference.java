/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

/**
 * @author david
 *
 */
public class NameOrValueReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private boolean name;
	public static final NameOrValueReference NAME = new NameOrValueReference(true);
	public static final NameOrValueReference VALUE = new NameOrValueReference(false);
	
	
	
	private NameOrValueReference(boolean name)
	{
		this.name = name;
	}

	public final boolean isName()
	{
		return name;
	}
	
	@Override
	public NameOrValueReference clone()
	{
		return new NameOrValueReference(name);
	}
}
