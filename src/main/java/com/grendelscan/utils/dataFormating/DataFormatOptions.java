/**
 * 
 */
package com.grendelscan.utils.dataFormating;

import java.io.Serializable;

/**
 * @author david
 *
 */
public class DataFormatOptions implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public int BASE64_LINE_LENGTH = 76;
	public byte[] BASE64_LINE_DELIMITER = new byte[]{0x0D, 0x0A};
	public byte[] LIST_DELIMITER = new byte[]{','};
}
