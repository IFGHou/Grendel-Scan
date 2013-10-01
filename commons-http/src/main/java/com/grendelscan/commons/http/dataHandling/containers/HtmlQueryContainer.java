/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.containers;

import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;

/**
 * @author david
 *
 */
public interface HtmlQueryContainer<ReferenceType extends NamedDataContainerDataReference> 
	extends DataContainer<ReferenceType>
{
	public void addParameter(String name, String value);
}
