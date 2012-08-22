/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import java.util.*;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReference;

/**
 * @author david
 *
 */
public interface DataContainer<ReferenceType extends DataReference> extends Data
{
	public Data[] getDataChildren();
	public void removeChild(Data child);
	public Data getChild(ReferenceType reference);
	public Data getChildUnsafeType(DataReference reference);
	public void replaceChild(ReferenceType reference, Data child);
}
