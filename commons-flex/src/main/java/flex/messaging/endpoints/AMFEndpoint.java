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

import flex.management.runtime.messaging.endpoints.AMFEndpointControl;
import flex.messaging.MessageBroker;
import flex.messaging.endpoints.amf.AMFFilter;
import flex.messaging.endpoints.amf.BatchProcessFilter;
import flex.messaging.endpoints.amf.LegacyFilter;
import flex.messaging.endpoints.amf.MessageBrokerFilter;
import flex.messaging.endpoints.amf.SerializationFilter;
import flex.messaging.endpoints.amf.SessionFilter;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.log.LogCategories;

/**
 * AMF based endpoint for Flex Messaging. Based on the Flash Remoting gateway servlet.
 * 
 * @author Sean Neville
 * @author Peter Farland
 */
public class AMFEndpoint extends BasePollingHTTPEndpoint
{
    /**
     * The log category for this endpoint.
     */
    public static final String LOG_CATEGORY = LogCategories.ENDPOINT_AMF;

    // --------------------------------------------------------------------------
    //
    // Constructors
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>AMFEndpoint</code>.
     */
    public AMFEndpoint()
    {
        this(false);
    }

    /**
     * Constructs an <code>AMFEndpoint</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>AMFEndpoint</code> is manageable; otherwise <code>false</code>.
     */
    public AMFEndpoint(boolean enableManagement)
    {
        super(enableManagement);
    }

    // --------------------------------------------------------------------------
    //
    // Protected/Private Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Create the gateway filters that transform action requests and responses.
     */
    @Override
    protected AMFFilter createFilterChain()
    {
        AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
        AMFFilter batchFilter = new BatchProcessFilter();
        AMFFilter sessionFilter = new SessionFilter();
        AMFFilter envelopeFilter = new LegacyFilter(this);
        AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);

        serializationFilter.setNext(batchFilter);
        batchFilter.setNext(sessionFilter);
        sessionFilter.setNext(envelopeFilter);
        envelopeFilter.setNext(messageBrokerFilter);

        return serializationFilter;
    }

    /**
     * Returns MessageIOConstants.AMF_CONTENT_TYPE.
     * 
     * @return MessageIOConstants.AMF_CONTENT_TYPE
     */
    @Override
    protected String getResponseContentType()
    {
        return MessageIOConstants.AMF_CONTENT_TYPE;
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
        return "flex.messaging.io.amf.AmfMessageDeserializer";
    }

    /**
     * Returns the serializer class name used by the endpoint.
     * 
     * @return The serializer class name used by the endpoint.
     */
    @Override
    protected String getSerializerClassName()
    {
        return "flex.messaging.io.amf.AmfMessageSerializer";
    }

    /**
     * Returns the Java 1.5 specific serializer class name used by the endpoint.
     * 
     * @return The Java 1.5 specific serializer class name used by the endpoint.
     */
    @Override
    protected String getSerializerJava15ClassName()
    {
        return "flex.messaging.io.amf.Java15AmfMessageSerializer";
    }

    /**
     * Invoked automatically to allow the <code>AMFEndpoint</code> to setup its corresponding MBean control.
     * 
     * @param broker
     *            The <code>MessageBroker</code> that manages this <code>AMFEndpoint</code>.
     */
    @Override
    protected void setupEndpointControl(MessageBroker broker)
    {
        controller = new AMFEndpointControl(this, broker.getControl());
        controller.register();
        setControl(controller);
    }
}
