package com.grendelscan.GUI.proxy.interception;

public enum InterceptFilterLocation  
{
	METHOD("Method"), URL("URL"), SCHEME("Scheme"), HOST("Host"), PATH("Path and filename"), 
	QUERY("URL Query"), COOKIE_HEADER("Cookie header"), REQUEST_HEADER_NAME("Request header name"), 
	REQUEST_HEADER_VALUE("Request header value"),
	REQUEST_BODY("Request body"),
	
	RESPONSE_CODE("Response code"), RESPONSE_BODY("Response body"), 
	RESPONSE_HEADER_NAME("Response header name"), RESPONSE_HEADER_VALUE("Response header value"),
	RESPONSE_MIME_TYPE("Response MIME type"), FUZZY_RESPONSE_COMPARE("Fuzzy response match");
	
	private String text;
	
	private InterceptFilterLocation(String text)
    {
	    this.text = text;
    }

	public String getText()
    {
    	return text;
    }
	
	public static InterceptFilterLocation[] getRequestLocations()
	{
		return new InterceptFilterLocation[] {
				METHOD,
				URL,
				SCHEME,
				HOST,
				PATH,
				QUERY,
				COOKIE_HEADER,
				REQUEST_HEADER_VALUE,
				REQUEST_BODY,
				REQUEST_HEADER_NAME
				};
	}

	public static InterceptFilterLocation[] getResponseLocations()
	{
		return new InterceptFilterLocation[] {
				RESPONSE_CODE,
				RESPONSE_BODY,
				RESPONSE_HEADER_NAME,
				RESPONSE_HEADER_VALUE,
				RESPONSE_MIME_TYPE,
				FUZZY_RESPONSE_COMPARE
				};
	}
	
	public static InterceptFilterLocation[] getAllLocations()
	{
		return new InterceptFilterLocation[] {
				METHOD,
				URL,
				SCHEME,
				HOST,
				PATH,
				QUERY,
				COOKIE_HEADER,
				REQUEST_HEADER_VALUE,
				REQUEST_BODY,
				REQUEST_HEADER_NAME,
				
				RESPONSE_CODE,
				REQUEST_BODY,
				RESPONSE_HEADER_NAME,
				RESPONSE_HEADER_VALUE,
				RESPONSE_MIME_TYPE,
				FUZZY_RESPONSE_COMPARE
				};
		
	}

}
