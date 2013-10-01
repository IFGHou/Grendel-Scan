package org.cobra_grendel.html.test;

import java.security.Policy;

import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.UserAgentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.cobra.CobraHttpRequest;

/**
 * Simple implementation of {@link org.cobra_grendel.html.UserAgentContext}.
 */
public class SimpleUserAgentContext implements UserAgentContext
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserAgentContext.class);

    /**
     * Creates a {@link org.cobra_grendel.html.test.SimpleHttpRequest} instance. Override if a custom mechanism to make requests is needed.
     */
    @Override
    public HttpRequest createHttpRequest(final int referingTransactionId)
    {
        throw new IllegalStateException("This needs to be fixed");
        // FIXME: This creates a circular dependency. - Jonathan Byrne 10/01/2013
        return new CobraHttpRequest(referingTransactionId, -1);
    }

    public void error(final String message)
    {
        LOGGER.error(message);
    }

    public void error(final String message, final Throwable throwable)
    {
        LOGGER.error(message, throwable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getAppCodeName()
     */
    @Override
    public String getAppCodeName()
    {
        return "Cobra";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getAppMinorVersion()
     */
    @Override
    public String getAppMinorVersion()
    {
        return "0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getAppName()
     */
    @Override
    public String getAppName()
    {
        return "Browser";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getAppVersion()
     */
    @Override
    public String getAppVersion()
    {
        return "1";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getBrowserLanguage()
     */
    @Override
    public String getBrowserLanguage()
    {
        return "EN";
    }

    @Override
    public String getCookie(final java.net.URL url)
    {
        this.warn("getCookie(): Method not overridden; returning null.");
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getPlatform()
     */
    @Override
    public String getPlatform()
    {
        return System.getProperty("os.name");
    }

    /**
     * Returns -1. Override to provide a different Rhino optimization level.
     */
    @Override
    public int getScriptingOptimizationLevel()
    {
        return -1;
    }

    /**
     * Returns <code>null</code>. This method must be overridden if JavaScript code is untrusted.
     */
    @Override
    public Policy getSecurityPolicy()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#getUserAgent()
     */
    @Override
    public String getUserAgent()
    {
        return "Mozilla/4.0 (compatible; MSIE 6.0;) Cobra/Simple";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlParserContext#isCookieEnabled()
     */
    @Override
    public boolean isCookieEnabled()
    {
        this.warn("isCookieEnabled(): Not overridden - returning false");
        return false;
    }

    /**
     * Returns <code>true</code>. Implementations wishing to disable JavaScript may override this method.
     */
    @Override
    public boolean isScriptingEnabled()
    {
        return true;
    }

    @Override
    public void setCookie(final java.net.URL url, final String cookieSpec)
    {
        this.warn("setCookie(): Method not overridden.");
    }

    public void warn(final String message)
    {
        LOGGER.warn(message);
    }

    public void warn(final String message, final Throwable throwable)
    {
        LOGGER.warn(message, throwable);
    }
}
