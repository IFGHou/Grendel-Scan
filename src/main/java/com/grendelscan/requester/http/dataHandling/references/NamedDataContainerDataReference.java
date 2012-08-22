/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references;

import java.util.Arrays;

/**
 * @author david
 *
 */
public class NamedDataContainerDataReference implements DataReference
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private byte[] name;

	public NamedDataContainerDataReference(byte[] name)
	{
		this.name = name;
	}
	
	@Override
	public NamedDataContainerDataReference clone()
	{
		return new NamedDataContainerDataReference(Arrays.copyOf(name, name.length));
	}

	public final byte[] getName()
	{
		return name;
	}
}
