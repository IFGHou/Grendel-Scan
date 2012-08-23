/**
 * 
 */
package com.grendelscan.utils.dataFormating;

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
	private static final long	serialVersionUID	= 1L;
	public DataFormatType formatType;
	public DataFormatOptions options = new DataFormatOptions();
	
}
