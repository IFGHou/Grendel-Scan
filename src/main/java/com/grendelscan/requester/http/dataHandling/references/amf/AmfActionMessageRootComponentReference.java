/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references.amf;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.amf.AmfBodyComponentReference.BodyLocation;

/**
 * @author david
 *
 */
public class AmfActionMessageRootComponentReference implements DataReference
{
	private static final long	serialVersionUID	= 1L;

	public enum RootLocation {BODIES, HEADERS}

	private RootLocation location;
	
	public final static AmfActionMessageRootComponentReference BODIES = new AmfActionMessageRootComponentReference(RootLocation.BODIES);
	public final static AmfActionMessageRootComponentReference HEADERS = new AmfActionMessageRootComponentReference(RootLocation.HEADERS);
	
	private AmfActionMessageRootComponentReference(RootLocation location)
	{
		this.location = location;
	}
	
	@Override public AmfActionMessageRootComponentReference clone()
	{
		return this;
	}

	public boolean isBodies()
	{
		return location.equals(RootLocation.BODIES);
	}

	public boolean isHeaders()
	{
		return location.equals(RootLocation.HEADERS);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Location is " + location.toString();
	}
}
