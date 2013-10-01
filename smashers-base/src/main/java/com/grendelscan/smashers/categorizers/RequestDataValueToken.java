/**
 * 
 */
package com.grendelscan.smashers.categorizers;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;

/**
 * @author david
 * 
 */
public class RequestDataValueToken implements Token
{
    private final DataReferenceChain chain;
    private final Data datum;

    public RequestDataValueToken(final Data datum)
    {
        chain = DataContainerUtils.getReferenceChain(datum);
        this.datum = datum;
    }

    public final Data getDatum()
    {
        return datum;
    }

    // public DataReferenceChain getChain()
    // {
    // return chain;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
     */
    @Override
    public String getTokenHash()
    {
        return StringUtils.md5Hash(chain.toString());
    }

}
