/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import org.apache.http.NameValuePair;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;


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
