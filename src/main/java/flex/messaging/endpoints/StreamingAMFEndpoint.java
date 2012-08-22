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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import flex.management.runtime.messaging.endpoints.StreamingAMFEndpointControl;
import flex.messaging.MessageBroker;
import flex.messaging.endpoints.amf.AMFFilter;
import flex.messaging.endpoints.amf.BatchProcessFilter;
import flex.messaging.endpoints.amf.LegacyFilter;
import flex.messaging.endpoints.amf.MessageBrokerFilter;
import flex.messaging.endpoints.amf.SerializationFilter;
import flex.messaging.endpoints.amf.SessionFilter;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.TypeMarshallingContext;
import flex.messaging.io.amf.Amf3Output;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;

/**
 * Extension to the AMFEndpoint to support streaming HTTP connections to connected
 * clients.
 * Each streaming connection managed by this endpoint consumes one of the request
 * handler threads provided by the servlet container, so it is not highly scalable
 * but offers performance advantages over client polling for clients receiving a steady,
 * rapid stream of pushed messages.
 * This endpoint does not support polling clients and will fault any poll requests
 * that are received. To support polling clients use AMFEndpoint instead.
 */
public class StreamingAMFEndpoint extends BaseStreamingHTTPEndpoint
{    
    //--------------------------------------------------------------------------
    //
    // Public Static Constants
    //
    //--------------------------------------------------------------------------
    
    /**
     * The log category for this endpoint.
     */
    public static final String LOG_CATEGORY = LogCategories.ENDPOINT_STREAMING_AMF;
 
    //--------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    //--------------------------------------------------------------------------
    
    //--------------------------------------------------------------------------
    //
    // Constructors
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs an unmanaged <code>StreamingAMFEndpoint</code>. 
     */
    public StreamingAMFEndpoint()
    {
        this(false);
    }
    
    /**
     * Constructs a <code>StreamingAMFEndpoint</code> with the indicated management.
     * 
     * @param enableManagement <code>true</code> if the <code>StreamingAMFEndpoint</code>
     * is manageable; otherwise <code>false</code>.
     */    
    public StreamingAMFEndpoint(boolean enableManagement)
    {
        super(enableManagement);
    }    
            
    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------      
    
    /**
     * Create the gateway filters that transform action requests
     * and responses.
     */
    @Override protected AMFFilter createFilterChain()
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
     */
    @Override protected String getResponseContentType()
    {
        return MessageIOConstants.AMF_CONTENT_TYPE;
    }
    
    /**
     * Returns the log category of the endpoint.
     * 
     * @return The log category of the endpoint.
     */
    @Override protected String getLogCategory()
    {        
        return LOG_CATEGORY;
    }    
         
    /**
     * Used internally for performance information gathering; not intended for
     * public use. Serializes the message in AMF format and returns the size of 
     * the serialized message.
     * 
     * @param message Message to get the size for.
     * 
     * @return The size of the message after message is serialized.
     */
    @Override protected long getMessageSizeForPerformanceInfo(Message message)
    {
        Amf3Output amfOut = new Amf3Output(serializationContext);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream dataOutStream = new DataOutputStream(outStream);
        amfOut.setOutputStream(dataOutStream);      
        try
        {
            amfOut.writeObject(message);
        }
        catch (IOException e)
        {
            if (Log.isDebug())
                log.debug("MPI exception while retrieving the size of the serialized message: " + e.toString());              
        }
        return dataOutStream.size();
    }
    
    /**
     * Returns the deserializer class name used by the endpoint.
     * 
     * @return The deserializer class name used by the endpoint.
     */
    @Override protected String getDeserializerClassName()
    {
        return "flex.messaging.io.amf.AmfMessageDeserializer";        
    }
    
    /**
     * Returns the serializer class name used by the endpoint.
     * 
     * @return The serializer class name used by the endpoint.
     */
    @Override protected String getSerializerClassName()
    {
        return "flex.messaging.io.amf.AmfMessageSerializer";        
    }

    /**
     * Returns the Java 1.5 specific serializer class name used by the endpoint.
     * 
     * @return The Java 1.5 specific serializer class name used by the endpoint.
     */    
    @Override protected String getSerializerJava15ClassName()
    {
        return "flex.messaging.io.amf.Java15AmfMessageSerializer";
    }
        
    /**
     * Invoked automatically to allow the <code>StreamingAMFEndpoint</code> to setup its 
     * corresponding MBean control.
     * 
     * @param broker The <code>MessageBroker</code> that manages this 
     * <code>StreamingAMFEndpoint</code>.
     */
    @Override protected void setupEndpointControl(MessageBroker broker)
    {
        controller = new StreamingAMFEndpointControl(this, broker.getControl());
        controller.register();
        setControl(controller);
    }    
    
    /**
     * Helper method invoked by the endpoint request handler thread cycling in wait-notify.
     * Serializes messages and streams each to the client as a response chunk using streamChunk().
     * 
     * @param messages The messages to serialize and push to the client.
     * @param os The output stream the chunk will be written to.
     * @param response The HttpServletResponse, used to flush the chunk to the client.
     */
    @Override protected void streamMessages(List messages, ServletOutputStream os, HttpServletResponse response) throws IOException
    {
        if (messages == null || messages.isEmpty())
            return;
                
        // Serialize each message as a separate chunk of bytes.
        TypeMarshallingContext.setTypeMarshaller(getTypeMarshaller());
        for (Iterator iter = messages.iterator(); iter.hasNext();)
        {
            Amf3Output amfOut = getAmfOutput();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            amfOut.setOutputStream(dataOutStream);
            
            Message message = (Message)iter.next();
            
            // Add performance information if MPI is enabled. 
            if (isRecordMessageSizes() || isRecordMessageTimes())
                addPerformanceInfo(message);
            
            if (Log.isDebug())
                log.debug("Endpoint with id '" + getId() + "' is streaming message: " + message);
                        
            amfOut.writeObject(message);
            dataOutStream.flush();
            byte[] messageBytes = outStream.toByteArray();
            streamChunk(messageBytes, os, response);
            
            // Update the push count for the StreamingEndpoint mbean.
            if (isManaged())
            {
                ((StreamingAMFEndpointControl)controller).incrementPushCount();
            }
        }
        TypeMarshallingContext.setTypeMarshaller(null);
    }

    /**
     * Determines and returns the correct Amf3Output implementation for the JVM
     * is use.
     *  
     * @return The correct Amf3Output implementation for the JVM in use.
     */
    protected Amf3Output getAmfOutput()
    {
        String scn = serializationContext.getSerializerClass().getName();
        // Trying not to depend on Java15Amf3Output.
        if (getSerializerJava15ClassName().equals(scn))
        {
            String amfOutClassName = "flex.messaging.io.amf.Java15Amf3Output";
            try
            {
                Class amfOutClass = createClass("flex.messaging.io.amf.Java15Amf3Output");
                Constructor c = amfOutClass.getConstructor(new Class[]{serializationContext.getClass()});
                return (Amf3Output)c.newInstance(new Object[]{serializationContext});
            }
            catch (Exception e)
            {
                if (Log.isDebug())
                    log.debug("Endpoint with id '" + getId() + "' cannot use Java5 specific AMF output class: " + e.getMessage());
            }
        }
        return new Amf3Output(serializationContext);
    }
}