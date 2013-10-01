/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.containers;

import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;

/**
 * @author david
 *
 */
public interface HtmlQueryContainer<ReferenceType extends NamedDataContainerDataReference> 
	extends DataContainer<ReferenceType>
{
	public void addParameter(String name, String value);
}
