/**
 * 
 */
package com.grendelscan.commons.formatting;

import java.io.Serializable;

/**
 * @author david
 * 
 */
public class DataFormat implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public DataFormatType formatType;
    public DataFormatOptions options = new DataFormatOptions();

}
