/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.data;

import org.jgroups.util.ExposedByteArrayOutputStream;



/**
 * @author david
 *
 */
public class DataUtils
{
	private DataUtils()
	{
	}

	
	public static byte[] getBytes(Data data) 
	{
		ExposedByteArrayOutputStream out = new ExposedByteArrayOutputStream();
		data.writeBytes(out);
		return out.toByteArray();
	}

}
