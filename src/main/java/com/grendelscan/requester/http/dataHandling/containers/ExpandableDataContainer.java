/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;

/**
 * @author david
 *
 */
public interface ExpandableDataContainer<ReferenceType extends DataReference>  extends DataContainer<ReferenceType>
{
	public void addChild(ReferenceType reference, Data child);
	public void addChild(Data child);

}
