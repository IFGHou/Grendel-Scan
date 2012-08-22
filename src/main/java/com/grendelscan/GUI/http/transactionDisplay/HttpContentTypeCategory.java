package com.grendelscan.GUI.http.transactionDisplay;

import com.grendelscan.utils.MimeUtils;

public enum HttpContentTypeCategory 
{
	IMAGE, HTML, OTHER, URL_ENCODED, AMF;
	
	
	
	public static HttpContentTypeCategory getContentType(String contentType)
	{
		if (contentType == null || contentType.equals(""))
			return OTHER;
		if (MimeUtils.isHtmlMimeType(contentType))
			return HTML;
		if (MimeUtils.isImageMimeType(contentType))
			return IMAGE;
		if (contentType.equalsIgnoreCase("application/x-amf"))
			return AMF;
		if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded"))
			return URL_ENCODED;
		return OTHER;
	}

}
