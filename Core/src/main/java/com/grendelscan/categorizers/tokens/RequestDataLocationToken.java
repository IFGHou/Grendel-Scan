/**
 * 
 */
package com.grendelscan.categorizers.tokens;

import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class RequestDataLocationToken implements Token
{
	private DataReferenceChain chain; 
	public RequestDataLocationToken(DataReferenceChain chain)
	{
		this.chain = chain;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
	 */
	@Override
	public String getTokenHash()
	{
		return StringUtils.md5Hash(chain.toString());
	}

	public DataReferenceChain getChain()
	{
		return chain;
	}

}
