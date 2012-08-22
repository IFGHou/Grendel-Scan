/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;


import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.amf.AmfBodyComponentReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.utils.AmfUtils;

import flex.messaging.messages.ErrorMessage;

/**
 * @author david
 *
 */
public class AmfErrorMessage extends AmfAcknowledgeMessage
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static final String FAULT_CODE = "faultCode";
	private static final String FAULT_STRING = "faultString";
	private static final String FAULT_DETAIL = "faultDetail";
	private static final String ROOT_CAUSE = "rootCause";
	private static final String EXTENDED_DATA = "extendedData";


	public AmfErrorMessage(String name, ErrorMessage message, AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
	{
		super(name, message, AmfDataType.kAmfErrorMessage, ErrorMessage.class.getCanonicalName(), parent, reference, transactionId);
		addFixedField(FAULT_CODE, AmfUtils.parseAmfData(message.faultCode, this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(FAULT_STRING, AmfUtils.parseAmfData(message.faultString, this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(FAULT_DETAIL, AmfUtils.parseAmfData(message.faultDetail, this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(ROOT_CAUSE, AmfUtils.parseAmfData(message.rootCause, this, NameOrValueReference.VALUE, transactionId), true);
		addFixedField(EXTENDED_DATA, AmfUtils.parseAmfData(message.extendedData, this, NameOrValueReference.VALUE, transactionId), true);
	}
	
	

}
