package com.grendelscan.testing.utils.tokens;

public enum TokenContextType
{
	CSS("CSS document"),
	HTML_COMMENT("HTML comment"),
	HTML_EVENT_HANDLER("HTML event handler"),
	HTML_OTHER("Other HTML context"),
	HTML_PRE("HTML PRE contents"),
	HTML_SCRIPT("HTML script body"),
	HTML_STYLE("HTML style definition"),
	HTML_TAG_ATTRIBUTE_NAME("HTML tag attribute name"),
	HTML_TAG_ATTRIBUTE_VALUE("HTML tag attribute value"),
	HTML_TAG_NAME("HTML tag name"),
	HTML_TEXT("HTML text element"),
	HTML_TEXTAREA("HTML textarea value"),
	HTML_TITLE("HTML document title"),
	HTTP_HEADER_NAME("HTTP header name"),
	HTTP_HEADER_VALUE("HTTP header value"),
	JAVASCRIPT_NON_HTML("JavaScript file"),
	OTHER_NON_HTML("Non-HTML file"),
	TEXT("Text document"),
	XML("XML document");

	private final String	description;

	TokenContextType(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}
}
