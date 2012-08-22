package com.grendelscan.requester.authentication;

public enum HttpAuthenticationType
{
	BASIC("BASIC"), DIGEST("DIGEST"), NTLM("NTLM");
	
	private String value;

	private HttpAuthenticationType(String value)
	{
		this.value = value;
	}

	public String getValue()
    {
    	return value;
    }
}
