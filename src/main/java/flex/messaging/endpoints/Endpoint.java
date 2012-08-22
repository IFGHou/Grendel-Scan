/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.management.Manageable;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.SecurityConstraint;

/**
 * An endpoint receives messages from clients and decodes them,
 * then sends them on to a MessageBroker for routing to a service.
 * The endpoint also encodes messages and delivers them to clients.
 * Endpoints are specific to a message format and network transport,
 * and are defined by the named URI path on which they are located.
 * 
 * @author neville
 */
public interface Endpoint extends Manageable
{
    /**
     * Initialize the endpoint with id and properties. 
     * 
     * @param id The id of the endpoint. 
     * @param properties Properties of the endpoint. 
     */
    void initialize(String id, ConfigMap properties);
    
    /**
     * Start the endpoint. The MethodBroker invokes this
     * method in order to set the endpoint up for sending and receiving
     * messages from Flash clients.
     *
     */
    void start();
    
    /**
     * Returns whether the endpoint is started or not. 
     * 
     * @return <code>true</code> if the endpoint is started; otherwise <code>false</code>. 
     */
    boolean isStarted();
    
    /**
     * Stop and destroy the endpoint. The MethodBroker invokes
     * this method in order to stop the endpoint from sending
     * and receiving messages from Flash clients.
     * 
     */
    void stop();
       
    /**
     * Returns the corresponding client channel type for the endpoint. 
     * 
     * @return The corresponding client channel type for the endpoint. 
     */
    String getClientType();
    
    /**
     * Sets the corresponding client channel type for the endpoint. 
     * 
     * @param clientType The corresponding client channel type for the endpoint.  
     */
    void setClientType(String clientType);
      
    /**
     * Returns a <code>ConfigMap</code> of endpoint properties that the client
     * needs.
     */
    ConfigMap describeEndpoint();

    /**
     * All endpoints are referenceable by an id that is unique among
     * all the endpoints registered to a single broker instance.
     */
    String getId();
    
    /**
     * All endpoints are referenceable by an id that is unique among
     * all the endpoints registered to a single broker instance. The id
     * is set through this method, usually through parsed configuration.
     */
    void setId(String id);
    
    /**
     * All endpoints must be managed by a single MessageBroker,
     * and must be capable of returning a reference to that broker.
     * This broker reference is used when the endpoint wishes to 
     * send a message to one of the broker's services.
     * 
     * @return broker The MessageBroker instance which manages this endpoint
     */
    MessageBroker getMessageBroker();
    
    /**
     * Sets the <code>MessageBroker</code> of the endpoint. 
     * 
     * @param broker
     */
    void setMessageBroker(MessageBroker broker);

    /**
     * Returns the highest messaging version currently available via this
     * endpoint.  
     */
    double getMessagingVersion();
    
    /** @exclude **/
    String getParsedUrl(String contextPath);
    
    /**
     * Returns the port of the url of the endpoint. 
     * 
     * @return The port of the url of the endpoint. 
     */
    int getPort();
       
    /**
     * Specifies whether this protocol requires the secure HTTPS protocol. 
     */
    boolean isSecure();
        
    /**
     * Returns the security constraint of the endpoint. 
     * 
     * @return The security constraint of the endpoint.
     */
    SecurityConstraint getSecurityConstraint();
    
    /**
     * Sets the security constraint of the endpoint. 
     * 
     * @param constraint
     */
    void setSecurityConstraint(SecurityConstraint constraint);

    /**
     * Respond to HTTP-based messages published by a client. Endpoints which
     * do not support access over HTTP should throw an UnsupportedOperationException
     * in the implementation of htis method.
     */
    void service(HttpServletRequest req, HttpServletResponse res);
        
    /**
     * Returns the url of the endpoint. 
     * 
     * @return The url of the endpoint. 
     */
    String getUrl();
    
    /**
     * Sets the url of the endpoint. 
     * 
     * @param url
     */
    void setUrl(String url);  
    
    /**
     * @exclude
     * Returns the url of the endpoint parsed for the client. 
     *  
     * @return The url of the endpoint parsed for the client.
     */
    String getUrlForClient();
}
