/**
 * 
 */
package com.grendelscan.commons.flex;


import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.references.DataReference;

/**
 * @author david
 *
 */
public abstract class AbstractAmfDataContainer<ReferenceType extends DataReference> 
	extends AbstractAmfData implements DataContainer<ReferenceType>
{

	private static final long	serialVersionUID	= 1L;


	/**
	 * @param name
	 * @param type
	 * @param parent
	 * @param forceAmf3Code
	 * @param reference
	 * @param transactionId
	 */
	public AbstractAmfDataContainer(String name, AmfDataType type, AbstractAmfDataContainer<?> parent, boolean forceAmf3Code, 
			int transactionId)
	{
		super(name, type, parent, forceAmf3Code, transactionId);
	}

}
