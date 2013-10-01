/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

import java.io.Serializable;

/**
 * @author david
 *
 */
public interface DataReference extends Serializable
{
	public DataReference clone();
	public String debugString();
}
