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
package flex.messaging.io.amf;

import java.io.Serializable;

import flex.messaging.io.MessageIOConstants;

/**
 * An AMF Message contains information about the actual individual transaction that is to be performed. It specifies the remote operation that is to be performed; a local (client) operation to be
 * invoked upon success; and, the data to be used in the operation.
 * <p>
 * This Message structure defines how a local client would invoke a method/operation on a remote server. Additionally, the response from the Server is structured identically.
 * </p>
 * 
 * @author Simeon Simeonov (simeons@macromedia.com)
 * @exclude
 */
public class MessageBody implements Serializable
{
    static final long serialVersionUID = 3874002169129668459L;

    /**
     * A String describing which operation, function, or method is to be remotely invoked. In the case of server to client transactions, this should specify a path relative to the client's controlling
     * NetConnection object.
     */
    private String targetURI = "";

    /**
     * Universal Resource Identifier that uniquely targets the originator's Object that should receive the server's response. The server will use this path specification to target the "OnResult()" or
     * "onStatus()" handlers within the client. For Flash, it specifies an ActionScript Object path only. The NetResponse object pointed to by the Response URI contains the connection state
     * information. Passing/specifying this provides a convenient mechanism for the client/server to share access to an object that is managing the state of the shared connection.
     * <p>
     * Since the server will use this field in the event of an error, this field is required even if a successful server request would not be expected to return a value to the client.
     * </p>
     */
    private String responseURI = "";

    /**
     * Contains the actual data associated with the operation. It contains the client's parameter data that is passed to the server's operation/method. When serializing a root level data type or a
     * parameter list array, no name field is included. That is, the data is anonomously represented as "Type"/"Value" pairs. When serializing member data, the data is represented as a series of
     * "Name"/"Type"/"Value" combinations.
     * <p>
     * An argument list for a method/function should be represented within a Strict Array type.
     * </p>
     * <p>
     * For server generated responses, it may contain any ActionScript data/objects that the server was expected to provide.
     * </p>
     * <p>
     * Additionally, if the server detects an error with the incoming (client) data and/or the operation requested, the Server will provide an InfoObject in this section.
     * </p>
     */
    protected Object data;

    public MessageBody()
    {
    }

    /*
     * TODO UCdetector: Remove unused code: public MessageBody(String targetURI, String responseURI, Object data) { setTargetURI(targetURI); setResponseURI(responseURI); this.data = data; }
     */

    public String getTargetURI()
    {
        return targetURI;
    }

    public void setTargetURI(String uri)
    {
        if (uri == null)
            uri = "";

        targetURI = uri;
    }

    public void setReplyMethod(String methodName)
    {
        if (targetURI.endsWith(MessageIOConstants.STATUS_METHOD) || targetURI.endsWith(MessageIOConstants.RESULT_METHOD))
        {
            targetURI = targetURI.substring(0, targetURI.lastIndexOf("/"));
        }
        targetURI = targetURI + methodName;
    }

    public String getReplyMethod()
    {
        return targetURI.substring((targetURI.lastIndexOf("/") + 1), targetURI.length());
    }

    public String getResponseURI()
    {
        return responseURI;
    }

    public void setResponseURI(String uri)
    {
        if (uri == null)
            uri = "";

        responseURI = uri;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }
}
