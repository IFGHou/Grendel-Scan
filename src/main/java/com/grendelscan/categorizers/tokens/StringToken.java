/**
 * 
 */
package com.grendelscan.categorizers.tokens;

import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class StringToken implements Token
{

	private final String token;
	
	public StringToken(String token)
	{
		this.token = token;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.tokens.Token#getTokenString()
	 */
	@Override
	public String getTokenHash()
	{
		return StringUtils.md5Hash(token);
	}

	public final String getToken()
	{
		return token;
	}

}
