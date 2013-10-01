package org.cobra_grendel.html;


/**
 * The user agent context.
 */
public interface UserAgentContext
{
	// /**
	// * Informs context about a warning.
	// */
	// public void warn(String message, Throwable throwable);
	//
	// /**
	// * Informs context about an error.
	// */
	// public void error(String message, Throwable throwable);
	//	
	// /**
	// * Informs context about a warning.
	// */
	// public void warn(String message);
	//
	// /**
	// * Informs context about an error.
	// */
	// public void error(String message);
	//		
	/**
	 * Creates an instance of {@link org.cobra_grendel.html.HttpRequest} which
	 * can be used by the renderer to load images and implement the Javascript
	 * XMLHttpRequest class.
	 */
	public HttpRequest createHttpRequest(int transactionId);
	
	/**
	 * Gets browser "code" name.
	 */
	public String getAppCodeName();
	
	/**
	 * Gets browser application minor version.
	 */
	public String getAppMinorVersion();
	
	/**
	 * Gets browser application name.
	 */
	public String getAppName();
	
	/**
	 * Gets browser application version.
	 */
	public String getAppVersion();
	
	/**
	 * Gets browser language code. See <a
	 * href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1
	 * codes</a>.
	 */
	public String getBrowserLanguage();
	
	/**
	 * Method used to implement document.cookie property.
	 */
	public String getCookie(java.net.URL url);
	
	/**
	 * Gets the name of the user's operating system.
	 */
	public String getPlatform();
	
	/**
	 * Gets the scripting optimization level, which is a value equivalent to
	 * Rhino's optimization level.
	 */
	public int getScriptingOptimizationLevel();
	
	/**
	 * Gets the security policy for scripting. Return <code>null</code> if
	 * JavaScript code is trusted.
	 */
	public java.security.Policy getSecurityPolicy();
	
	/**
	 * Should return the string used in the User-Agent header.
	 */
	public String getUserAgent();
	
	/**
	 * Returns a boolean value indicating whether cookies are enabled in the
	 * user agent.
	 */
	public boolean isCookieEnabled();
	
	/**
	 * Returns a boolean value indicating whether scripting is enabled in the
	 * user agent.
	 */
	public boolean isScriptingEnabled();
	
	/**
	 * Method used to implement document.cookie property.
	 * 
	 * @param cookieSpec
	 *            Specification of cookies, as they would appear in the
	 *            Set-Cookie header value of HTTP.
	 */
	public void setCookie(java.net.URL url, String cookieSpec);
}
