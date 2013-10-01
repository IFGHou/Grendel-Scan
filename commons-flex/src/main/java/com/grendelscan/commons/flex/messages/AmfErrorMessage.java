/**
 * 
 */
package com.grendelscan.commons.flex.messages;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfUtils;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

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
    private static final long serialVersionUID = 1L;
    private static final String FAULT_CODE = "faultCode";
    private static final String FAULT_STRING = "faultString";
    private static final String FAULT_DETAIL = "faultDetail";
    private static final String ROOT_CAUSE = "rootCause";
    private static final String EXTENDED_DATA = "extendedData";

    public AmfErrorMessage(final String name, final ErrorMessage message, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, message, AmfDataType.kAmfErrorMessage, ErrorMessage.class.getCanonicalName(), parent, transactionId);
        addFixedField(FAULT_CODE, AmfUtils.parseAmfData(message.faultCode, this, transactionId, true), true);
        addFixedField(FAULT_STRING, AmfUtils.parseAmfData(message.faultString, this, transactionId, true), true);
        addFixedField(FAULT_DETAIL, AmfUtils.parseAmfData(message.faultDetail, this, transactionId, true), true);
        addFixedField(ROOT_CAUSE, AmfUtils.parseAmfData(message.rootCause, this, transactionId, true), true);
        addFixedField(EXTENDED_DATA, AmfUtils.parseAmfData(message.extendedData, this, transactionId, true), true);
    }

}
