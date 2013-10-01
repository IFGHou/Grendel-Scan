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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.management.runtime.messaging.endpoints.EndpointControl;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.HttpFlexSession;
import flex.messaging.MessageClient;
import flex.messaging.client.FlexClient;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.endpoints.amf.AMFFilter;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.util.SettingsReplaceUtil;

/**
 * Base for all of HTTP-based endpoints.
 */
public abstract class BaseHTTPEndpoint extends AbstractEndpoint 
{
    //--------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    //--------------------------------------------------------------------------

    private static final String ADD_NO_CACHE_HEADERS = "add-no-cache-headers";
    private static final String REDIRECT_URL = "redirect-url";
    private static final String INVALIDATE_SESSION_ON_DISCONNECT = "invalidate-session-on-disconnect";
    
    private static final int ERR_MSG_DUPLICATE_SESSIONS_DETECTED = 10035;
    private static final String REQUEST_ATTR_DUPLICATE_SESSION_FLAG = "flex.messaging.request.DuplicateSessionDetected";

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>BaseHTTPEndpoint</code>. 
     */
    public BaseHTTPEndpoint()
    {
        this(false);
    }

    /**
     * Constructs an <code>BaseHTTPEndpoint</code> with the indicated management.
     * 
     * @param enableManagement <code>true</code> if the <code>BaseHTTPEndpoint</code>
     * is manageable; otherwise <code>false</code>.
     */
    public BaseHTTPEndpoint(boolean enableManagement)
    {
        super(enableManagement);              
    }

    //--------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods. 
    //
    //--------------------------------------------------------------------------

    /**
     * Initializes the <code>Endpoint</code> with the properties. 
     * If subclasses override, they must call <code>super.initialize()</code>.
     * 
     * @param id Id of the <code>Endpoint</code>. 
     * @param properties Properties for the <code>Endpoint</code>.
     */    
    @Override public void initialize(String id, ConfigMap properties)
    {
        super.initialize(id, properties);

        if (properties == null || properties.size() == 0)
            return;

        // General HTTP props.
        addNoCacheHeaders = properties.getPropertyAsBoolean(ADD_NO_CACHE_HEADERS, true);
        redirectURL = properties.getPropertyAsString(REDIRECT_URL, null);
        invalidateSessionOnDisconnect = properties.getPropertyAsBoolean(INVALIDATE_SESSION_ON_DISCONNECT, false);

        loginAfterDisconnect = properties.getPropertyAsBoolean(ConfigurationConstants.LOGIN_AFTER_DISCONNECT_ELEMENT, false);

        validateEndpointProtocol();
    }

