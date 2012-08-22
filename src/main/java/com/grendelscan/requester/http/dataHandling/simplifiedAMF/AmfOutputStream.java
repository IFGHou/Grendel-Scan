/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * @author david
 *
 */
public class AmfOutputStream extends DataOutputStream
{

	/**
	 * @param out
	 */
	public AmfOutputStream(OutputStream out)
	{
		super(out);
	}
	
	private boolean amf3Active;
	public final boolean isAmf3Active()
	{
		return amf3Active;
	}
	public final void setAmf3Active(boolean amf3Active)
	{
		this.amf3Active = amf3Active;
	}
}
