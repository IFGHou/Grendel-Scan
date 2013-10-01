/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import org.apache.http.NameValuePair;

import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;


/**
 * @author david
 *
 */
public interface NameValuePairDataContainer extends DataContainer<NameOrValueReference>, NameValuePair
{
	
//	public byte[] getNameBytes();
	public void setValue(byte[] value);
	public void setName(byte[] name);
//	public byte[] getValueBytes();
	public Data getValueData();
	public Data getNameData();
}
