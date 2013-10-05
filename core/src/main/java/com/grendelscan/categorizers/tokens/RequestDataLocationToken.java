/**
 * 
 */
package com.grendelscan.categorizers.tokens;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;

/**
 * @author david
 * 
 */
public class RequestDataLocationToken implements Token
{
    private final DataReferenceChain chain;

    public RequestDataLocationToken(final DataReferenceChain chain)
    {
        this.chain = chain;
    }

    public DataReferenceChain getChain()
    {
        return chain;
    }

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
