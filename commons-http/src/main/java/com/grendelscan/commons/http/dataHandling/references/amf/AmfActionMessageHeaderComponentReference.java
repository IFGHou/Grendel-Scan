/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references.amf;

import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.amf.AmfBodyComponentReference.BodyLocation;

/**
 * @author david
 *
 */
public class AmfActionMessageHeaderComponentReference implements DataReference
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfActionMessageHeaderComponentReference.class);
	private static final long	serialVersionUID	= 1L;

	public enum HeaderLocation {DATA, MUST_UNDERSTAND}

	private HeaderLocation location;
	
	public final static AmfActionMessageHeaderComponentReference DATA = new AmfActionMessageHeaderComponentReference(HeaderLocation.DATA);
	public final static AmfActionMessageHeaderComponentReference MUST_UNDERSTAND = new AmfActionMessageHeaderComponentReference(HeaderLocation.MUST_UNDERSTAND);
	
	private AmfActionMessageHeaderComponentReference(HeaderLocation location)
	{
		this.location = location;
	}
	
	@Override public AmfActionMessageHeaderComponentReference clone()
	{
		return this;
	}

	public boolean isData()
	{
		return location.equals(HeaderLocation.DATA);
	}

	public boolean isMustUnderstand()
	{
		return location.equals(HeaderLocation.MUST_UNDERSTAND);
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Location is " + location.toString();
	}

}
