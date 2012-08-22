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
public class RequestDataToken implements Token
{
	private DataReferenceChain chain; 
	private Data datum;
	public RequestDataToken(Data datum)
	{
		chain = DataContainerUtils.getReferenceChain(datum);
		this.datum = datum;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
	 */
	@Override
	public String getTokenHash()
	{
		return StringUtils.md5Hash(chain.toString());
	}

	public final Data getDatum()
	{
		return datum;
	}

}
