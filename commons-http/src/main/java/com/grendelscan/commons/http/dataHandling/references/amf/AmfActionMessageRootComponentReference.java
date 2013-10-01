/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references.amf;

import com.grendelscan.commons.http.dataHandling.references.DataReference;

/**
 * @author david
 * 
 */
public class AmfActionMessageRootComponentReference implements DataReference
{
    public enum RootLocation
    {
        BODIES, HEADERS
    }

    private static final long serialVersionUID = 1L;

    private final RootLocation location;

    public final static AmfActionMessageRootComponentReference BODIES = new AmfActionMessageRootComponentReference(RootLocation.BODIES);
    public final static AmfActionMessageRootComponentReference HEADERS = new AmfActionMessageRootComponentReference(RootLocation.HEADERS);

    private AmfActionMessageRootComponentReference(final RootLocation location)
    {
        this.location = location;
    }

    @Override
    public AmfActionMessageRootComponentReference clone()
    {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return "Location is " + location.toString();
    }

    public boolean isBodies()
    {
        return location.equals(RootLocation.BODIES);
    }

    public boolean isHeaders()
    {
        return location.equals(RootLocation.HEADERS);
    }
}
