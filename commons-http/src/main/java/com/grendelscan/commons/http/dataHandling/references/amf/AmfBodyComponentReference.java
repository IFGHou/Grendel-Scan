/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references.amf;

import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AmfPrimitiveData;
import com.grendelscan.commons.http.dataHandling.references.DataReference;

/**
 * @author david
 *
 */
public class AmfBodyComponentReference implements DataReference
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfBodyComponentReference.class);
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
	 * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Location: " + location.toString();
	}

}
