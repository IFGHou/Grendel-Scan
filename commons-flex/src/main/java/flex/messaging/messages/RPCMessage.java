/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.messages;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;

/**
 * Base class for RPC request-styled messages, such as RemotingMessage, HTTPMessage and SOAPMessage.
 * 
 * @exclude
 */
public abstract class RPCMessage extends AbstractMessage
{
    /**
     * This number was generated using the 'serialver' command line tool. This number should remain consistent with the version used by ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = -1203255926746881424L;

    private String remoteUsername;
    private String remotePassword;

    public RPCMessage()
    {
    }

    public String getRemoteUsername()
    {
        return remoteUsername;
    }

    public void setRemoteUsername(String s)
    {
        remoteUsername = s;
    }

    public String getRemotePassword()
    {
        return remotePassword;
    }

    public void setRemotePassword(String s)
    {
        remotePassword = s;
    }

    @Override
    protected String toStringFields(int indentLevel)
    {
        String sp = super.toStringFields(indentLevel);
        String sep = getFieldSeparator(indentLevel);
        String s = sep + "clientId = " + (Log.isExcludedProperty("clientId") ? Log.VALUE_SUPRESSED : clientId);
        s += sep + "destination = " + (Log.isExcludedProperty("destination") ? Log.VALUE_SUPRESSED : destination);
        s += sep + "messageId = " + (Log.isExcludedProperty("messageId") ? Log.VALUE_SUPRESSED : messageId);
        s += sep + "timestamp = " + (Log.isExcludedProperty("timestamp") ? Log.VALUE_SUPRESSED : String.valueOf(timestamp));
        s += sep + "timeToLive = " + (Log.isExcludedProperty("timeToLive") ? Log.VALUE_SUPRESSED : String.valueOf(timeToLive));
        s += sep + "body = " + (Log.isExcludedProperty("clientId") ? Log.VALUE_SUPRESSED : bodyToString(body, indentLevel) + sp);
        return s;
    }

    @Override
    public String logCategory()
    {
        return LogCategories.MESSAGE_RPC;
    }
}
