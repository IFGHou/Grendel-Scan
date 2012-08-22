/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.messages;

import java.util.Map;

import flex.messaging.MessageException;
import flex.messaging.log.Log;

/**
 * A message describing a MessageException.
 *
 * @author neville
 * @exclude
 */
public class ErrorMessage extends AcknowledgeMessage
{
    /**
     * This number was generated using the 'serialver' command line tool.
     * This number should remain consistent with the version used by
     * ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = -9069412644250075809L;

    public String faultCode;
    public String faultString;
    public String faultDetail;
    public Object rootCause;
    public Map extendedData;

    public ErrorMessage(MessageException mxe)
    {
        faultCode = mxe.getCode();
        faultString = mxe.getMessage();
        faultDetail = mxe.getDetails();
        if (mxe.getRootCause() != null)
        {
            rootCause = mxe.getRootCauseErrorMessage();
        }
        Map extendedData = mxe.getExtendedData();
        if (extendedData != null)
        {
            this.extendedData = extendedData;
        }
    }

    public ErrorMessage()
    {
    }

    /**
     * @exclude
     */
    @Override public Message getSmallMessage()
    {
        return null;
    }

    @Override protected String toStringFields(int indentLevel) 
    {
        String sep = getFieldSeparator(indentLevel);
        String s = super.toStringFields(indentLevel);
        s += sep + "code =  " + faultCode;
        s += sep + "message =  " + faultString;
        s += sep + "details =  " + faultDetail;
        s += sep + "rootCause =  ";
        if (rootCause == null) s += "null";
        else s += rootCause.toString();
        if (Log.isExcludedProperty("body"))
            s += sep + "body = " + Log.VALUE_SUPRESSED;
        else
            s += sep + "body =  " + bodyToString(body, indentLevel);
        s += sep + "extendedData =  " + bodyToString(extendedData, indentLevel);
        return s;
    }
}
