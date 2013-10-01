/**
 * 
 */
package com.grendelscan.categorizers.tokens;

import com.grendelscan.requester.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.utils.StringUtils;

/**
 * @author david
 *
 */
public class CookieToken implements Token
{
	private final SerializableBasicCookie cookie;
	
	public CookieToken(SerializableBasicCookie cookie)
	{
		this.cookie = cookie;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
	 */
	@Override
	public String getTokenHash()
	{
		return StringUtils.md5Hash(cookie.getDomain() + cookie.getPath() + ";" + cookie.getName());
	}

	public final SerializableBasicCookie getCookie()
	{
		return cookie;
	}

}
