/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.data;

import org.jgroups.util.ExposedByteArrayOutputStream;

/**
 * @author david
 * 
 */
public class DataUtils
{
    public static byte[] getBytes(final Data data)
    {
        ExposedByteArrayOutputStream out = new ExposedByteArrayOutputStream();
        data.writeBytes(out);
        return out.toByteArray();
    }

    private DataUtils()
    {
    }

}
