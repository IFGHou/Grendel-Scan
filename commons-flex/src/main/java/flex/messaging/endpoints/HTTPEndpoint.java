/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.endpoints;

import flex.management.runtime.messaging.endpoints.HTTPEndpointControl;
import flex.messaging.MessageBroker;
import flex.messaging.endpoints.amf.AMFFilter;
import flex.messaging.endpoints.amf.BatchProcessFilter;
import flex.messaging.endpoints.amf.MessageBrokerFilter;
import flex.messaging.endpoints.amf.SerializationFilter;
import flex.messaging.endpoints.amf.SessionFilter;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;

/**
 * This class replaces Flex 1.5's ProxyServlet by splitting the proxy's functionality into two pieces. Requests for proxied HTTP content can now be sent using a message type via any channel. The
 * message broker directs requests to the appropriate service, in Flex 1.5 terms, the Proxy Service. The response from the proxy request is streamed back to the client.
 * 
 * //TODO: QUESTION: Pete, How should we wrap HTTP responses via this channel?
 */
public class HTTPEndpoint extends BasePollingHTTPEndpoint
{
    public static final String LOG_CATEGORY = LogCategories.ENDPOINT_HTTP;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>HTTPEndpoint</code>.
     */
    public HTTPEndpoint()
    {
        this(false);
    }

    /**
     * Constructs a <code>HTTPEndpoint</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>HTTPEndpoint</code> is manageable; otherwise <code>false</code>.
     */
    public HTTPEndpoint(boolean enableManagement)
    {
        super(enableManagement);
    }

    /**
     * Currently this override is a no-op to disable small messages over HTTP endpoints.
     */
    @Override
    public Message convertToSmallMessage(Message message)
    {
        return message;
    }

    // --------------------------------------------------------------------------
    //
    // Protected/Private Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Create default filter chain or return current one if already present.
     */
    @Override
    protected AMFFilter createFilterChain()
    {
        AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
        AMFFilter batchFilter = new BatchProcessFilter();
        AMFFilter sessionFilter = new SessionFilter();
        AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);

        serializationFilter.setNext(batchFilter);
        batchFilter.setNext(sessionFilter);
        sessionFilter.setNext(messageBrokerFilter);

        return serializationFilter;
    }

    /**
     * Returns MessageIOConstants.XML_CONTENT_TYPE.
     */
    @Override
    protected String getResponseContentType()
    {
        return MessageIOConstants.XML_CONTENT_TYPE;
    }

    /**
     * Returns the log category of the endpoint.
     * 
     * @return The log category of the endpoint.
     */
    @Override
    protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Returns the deserializer class name used by the endpoint.
     * 
     * @return The deserializer class name used by the endpoint.
     */
    @Override
    protected String getDeserializerClassName()
    {
        return "flex.messaging.io.amfx.AmfxMessageDeserializer";
    }

    /**
     * Returns the serializer class name used by the endpoint.
     * 
     * @return The serializer class name used by the endpoint.
     */
    @Override
    protected String getSerializerClassName()
    {
        return "flex.messaging.io.amfx.AmfxMessageSerializer";
    }

    /**
     * Returns the Java 1.5 specific serializer class name used by the endpoint.
     * 
     * @return The Java 1.5 specific serializer class name used by the endpoint.
     */
    @Override
    protected String getSerializerJava15ClassName()
    {
        return "flex.messaging.io.amfx.Java15AmfxMessageSerializer";
    }

    /**
     * Invoked automatically to allow the <code>HTTPEndpoint</code> to setup its corresponding MBean control.
     * 
     * @param broker
     *            The <code>MessageBroker</code> that manages this <code>HTTPEndpoint</code>.
     */
    @Override
    protected void setupEndpointControl(MessageBroker broker)
    {
        controller = new HTTPEndpointControl(this, broker.getControl());
        controller.register();
        setControl(controller);
    }
}
