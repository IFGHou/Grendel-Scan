/**
 * 
 */
package com.grendelscan.requester.http;

/**
 * @author david
 *
 */
public class HttpConstants
{
	private HttpConstants()
	{
		
	}
	
	public static final String ENCODING_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String ENCODING_MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String ENCODING_TEXT_PLAIN = "text/plain";
	public static final byte[] CRLF_BYTES = new byte[] {'\r', '\n'};
}
