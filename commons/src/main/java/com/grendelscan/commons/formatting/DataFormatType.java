/**
 * 
 */
package com.grendelscan.commons.formatting;
import java.io.Serializable;
import java.util.*;
/**
 * @author david
 *
 */
public enum DataFormatType  implements Serializable
{
	
	HEX_UPPER_SIMPLE("Simple uppercase hexadecimal", "", "0F3DFA83", true, false), 
	HEX_LOWER_SIMPLE("Simple lowercase hexadecimal", "", "0f3dfa83", true, false), 
	HEX_LOWER_WITH_0X_PREFIX("lowercase hexadecimal starting with 0x", "", "0x0f3dfa83", true, false), 
	HEX_UPPER_WITH_0X_PREFIX("uppercase hexadecimal starting with 0x", "", "0x0F3DFA83", true, false), 
	HEX_LOWER_SLASH_X("", "", "\\x0f\\x3d\\xfa\\x83", true, false), 
	HEX_UPPER_SLASH_X("", "", "\\x0F\\x3D\\xFA\\x83", true, false),

	INTEGER("Integer", "Whole number between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE, "123456", false, false), 
	NUMERIC("Numeric", "Series of decimal numerals that can exceed integer ranges", "11111111111111111111111111111123456", false, false), 
	FLOAT("Float", "Double precision floating point", "3.1459", false, false),
	
	ALPHANUMERIC("Alphanumeric", "Numerals and letters, any case, nothing else (e.g., no whitespace)", "asdf1234", false, false),
	
	ASCII_TEXT("ASCII text", "\\x09,\\x0a,\\x0d,\\x20-\\x7e", "Lorem ipsum dolor sit amet, consectetur adipisicing elit", false, false), 
	ASCII_BINARY("Binary ASCII", "Any ASCII character. No restrictions.", "", false, false),
	
	
	HTML_BASIC_ENTITIES("", "", "", true, false), 
	HTML_FULL_ENTITIES("", "", "", true, false),
	
	URL_ENCODED_QUERY_STRING("", "", "", false, true),
	URL_BASIC_ENCODED("", "", "", true, false), 
	URL_FULL_ENCODED("", "", "", true, false), 
	
	BASE64("", "", "", true, false), 
	BASE64_PRETTY("", "", "", true, false), 
	BASE64_WEB("", "", "", true, false),
	
	AMF("Action Message Format", "", "", false, true)
	; 
	
//	SERIALIZED_JAVA_OBJECT("Serialized Java object", "", "", false),
//
//	JSON("", "", "", false), 
//	
//	XML("", "", "", false), 
//	
//	AMF("", "", "", false), 
//	
//	BZIP2("", "", "", true), 
//	GZIP("", "", "", true), 
//	DEFLATE("", "", "", true), 
//	COMPRESS("", "", "", true), 
//	PACK200("", "", "", true), 
//	
//	UTF8("", "", "", true), 
//	
//	SDCH("", "", "", true);
	
	
	public DataFormatType[] getEncodeableTypes()
	{
		List<DataFormatType> l = new ArrayList<DataFormatType>();
		for(DataFormatType format: DataFormatType.values())
		{
			if (format.isEncodeable())
				l.add(format);
		}
		return l.toArray(new DataFormatType[0]);
	}
	
	
	private DataFormatType(String name, String description, String example, boolean encodeable, boolean container)
	{
		this.encodeable = encodeable;
		this.name = name;
		this.description = description;
		this.example = example;
		this.container = container;
	}
	
	private boolean encodeable, container;
	private String name, description, example;
	public final boolean isEncodeable()
	{
		return encodeable;
	}
	public final String getName()
	{
		return name;
	}
	public final String getDescription()
	{
		return description;
	}
	public final String getExample()
	{
		return example;
	}


	public final boolean isContainer()
	{
		return container;
	}
}
