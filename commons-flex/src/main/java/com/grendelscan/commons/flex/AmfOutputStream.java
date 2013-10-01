/**
 * 
 */
package com.grendelscan.commons.flex;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * @author david
 * 
 */
public class AmfOutputStream extends DataOutputStream
{

    private boolean amf3Active;

    /**
     * @param out
     */
    public AmfOutputStream(final OutputStream out)
    {
        super(out);
    }

    public final boolean isAmf3Active()
    {
        return amf3Active;
    }

    public final void setAmf3Active(final boolean amf3Active)
    {
        this.amf3Active = amf3Active;
    }
}