    /**
     * Starts the <code>Endpoint</code> by creating a filter chain and setting
     * up serializers and deserializers. 
     */
    @Override public void start()
    {
        if (isStarted())
            return;

        super.start();

        filterChain = createFilterChain();
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Controller used to manage this endpoint.
     */
    protected EndpointControl controller;      

    /**
     * AMF processing filter chain used by this endpoint.
     */
    protected AMFFilter filterChain;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  addNoCacheHeaders
    //----------------------------------

    protected boolean addNoCacheHeaders = true;

    /**
     * Returns the <code>add-no-cache-headers</code> property. 
     * 
     * @return <code>true</code> if <code>add-no-cache-headers</code> is enabled;
     * otherwise <code>false</code>.
     */
    public boolean isAddNoCacheHeaders()
    {
        return addNoCacheHeaders;
    }  

    /**
     * Sets the <code>add-no-cache-headers</code> property.
     * 
     * @param addNoCacheHeaders
     */
    public void setAddNoCacheHeaders(boolean addNoCacheHeaders)
    {
        this.addNoCacheHeaders = addNoCacheHeaders;
    }

    //----------------------------------
    //  loginAfterDisconnect
    //---------------------------------- 

    /**
     * @exclude
     * This is a property used on the client. 
     */
    protected boolean loginAfterDisconnect; 
    
    //----------------------------------
    //  invalidateSessionOnDisconnect
    //---------------------------------- 
    
    protected boolean invalidateSessionOnDisconnect;
    
    /**
     * Indicates whether the server session will be invalidated
     * when a client channel disconnects.
     * Default is <code>false</code>.
     * 
     * @return <code>true</code> if the server session will be invalidated
     *         when a client channel disconnects.
     */
    public boolean isInvalidateSessionOnDisconnect()
    {
        return invalidateSessionOnDisconnect;
    }
    
    /**
     * Set to <code>true</code> to invalidate the server session for a client
     * that disconnects its channel.
     * Default is <code>false</code>.
     * 
     * @param value Set to <code>true</code> to invalidate the server session for a client
     *              that disconnects its channel.
     */
    public void setInvalidateSessionOnDisconnect(boolean value)
    {
        invalidateSessionOnDisconnect = value;
    }

    //----------------------------------
    //  redirectURL
    //---------------------------------- 

    protected String redirectURL;

    /**
     * Returns the <code>redirect-url</code> property. 
     * 
     * @return The <code>redirect-url</code> property.
     */
    public String getRedirectURL()
    {
        return redirectURL;
    }    

    /**
     * Sets the <code>redirect-url</code> property.
     * 
     * @param redirectURL
     */
    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------          

    /**
     * Handle AMF/AMFX encoded messages sent over HTTP. 
     * 
     * @param req The original servlet request.
     * @param res The active servlet response.
     */
    @Override public void service(HttpServletRequest req, HttpServletResponse res)
    {
        super.service(req, res);

        try
        {
            // Setup serialization and type marshalling contexts
            setThreadLocals();

            // Create a context for this request
            ActionContext context = new ActionContext();

            // Pass endpoint's mpi settings to the context so that it knows what level of 
            // performance metrics should be gathered during serialization/deserialization
            context.setRecordMessageSizes(isRecordMessageSizes());
            context.setRecordMessageTimes(isRecordMessageTimes());

            // Send invocation through filter chain, which ends at the MessageBroker
            filterChain.invoke(context);

            // After serialization completes, increment endpoint byte counters, 
            // if the endpoint is managed
            if (isManaged())
            {
                controller.addToBytesDeserialized(context.getDeserializedBytes());
                controller.addToBytesSerialized(context.getSerializedBytes());
            }

            if (context.getStatus() != MessageIOConstants.STATUS_NOTAMF)
            {
                if (addNoCacheHeaders)
                    addNoCacheHeaders(req, res);

                ByteArrayOutputStream outBuffer = context.getResponseOutput();
                res.setContentType(getResponseContentType());
                res.setContentLength(outBuffer.size());
                outBuffer.writeTo(res.getOutputStream());
                res.flushBuffer();
            }
            else
            {
                // Not an AMF request, probably viewed in a browser
                if (redirectURL != null)
                {
                    try
                    {
                        //Check for redirect URL context-root token
                        redirectURL = SettingsReplaceUtil.replaceContextPath(redirectURL, req.getContextPath());
                        res.sendRedirect(redirectURL);
                    }
                    catch (IllegalStateException alreadyFlushed)
                    {
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            // This happens when client closes the connection, log it at info level
            log.info(ioe.getMessage());
        }
        catch (Throwable t)
        {
            log.error(t.getMessage(), t);
        }
        finally
        {
            clearThreadLocals();
        }        
    }


    /**
     * @exclude 
     * Returns a <code>ConfigMap</code> of endpoint properties that the client
     * needs. This includes properties from <code>super.describeEndpoint</code> 
     * and additional <code>BaseHTTPEndpoint</code> specific properties under
     * "properties" key. 
     */
    @Override public ConfigMap describeEndpoint()
    {
        ConfigMap endpointConfig = super.describeEndpoint();

        boolean createdProperties = false;
        ConfigMap properties = endpointConfig.getPropertyAsMap("properties", null);

        if (properties == null)
        {
            properties = new ConfigMap();
            createdProperties = true;
        }

        if (loginAfterDisconnect)
        {
            ConfigMap loginAfterDisconnect = new ConfigMap();
            // Adding as a value rather than attribute to the parent
            loginAfterDisconnect.addProperty("", "true");            
            properties.addProperty(ConfigurationConstants.LOGIN_AFTER_DISCONNECT_ELEMENT, loginAfterDisconnect);
        }                       

        if (createdProperties && properties.size() > 0)
            endpointConfig.addProperty("properties", properties);

        return endpointConfig;
    }
    
    /**
     * Overrides to guard against duplicate HTTP-based sessions for the same FlexClient
     * which will occur if the remote host has disabled session cookies.
     * 
     * @see AbstractEndpoint#setupFlexClient(String)
     */
    @Override public FlexClient setupFlexClient(String id)
    {
        FlexClient flexClient = super.setupFlexClient(id);
        
        // Scan for duplicate HTTP-sessions and if found, invalidate them and throw a MessageException.
        // A request attribute is used to deal with batched AMF messages that arrive in a single request by trigger multiple passes through this method.
        boolean duplicateSessionDetected = (FlexContext.getHttpRequest().getAttribute(REQUEST_ATTR_DUPLICATE_SESSION_FLAG) != null)
                                           ? true : false;
        
        if (!duplicateSessionDetected)
        {
            List sessions = flexClient.getFlexSessions();
            int n = sessions.size();
            if (n > 1)
            {
                int count = 0;            
                for (int i = 0; i < n; i++)
                {
                    if (sessions.get(i) instanceof HttpFlexSession)
                        count++;
                    if (count > 1)
                    {
                        FlexContext.getHttpRequest().setAttribute(REQUEST_ATTR_DUPLICATE_SESSION_FLAG, Boolean.TRUE);
                        duplicateSessionDetected = true;
                        break;
                    }
                }
            }
        }
        
        // If more than one was found, remote host isn't using session cookies. Kill all duplicate sessions and return an error.
        // Simplest to just re-scan the list given that it will be very short, but use an iterator for concurrent modification.
        if (duplicateSessionDetected)
        {
            List sessions = flexClient.getFlexSessions();
            for (Iterator iter = sessions.iterator(); iter.hasNext();)
            {
                FlexSession session = (FlexSession)iter.next();
                if (session instanceof HttpFlexSession)
                    session.invalidate();
            }
            
            // Return an error to the client.
            DuplicateSessionException e = new DuplicateSessionException();
            e.setMessage(ERR_MSG_DUPLICATE_SESSIONS_DETECTED);
            throw e;
        }

        return flexClient;
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
    protected abstract AMFFilter createFilterChain();

    /**
     * Returns the content type used by the connection handler to set on the 
     * HTTP response. Subclasses should either return MessageIOConstants.AMF_CONTENT_TYPE 
     * or MessageIOConstants.XML_CONTENT_TYPE. 
     */
    protected abstract String getResponseContentType();
    
    /**
     * @see flex.messaging.endpoints.AbstractEndpoint#handleChannelDisconnect(CommandMessage)
     */
    @Override protected Message handleChannelDisconnect(CommandMessage disconnectCommand)
    {        
        HttpFlexSession session = (HttpFlexSession)FlexContext.getFlexSession();
        FlexClient flexClient = FlexContext.getFlexClient();
        
        // Shut down any subscriptions established over this channel/endpoint
        // for this specific FlexClient.
        if (flexClient.isValid())
        {
            String endpointId = getId();            
            List messageClients = flexClient.getMessageClients();
            for (Iterator iter = messageClients.iterator(); iter.hasNext();)
            {
                MessageClient messageClient = (MessageClient)iter.next();
                if (messageClient.getEndpointId().equals(endpointId))
                {
                    messageClient.setClientChannelDisconnected(true);
                    messageClient.invalidate();
                }
            }
        }
        
        // And optionally invalidate the session.
        if (session.isValid() && isInvalidateSessionOnDisconnect())
            session.invalidate(false /* don't recreate */);

        return super.handleChannelDisconnect(disconnectCommand);
    }

    protected void validateEndpointProtocol()
    {
        if (isSecure() && !url.startsWith("https:"))
        {
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(11100, new Object[] {url, "https"});
            throw ce;
        }
    }
}