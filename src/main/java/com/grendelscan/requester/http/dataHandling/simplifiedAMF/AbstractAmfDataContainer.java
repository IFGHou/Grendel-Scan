/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF;


import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

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
			DataReference reference, int transactionId)
	{
		super(name, type, parent, forceAmf3Code, reference, transactionId);
	}

}
