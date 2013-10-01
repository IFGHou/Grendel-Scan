/**
 * 
 */
package com.grendelscan.smashers.categorizers;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;

/**
 * @author david
 * 
 */
public class CookieToken implements Token
{
    private final SerializableBasicCookie cookie;

    public CookieToken(final SerializableBasicCookie cookie)
    {
        this.cookie = cookie;
    }

    public final SerializableBasicCookie getCookie()
    {
        return cookie;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.tokens.Token#getTokenHash()
     */
    @Override
    public String getTokenHash()
    {
        return StringUtils.md5Hash(cookie.getDomain() + cookie.getPath() + ";" + cookie.getName());
    }

}
