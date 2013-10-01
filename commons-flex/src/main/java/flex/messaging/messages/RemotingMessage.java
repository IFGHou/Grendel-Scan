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

import java.util.Arrays;
import java.util.List;

/**
 * This type of message contains information needed to perform a Remoting invocation. Some of this information mirrors that of the gateway's ActionContext, but the context itself cannot be used as the
 * message, because it is available only from the AMF Endpoint and not to other endpoints (the RTMP Endpoint has no HTTP, and therefore cannot support the request/response and session properties of
 * the ActionContext).
 * 
 * @author neville
 * @exclude
 */
public class RemotingMessage extends RPCMessage
{
    /**
     * This number was generated using the 'serialver' command line tool. This number should remain consistent with the version used by ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = 1491092800943415719L;

    private String source;
    private String operation;
    private Object[] parameters;
    private transient List parameterList;

    public RemotingMessage()
    {
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String s)
    {
        source = s;
    }

    @Override
    public Object getBody()
    {
        return parameters;
    }

    @Override
    public void setBody(Object bodyValue)
    {
        if (bodyValue instanceof List)
        {
            // some channels/endpoints may send in a list
            // and expect to keep a reference to it - amfx
            // for example works this way, so keep the list
            // around rather than making an array copy
            if (parameterList != null)
            {
                parameterList.addAll((List) bodyValue);
            }
            else
            {
                parameterList = (List) bodyValue;
            }
        }
        else if (!bodyValue.getClass().isArray())
        {
            parameters = new Object[] { bodyValue };
        }
        else
        {
            parameters = (Object[]) bodyValue;
        }
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public List getParameters()
    {
        if (parameters == null && parameterList != null)
        {
            parameters = parameterList.toArray();
            // we can clean up the parameter list now
            parameterList = null;
        }
        return (parameters == null) ? null : Arrays.asList(parameters);
    }

    public void setParameters(List params)
    {
        parameters = params.toArray();
    }

    @Override
    protected String toStringFields(int indentLevel)
    {
        String s = getOperation();
        String sp = super.toStringFields(indentLevel);
        String sep = getFieldSeparator(indentLevel);
        return sep + "operation = " + s + sp;
    }

}
