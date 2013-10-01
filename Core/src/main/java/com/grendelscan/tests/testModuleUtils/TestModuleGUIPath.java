package com.grendelscan.tests.testModuleUtils;

public enum TestModuleGUIPath
{
	ARCHITECTURE("Application architecture"),
	FILE_ENUMERATION("File enumeration"),
	HIDDEN("Hidden"),
	INFORMATION_LEAKAGE("Information leakage"),
	MISCELLANEOUS_ATTACKS("Miscellaneous attacks"),
	NIKTO("Nikto"),
	SESSION_MANAGEMENT("Session management"),
	SPIDER("Spider"),
	SQL_INJECTION("SQL injection"),
	WEB_SERVER_CONFIGURATION("Web server configuration"),
	XSS("XSS");

	private String	text;

	private TestModuleGUIPath(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}
}
