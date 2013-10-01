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

import java.io.Serializable;
import java.util.Map;

/**
 * Messages are sent from Endpoints into the MessageBroker, which then
 * sends them to a Service. The MessageBroker also sends Messages to
 * Endpoints, and a Service may ask its Broker to send Messages to
 * Endpoints.
 *
 * @author neville
 */
public interface Message 
        extends Serializable, Cloneable //, javax.jms.Message
{
    // Message header name constants
    /**
     *  This header is used to transport the FlexClient Id value in messages
     *  sent clients to the server.
     */
    String FLEX_CLIENT_ID_HEADER = "DSId";    
    
    /**
     * The name for the destination client Id header, used to target a message
     * back to the proper client when multiple clients share a channel.
     */
    String DESTINATION_CLIENT_ID_HEADER = "DSDstClientId";
    
    /**
     * The name for the endpoint header.
     */
    String ENDPOINT_HEADER = "DSEndpoint";
    
    /**
     * @exclude
     * Used internally to enable/disable validation of the channel endpoint that a
     * message for a destination arrived over.
     */
    String VALIDATE_ENDPOINT_HEADER = "DSValidateEndpoint";
    
    /**
     * The name for the header where remote credentials will be passed.
     */
    String REMOTE_CREDENTIALS_HEADER = "DSRemoteCredentials";

    /**
     *  The name of the header that reports which character set encoding was
     *  used to create remote credentials.  
     */
    String REMOTE_CREDENTIALS_CHARSET_HEADER = "DSRemoteCredentialsCharset";

    /**
     * Presence of param means that auto-sync is false for the destination.
     */
    String SYNC_HEADER = "sync";    
    
    /**
     * Returns the client id indicating the client that sent the message.
     * 
     * @return The client id indicating the client that sent the message.
     */
    Object getClientId();
    
    /**
     * Sets the client id indicating the client that sent the message.
     * 
     * @param value The client id to set for the message.
     */
    void setClientId(Object value);

    /**
     * Returns the destination that the message targets.
     * 
     * @return The destination that the message targets.
     */
    String getDestination();
    
    /**
     * Sets the destination that the message targets.
     * 
     * @param value The destination that the message targets.
     */
    void setDestination(String value);

    /**
     * Returns the unique message id.
     * 
     * @return The unique message id.
     */
    String getMessageId();
    
    /**
     * Sets the unique message id.
     * The id value should be universally unique.
     * 
     * @param value The unique message id.
     */
    void setMessageId(String value);

    /**
     * Returns the timestamp for the message.
     * Number of milleseconds since the epoch.
     * 
     * @return The timestamp for the message.
     */
    long getTimestamp();
    
    /**
     * Sets the timestamp for the message.
     * Number of milliseconds since the epoch.
     * 
     * @param value The timestamp for the message.
     */
    void setTimestamp(long value);

    /**
     * Returns the time to live for the message. This is the number of 
     * milliseconds beyond the message timestamp that the message is 
     * considered valid and deliverable.
     * 
     * @return The time to live for the message.
     */
    long getTimeToLive();
    
    /**
     * Sets the time to live for the message. This is the number of milliseconds
     * beyond the message timestamp that the message will be considered valid and
     * deliverable.
     * 
     * @param value The time to live for the message.
     */
    void setTimeToLive(long value);

    /**
     * Returns the body of the message.
     * 
     * @return The body of the message.
     */
    Object getBody();
    
    /**
     * Sets the body of the message.
     * 
     * @param value The body of the message.
     */
    void setBody(Object value);

    /**
     * Returns the headers for the message.
     *
     * @return The headers for the message.
     */
    Map getHeaders();
    
    /**
     * Sets the headers for the message.
     * 
     * @param value The headers to set on the message.
     */
    void setHeaders(Map value);

    /**
     * Returns a header value corresponding to the passed header name.
     * If no header with this name exists, <code>null</code> is returned.
     * 
     * @param name The header name to retrieve a value for.
     * @return The header value.
     */
    Object getHeader(String name);
    
    /**
     * Sets a header on the message.
     * 
     * @param name The name of the header to set.
     * @param value The value for the header.
     */
    void setHeader(String name, Object value);

    /**
     * Tests whether a header with the passed name exists.
     * 
     * @param name The header to test for existence.
     * @return <code>true</code> if the headers exists; otherwise <code>false</code>.
     */
    boolean headerExists(String name);

    /**
     * Returns a clone of the message.
     * 
     * @return A clone of the message.
     */
    Object clone();
}
