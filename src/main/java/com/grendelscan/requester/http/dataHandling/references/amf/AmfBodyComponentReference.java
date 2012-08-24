/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.references.amf;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;

/**
 * @author david
 *
 */
public class AmfBodyComponentReference implements DataReference
{
	private static final long	serialVersionUID	= 1L;

	public enum BodyLocation {_TARGET_URI, _RESPONSE_URI, _BODY_DATA}

	private BodyLocation location;
	
	public final static AmfBodyComponentReference TARGET_URI = new AmfBodyComponentReference(BodyLocation._TARGET_URI);
	public final static AmfBodyComponentReference RESPONSE_URI = new AmfBodyComponentReference(BodyLocation._RESPONSE_URI);
	public final static AmfBodyComponentReference BODY_DATA = new AmfBodyComponentReference(BodyLocation._BODY_DATA);
	
	private AmfBodyComponentReference(BodyLocation location)
	{
		this.location = location;
	}
	
	@Override public AmfBodyComponentReference clone()
	{
		return this;
	}

	public final BodyLocation getLocation()
	{
		return location;
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Location: " + location.toString();
	}

}
