package com.grendelscan.scan;

import java.util.ArrayList;
import java.util.List;


public enum TransactionSource
{
	
    SPIDER("Spider"), ENUMERATION("Enumeration"), MISC_TEST("Misc test"), BASE("Base URL"), 
    COBRA("Cobra"), PROXY("Proxy"), AUTHENTICATION("Authentication"), NIKTO("Nikto"), 
    CATEGORIZER("Categorizer"), AUTOMATIC_RESPONSE_CODE_OVERRIDES("404-detect"), 
    MANUAL_REQUEST("Manual request"), FUZZER("Fuzzer");
    
    private String text;
    private TransactionSource(String text)
    {
    	this.text = text;
    }
    
	public String getText()
    {
    	return text;
    }
	
	public static List<TransactionSource> allSources()
	{
		List<TransactionSource> l = new ArrayList<TransactionSource>();
		for (TransactionSource t: TransactionSource.values())
		{
			l.add(t);
		}
		return l;
	}

}
