/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * @author david
 * 
 */
public class DataReferenceChain extends ArrayList<DataReference>
{

    private static final long serialVersionUID = 1L;

    @Override
    public String toString()
    {
        StringWriter out = new StringWriter();
        boolean first = true;
        for (DataReference ref : this)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                out.write("->");
            }
            out.write(ref.debugString());
        }
        return out.toString();
    }

}
