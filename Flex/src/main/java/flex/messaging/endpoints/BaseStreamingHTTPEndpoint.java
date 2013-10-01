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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.MessageException;
import flex.messaging.client.EndpointPushNotifier;
import flex.messaging.client.FlexClient;
import flex.messaging.client.FlushResult;
import flex.messaging.client.UserAgentSettings;
import flex.messaging.config.ConfigMap;
import flex.messaging.log.Log;
import flex.messaging.messages.AcknowledgeMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.messages.MessagePerformanceInfo;
import flex.messaging.messages.MessagePerformanceUtils;
import flex.messaging.util.TimeoutManager;
import flex.messaging.util.UserAgentManager;

/**
 * Base for HTTP-based endpoints that support streaming HTTP connections to
 * connected clients.
 * Each streaming connection managed by this endpoint consumes one of the request
 * handler threads provided by the servlet container, so it is not highly scalable
 * but offers performance advantages over client polling for clients receiving
 * a steady, rapid stream of pushed messages.
 * This endpoint does not support polling clients and will fault any poll requests
 * that are received. To support polling clients use subclasses of
 * BaseHTTPEndpoint instead.
 */
public abstract class BaseStreamingHTTPEndpoint extends BaseHTTPEndpoint
{
    //--------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    //--------------------------------------------------------------------------

    /**
     * This token is used in chunked HTTP responses frequently so initialize it statically for general use.
     */
    private static final byte[] CRLF_BYTES = {(byte)13, (byte)10};

    /**
     * This token is used for the terminal chunk within a chunked response.
     */
    private static final byte ZERO_BYTE = (byte)48;

    /**
     * This token is used to signal that a chunk of data should be skipped by the client.
     */
    private static final byte NULL_BYTE = (byte)0;

    /**
     * Parameter name for 'command' passed in a request for a new streaming connection.
     */
    private static final String COMMAND_PARAM_NAME = "command";

    /**
     * This is the token at the end of the HTTP request line that indicates that it's
     * a stream connection that we should hold open to push data back to the client over
     * as opposed to a regular request-response message.
     */
    private static final String OPEN_COMMAND = "open";

    /**
     * This is the token at the end of the HTTP request line that indicates that it's
     * a stream connection that we should close.
     */
    private static final String CLOSE_COMMAND = "close";

    /**
     *  Parameter name for the stream id; passed with commands for an existing streaming connection.
     */
    private static final String STREAM_ID_PARAM_NAME = "streamId";

    /**
     * Parameter name for 'version' passed in a request for a new streaming connection.
     */
    private static final String VERSION_PARAM_NAME = "version";

    /**
     * Constant for HTTP/1.0.
     */
    private static final String HTTP_1_0 = "HTTP/1.0";

    /**
     * Thread name suffix for request threads that are servicing a pinned open streaming connection.
     */
    private static final String STREAMING_THREAD_NAME_EXTENSION = "-in-streaming-mode";

    /**
     * Configuration constants.
     */
    private static final String IDLE_TIMEOUT_MINUTES = "idle-timeout-minutes";
    private static final String MAX_STREAMING_CLIENTS = "max-streaming-clients";
    private static final String SERVER_TO_CLIENT_HEARTBEAT_MILLIS = "server-to-client-heartbeat-millis";

    /**
     * Defaults.
     */
    private static final int DEFAULT_SERVER_TO_CLIENT_HEARTBEAT_MILLIS = 5000;
    private static final int DEFAULT_IDLE_TIMEOUT_MINUTES = 0;
    private static final int DEFAULT_MAX_STREAMING_CLIENTS = 10;

    /**
     * Errors.
     */
    public static final String POLL_NOT_SUPPORTED_CODE = "Server.PollNotSupported";
    public static final int POLL_NOT_SUPPORTED_MESSAGE = 10034;


    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>BaseStreamingHTTPEndpoint</code>.
     */
    public BaseStreamingHTTPEndpoint()
    {
        this(false);
    }

    /**
     * Constructs an <code>BaseStreamingHTTPEndpoint</code> with the indicated management.
     *
     * @param enableManagement <code>true</code> if the <code>BaseStreamingHTTPEndpoint</code>
     * is manageable; otherwise <code>false</code>.
     */
    public BaseStreamingHTTPEndpoint(boolean enableManagement)
    {
        super(enableManagement);
        setIdleTimeoutMinutes(idleTimeoutMinutes);
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

        // The interval that the server will check if the client is still available.
        serverToClientHeartbeatMillis = properties.getPropertyAsLong(SERVER_TO_CLIENT_HEARTBEAT_MILLIS, DEFAULT_SERVER_TO_CLIENT_HEARTBEAT_MILLIS);
        setServerToClientHeartbeatMillis(serverToClientHeartbeatMillis);

        // Number of minutes a client can remain idle before the server times the connection out.
        int idleTimeoutMinutes = properties.getPropertyAsInt(IDLE_TIMEOUT_MINUTES, DEFAULT_IDLE_TIMEOUT_MINUTES);
        setIdleTimeoutMinutes(idleTimeoutMinutes);

        // User-agent configuration for kick-start bytes and max streaming connections per session.
        UserAgentManager.setupUserAgentManager(properties, userAgentManager);

        // Maximum number of clients allowed to have streaming HTTP connections with the endpoint.
        maxStreamingClients = properties.getPropertyAsInt(MAX_STREAMING_CLIENTS, DEFAULT_MAX_STREAMING_CLIENTS);

        // Set initial state for the canWait flag based on whether we allow waits or not.
        canStream =  (maxStreamingClients > 0);
    }


    @Override public void start()
    {
        if (isStarted())
            return;

        super.start();

        if (idleTimeoutMinutes > 0)
            pushNotifierTimeoutManager = new TimeoutManager();

        currentStreamingRequests = new ConcurrentHashMap();
    }

    /**
     * @see flex.messaging.endpoints.AbstractEndpoint#stop()
     */
    @Override public void stop()
    {
        if (!isStarted())
            return;

        // Shutdown the timeout manager for streaming connections cleanly.
        if (pushNotifierTimeoutManager != null)
        {
            pushNotifierTimeoutManager.shutdown();
            pushNotifierTimeoutManager = null;
        }

        // Shutdown any currently open streaming connections.
        for (Iterator iter = currentStreamingRequests.values().iterator(); iter.hasNext(); )
        {
            EndpointPushNotifier notifier = (EndpointPushNotifier)iter.next();
            notifier.close();
        }
        currentStreamingRequests = null;

        super.stop();
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Used to synchronize sets and gets to the number of streaming clients.
     */
    protected final Object lock = new Object();

    /**
     * Used to keep track of the mapping between user agent match strings and
     * the bytes needed to kickstart their streaming connections.
     */
    protected UserAgentManager userAgentManager = new UserAgentManager();

    /**
     * This flag is volatile to allow for consistent reads across thread without
     * needing to pay the cost for a synchronized lock for each read.
     */
    private volatile boolean canStream = true;

    /**
     * Manages timing out EndpointPushNotifier instances.
     */
    private volatile TimeoutManager pushNotifierTimeoutManager;

    /**
     * A Map(EndpointPushNotifier, Boolean.TRUE) containing all currently open streaming notifiers
     * for this endpoint.
     * Used for clean shutdown.
     */
    private ConcurrentHashMap currentStreamingRequests;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  serverToClientHeartbeatMillis
    //----------------------------------

    private long serverToClientHeartbeatMillis = DEFAULT_SERVER_TO_CLIENT_HEARTBEAT_MILLIS;

    /**
     * Returns the number of milliseconds the server will wait before writing a
     * single null byte to the streaming connection to make sure the client is
     * still available.
     */
    public long getServerToClientHeartbeatMillis()
    {
        return serverToClientHeartbeatMillis;
    }

    /**
     * Returns the number of milliseconds the server will wait before writing a
     * single null byte to the streaming connection to make sure the client is
     * still available when there are no new messages for the client.
     * A non-positive value means server will wait forever for new messages and
     * it will not write the null byte to determine if the client is available.
     */
    public void setServerToClientHeartbeatMillis(long serverToClientHeartbeatMillis)
    {
        if (serverToClientHeartbeatMillis < 0)
            serverToClientHeartbeatMillis = 0;
        this.serverToClientHeartbeatMillis = serverToClientHeartbeatMillis;
    }

    //----------------------------------
    //  idleTimeoutMinutes
    //----------------------------------

    private int idleTimeoutMinutes = DEFAULT_IDLE_TIMEOUT_MINUTES;

    /**
     * Returns the number of minutes a client can remain idle before the server
     * times the connection out.
     *
     * @return The number of minutes a client can remain idle before the server
     * times the connection out.
     */
    public int getIdleTimeoutMinutes()
    {
        return idleTimeoutMinutes;
    }

    /**
     * Sets the number of minutes a client can remain idle before the server
     * times the connection out. A value of 0 or below indicates that
     * connections will not be timed out.
     *
     * @param idleTimeoutMinutes The number of minutes a client can remain idle
     * before the server times the connection out.
     */
    public void setIdleTimeoutMinutes(int idleTimeoutMinutes)
    {
        this.idleTimeoutMinutes = idleTimeoutMinutes;
    }

    //----------------------------------
    //  maxStreamingClients
    //----------------------------------

    private int maxStreamingClients = DEFAULT_MAX_STREAMING_CLIENTS;

    /**
     * Returns the maximum number of clients that will be allowed to establish
     * a streaming HTTP connection with the endpoint.
     *
     * @return The maximum number of clients that will be allowed to establish
     * a streaming HTTP connection with the endpoint.
     */
    public int getMaxStreamingClients()
    {
        return maxStreamingClients;
    }

    /**
     * Sets the maximum number of clients that will be allowed to establish
     * a streaming HTTP connection with the server.
     *
     * @param maxStreamingClients The maximum number of clients that will be allowed
     * to establish a streaming HTTP connection with the server.
     */
    public void setMaxStreamingClients(int maxStreamingClients)
    {
        this.maxStreamingClients = maxStreamingClients;
        canStream = (streamingClientsCount < maxStreamingClients);
    }

    //----------------------------------
    //  streamingClientsCount
    //----------------------------------

    protected int streamingClientsCount;

    /**
     * Returns the the number of clients that are currently in the streaming state.
     *
     * @return The number of clients that are currently in the streaming state.
     */
    public int getStreamingClientsCount()
    {
        return streamingClientsCount;
    }

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @exclude
     * Returns a <code>ConfigMap</code> of endpoint properties that the client
     * needs. This includes properties from <code>super.describeEndpoint</code>
     * and additional <code>BaseHTTPEndpoint</code> specific properties under
     * "properties" key.
     */
    @Override public ConfigMap describeEndpoint()
    {
        // Any future properties that will be needed by the client should be
        // added here.
        return super.describeEndpoint();
    }

    /**
     * Handles HTTP requests targetting this endpoint.
     * Two types or requests are supported. If the request is a regular request-response AMF/AMFX
     * message it is handled by the base logic in BaseHTTPEndpoint.service. However, if it is a
     * request to open a streaming HTTP connection to the client this endpoint performs some
     * validation checks and then holds the connection open to stream data back to the client
     * over.
     *
     * @param req The original servlet request
     * @param res The active servlet response
     */
    @Override public void service(HttpServletRequest req, HttpServletResponse res)
    {
        String command = req.getParameter(COMMAND_PARAM_NAME);
        if (command != null)
            serviceStreamingRequest(req, res);
        else // Let BaseHTTPEndpoint logic handle regular request-response messaging.
            super.service(req, res);
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     * If the message has MPI enabled, this method adds all the needed performance
     * headers to the message.
     *
     * @param message Message to add performance headers to.
     */
    protected void addPerformanceInfo(Message message)
    {
        // If MPI is not enabled, simply return.
        MessagePerformanceInfo mpiOriginal = MessagePerformanceUtils.getMPII(message);
        if (mpiOriginal == null)
            return;

        // Otherwise, move the MPII object of the queued message to be
        // the MPIP object of the outgoing message.
        MessagePerformanceInfo mpip;
        mpip = (MessagePerformanceInfo)mpiOriginal.clone();
        try
        {
            // Set the original message info as the pushed causer info.
            MessagePerformanceUtils.setMPIP(message, mpip);
            MessagePerformanceUtils.setMPII(message, null);
        }
        catch (Exception e)
        {
            if (Log.isDebug())
                log.debug("MPI exception while streaming the message: " + e.toString());
        }

        // Overhead only used when MPI is enabled for sizing
        long serializationOverhead;
        MessagePerformanceInfo mpio;
        mpio = new MessagePerformanceInfo();
        if (mpip.recordMessageTimes)
        {
            mpio.sendTime = System.currentTimeMillis();
            mpio.infoType = "OUT";
        }
        mpio.pushedFlag = true;
        MessagePerformanceUtils.setMPIO(message, mpio);

        // If MPI sizing information is enabled serialize again so that we know size
        if (mpip.recordMessageSizes)
        {
            try
            {
                // Each subclass serializes the message in their own format to
                // get the message size for the MPIO.
                serializationOverhead = System.currentTimeMillis();
                mpio.messageSize = getMessageSizeForPerformanceInfo(message);

                // Set serialization overhead to the time calculated during serialization above
                if (mpip.recordMessageTimes)
                {
                    serializationOverhead = System.currentTimeMillis() - serializationOverhead;
                    mpip.addToOverhead(serializationOverhead);
                    mpiOriginal.addToOverhead(serializationOverhead);
                    mpio.sendTime = System.currentTimeMillis();
                }
            }
            catch(Exception e)
            {
                log.debug("MPI exception while streaming the message: " + e.toString());
            }
        }
    }

    /**
     * Used internally for performance information gathering; not intended for
     * public use. The default implementation of this method returns zero.
     * Subclasses should overwrite if they want to accurately report message
     * size information in performance information gathering.
     *
     * @param message Message to get the size for.
     *
     * @return The size of the message after message is serialized.
     */
    protected long getMessageSizeForPerformanceInfo(Message message)
    {
        return 0;
    }

    /**
     * This streaming endpoint does not support polling clients.
     *
     * @param flexClient The FlexClient that issued the poll request.
     * @param pollCommand The poll command from the client.
     * @return The flush info used to build the poll response.
     */
    @Override protected FlushResult handleFlexClientPoll(FlexClient flexClient, CommandMessage pollCommand)
    {
        MessageException me = new MessageException();
        me.setMessage(POLL_NOT_SUPPORTED_MESSAGE);
        me.setDetails(POLL_NOT_SUPPORTED_MESSAGE);
        me.setCode(POLL_NOT_SUPPORTED_CODE);
        throw me;
    }

    /**
     * Handles streaming connection open command sent by the FlexClient.
     *
     * @param req The <code>HttpServletRequest</code> to service.
     * @param res The <code>HttpServletResponse</code> to be used in case an error
     * has to be sent back.
     * @param flexClient FlexClient that requested the streaming connection.
     */
    protected void handleFlexClientStreamingOpenRequest(HttpServletRequest req, HttpServletResponse res, FlexClient flexClient)
    {
        FlexSession session = FlexContext.getFlexSession();
        if (canStream && session.canStream)
        {
            // If canStream/session.canStream is true it means we currently have
            // less than the max number of allowed streaming threads, per endpoint/session.

            // We need to protect writes/reads to the stream count with the endpoint's lock.
            // Also, we have to be careful to handle the case where two threads get to this point when only
            // one streaming spot remains; one thread will win and the other needs to fault.
            boolean thisThreadCanStream;
            synchronized (lock)
            {
                ++streamingClientsCount;
                if (streamingClientsCount == maxStreamingClients)
                {
                    thisThreadCanStream = true; // This thread got the last spot.
                    canStream = false;
                }
                else if (streamingClientsCount > maxStreamingClients)
                {
                    thisThreadCanStream = false; // This thread was beaten out for the last spot.
                    --streamingClientsCount; // Decrement the count because we're not going to grant the streaming right to the client.
                }
                else
                {
                    // We haven't hit the limit yet, allow this thread to stream.
                    thisThreadCanStream = true;
                }
            }

            // If the thread cannot wait due to endpoint streaming connection
            // limit, inform the client and return.
            if (!thisThreadCanStream)
            {
                if (Log.isError())
                    log.error("Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '"
                            + flexClient.getId() + "' because " + MAX_STREAMING_CLIENTS + " limit of '"
                            + maxStreamingClients + "' has been reached.");
                try
                {
                    // Return an HTTP status code 400.
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
                catch (IOException ignore)
                {}
                return;
            }

            // Setup for specific user agents.
            byte[] kickStartBytesToStream = null;
            String userAgentValue = req.getHeader(UserAgentManager.USER_AGENT_HEADER_NAME);
            UserAgentSettings agentSettings = userAgentManager.match(userAgentValue);
            if (agentSettings != null)
            {
                synchronized (session)
                {
                    session.maxConnectionsPerSession = agentSettings.getMaxStreamingConnectionsPerSession();
                }
                int kickStartBytes = agentSettings.getKickstartBytes();
                if (kickStartBytes > 0)
                {
                    // Determine the minimum number of actual bytes that need to be sent to
                    // kickstart, taking into account transfer-encoding overhead.
                    try
                    {
                        int chunkLengthHeaderSize = Integer.toHexString(kickStartBytes).getBytes("ASCII").length;
                        int chunkOverhead = chunkLengthHeaderSize + 4; // 4 for the 2 wrapping CRLF tokens.
                        int minimumKickstartBytes = kickStartBytes - chunkOverhead;
                        kickStartBytesToStream = new byte[(minimumKickstartBytes > 0) ? minimumKickstartBytes :
                                kickStartBytes];
                    }
                    catch (UnsupportedEncodingException ignore)
                    {
                        kickStartBytesToStream = new byte[kickStartBytes];
                    }
                    Arrays.fill(kickStartBytesToStream, NULL_BYTE);
                }
            }

            // Now, check with the session before granting the streaming connection.
            synchronized(session)
            {
                ++session.streamingConnectionsCount;
                if (session.streamingConnectionsCount == session.maxConnectionsPerSession)
                {
                    thisThreadCanStream = true; // This thread got the last spot in the session.
                    session.canStream = false;
                }
                else if (session.streamingConnectionsCount > session.maxConnectionsPerSession)
                {
                    thisThreadCanStream = false; // This thread was beaten out for the last spot.
                    --session.streamingConnectionsCount;
                    synchronized(lock)
                    {
                        // Decrement the endpoint count because we're not going to grant the streaming right to the client.
                        --streamingClientsCount;
                    }
                }
                else
                {
                    // We haven't hit the limit yet, allow this thread to stream.
                    thisThreadCanStream = true;
                }
            }

            // If the thread cannot wait due to session streaming connection
            // limit, inform the client and return.
            if (!thisThreadCanStream)
            {
                if (Log.isInfo())
                    log.info("Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '"
                            + flexClient.getId() + "' because " + UserAgentManager.MAX_STREAMING_CONNECTIONS_PER_SESSION + " limit of '" + session.maxConnectionsPerSession
                            + ((agentSettings != null) ? "' for user-agent '" + agentSettings.getMatchOn() + "'" : "") +  " has been reached." );
                try
                {
                 // Return an HTTP status code 400.
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
                catch (IOException ignore)
                {
                    // NOWARN
                }
                return;
            }

            Thread currentThread = Thread.currentThread();
            String threadName = currentThread.getName();
            EndpointPushNotifier notifier = null;
            boolean suppressIOExceptionLogging = false; // Used to suppress logging for IO exception.
            try
            {
                currentThread.setName(threadName + STREAMING_THREAD_NAME_EXTENSION);

                // Open and commit response headers and get output stream.
                if (addNoCacheHeaders)
                    addNoCacheHeaders(req, res);
                res.setContentType(getResponseContentType());
                res.setHeader("Connection", "close");
                res.setHeader("Transfer-Encoding", "chunked");
                ServletOutputStream os = res.getOutputStream();
                res.flushBuffer();

                // If kickstart-bytes are specified, stream them.
                if (kickStartBytesToStream != null)
                {
                    if (Log.isDebug())
                        log.debug("Endpoint with id '" + getId() + "' is streaming " + kickStartBytesToStream.length
                                + " bytes (not counting chunk encoding overhead) to kick-start the streaming connection for FlexClient with id '"
                                + flexClient.getId() + "'.");

                    streamChunk(kickStartBytesToStream, os, res);
                }

                // Setup serialization and type marshalling contexts
                setThreadLocals();

                // Activate streaming helper for this connection.
                // Watch out for duplicate stream issues.
                try
                {
                    notifier = new EndpointPushNotifier(this, flexClient);
                }
                catch (MessageException me)
                {
                    if (me.getNumber() == 10033) // It's a duplicate stream request from the same FlexClient. Leave the current stream in place and fault this.
                    {
                        if (Log.isWarn())
                            log.warn("Endpoint with id '" + getId() + "' received a duplicate streaming connection request from, FlexClient with id '"
                                    + flexClient.getId() + "'. Faulting request.");

                        // Rollback counters and send an error response.
                        synchronized (lock)
                        {
                            --streamingClientsCount;
                            canStream = (streamingClientsCount < maxStreamingClients);
                            synchronized (session)
                            {
                                --session.streamingConnectionsCount;
                                session.canStream = (session.streamingConnectionsCount < session.maxConnectionsPerSession);
                            }
                        }
                        try
                        {
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        }
                        catch (IOException ignore)
                        {
                            // NOWARN
                        }
                        return; // Exit early.
                    }
                }
                notifier.setIdleTimeoutMinutes(idleTimeoutMinutes);
                notifier.setLogCategory(getLogCategory());
                monitorTimeout(notifier);
                currentStreamingRequests.put(notifier.getNotifierId(), notifier);

                // Push down an acknowledgement for the 'connect' request containing the unique id for this specific stream.
                AcknowledgeMessage connectAck = new AcknowledgeMessage();
                connectAck.setBody(notifier.getNotifierId());
                connectAck.setCorrelationId(BaseStreamingHTTPEndpoint.OPEN_COMMAND);
                ArrayList toPush = new ArrayList(1);
                toPush.add(connectAck);
                streamMessages(toPush, os, res);

                // Output session level streaming count.
                if (Log.isDebug())
                    Log.getLogger(FlexSession.FLEX_SESSION_LOG_CATEGORY).info("Number of streaming clients for FlexSession with id '"+ session.getId() +"' is " + session.streamingConnectionsCount + ".");

                // Output endpoint level streaming count.
                if (Log.isDebug())
                    log.debug("Number of streaming clients for endpoint with id '"+ getId() +"' is " + streamingClientsCount + ".");

                // And cycle in a wait-notify loop with the aid of the helper until it
                // is closed, we're interrupted or the act of streaming data to the client fails.
                while (!notifier.isClosed())
                {
                    // Synchronize on pushNeeded which is our condition variable.
                    synchronized (notifier.pushNeeded)
                    {
                        try
                        {
                            // Drain any messages that might have been accumulated
                            // while the previous drain was being processed.
                            streamMessages(notifier.drainMessages(), os, res);

                            notifier.pushNeeded.wait(serverToClientHeartbeatMillis);

                            List messages = notifier.drainMessages();
                            // If there are no messages to send to the client, send an null
                            // byte as a heartbeat to make sure the client is still valid.
                            if (messages == null && serverToClientHeartbeatMillis > 0)
                            {
                                try
                                {
                                    os.write(NULL_BYTE);
                                    res.flushBuffer();
                                }
                                catch (IOException e)
                                {
                                    if (Log.isWarn())
                                        log.warn("Endpoint with id '" + getId() + "' is closing the streaming connection to FlexClient with id '"
                                                + flexClient.getId() + "' because endpoint encountered a socket write error" +
                                        ", possibly due to an unresponsive FlexClient.");
                                    break; // Exit the wait loop.
                                }
                            }
                            // Otherwise stream the messages to the client.
                            else
                            {
                                // Update the last time notifier was used to drain messages.
                                // Important for idle timeout detection.
                                notifier.updateLastUse();

                                streamMessages(messages, os, res);
                            }
                        }
                        catch (InterruptedException e)
                        {
                            if (Log.isWarn())
                                log.warn("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' has been interrupted and the streaming connection will be closed.");
                            os.close();
                            break; // Exit the wait loop.
                        }
                    }
                    // Update the FlexClient last use time to prevent FlexClient from
                    // timing out when the client is still subscribed. It is important
                    // to do this outside synchronized(notifier.pushNeeded) to avoid
                    // thread deadlock!
                    flexClient.updateLastUse();
                }
                if (Log.isDebug())
                    log.debug("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' is releasing connection and returning to the request handler pool.");
                suppressIOExceptionLogging = true;
                // Terminate the response.
                streamChunk(null, os, res);
            }
            catch (IOException e)
            {
                if (Log.isWarn() && !suppressIOExceptionLogging)
                    log.warn("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' is closing connection due to an IO error.", e);
            }
            finally
            {
                currentThread.setName(threadName);

                // We're done so decrement the counts for streaming threads,
                // and update the canStream flag if necessary.
                synchronized (lock)
                {
                    --streamingClientsCount;
                    canStream = (streamingClientsCount < maxStreamingClients);
                    synchronized (session)
                    {
                        --session.streamingConnectionsCount;
                        session.canStream = (session.streamingConnectionsCount < session.maxConnectionsPerSession);
                    }
                }

                if (notifier != null && currentStreamingRequests != null)
                {
                    currentStreamingRequests.remove(notifier.getNotifierId());
                    notifier.close();
                }

                // Output session level streaming count.
                if (Log.isDebug())
                    Log.getLogger(FlexSession.FLEX_SESSION_LOG_CATEGORY).info("Number of streaming clients for FlexSession with id '"+ session.getId() +"' is " + session.streamingConnectionsCount + ".");

                // Output endpoint level streaming count.
                if (Log.isDebug())
                    log.debug("Number of streaming clients for endpoint with id '"+ getId() +"' is " + streamingClientsCount + ".");
            }
        }
        // Otherwise, client's streaming connection open request could not be granted.
        else
        {
            if (Log.isError())
            {
                String logString = null;
                if (!canStream)
                {
                    logString = "Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '"
                    + flexClient.getId() + "' because " + MAX_STREAMING_CLIENTS + " limit of '"
                    + maxStreamingClients + "' has been reached.";
                }
                else if (!session.canStream)
                {
                    logString = "Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '"
                    + flexClient.getId() + "' because " + UserAgentManager.MAX_STREAMING_CONNECTIONS_PER_SESSION + " limit of '"
                    + session.maxConnectionsPerSession + "' has been reached.";
                }
                if (logString != null)
                    log.error(logString);
            }

            try
            {
                // Return an HTTP status code 400 to indicate that client request can't be processed.
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException ignore)
            {}
        }
    }

    /**
     * Handles streaming connection close command sent by the FlexClient.
     *
     * @param req The <code>HttpServletRequest</code> to service.
     * @param res The <code>HttpServletResponse</code> to be used in case an error
     * has to be sent back.
     * @param flexClient FlexClient that requested the streaming connection.
     * @param streamId The id for the stream to close.
     */
    protected void handleFlexClientStreamingCloseRequest(HttpServletRequest req, HttpServletResponse res, FlexClient flexClient, String streamId)
    {
        if (streamId != null)
        {
            EndpointPushNotifier notifier = (EndpointPushNotifier)flexClient.getEndpointPushHandler(getId());
            if ((notifier != null) && notifier.getNotifierId().equals(streamId))
                notifier.close();
        }
    }

    /**
     * Service streaming connection commands.
     *
     * @param req The <code>HttpServletRequest</code> to service.
     * @param res The <code>HttpServletResponse</code> to be used in case an error
     * has to be sent back.
     */
    protected void serviceStreamingRequest(HttpServletRequest req, HttpServletResponse res)
    {
        // If this is a request for a streaming connection, make sure it's for a valid FlexClient
        // and that the FlexSession doesn't already have a streaming connection open.
        // Streaming requests are POSTs (to help prevent the possibility of caching) that carry the
        // following parameters:
        // command - Indicating a custom command for the endpoint; currently 'open' to request a new
        //           streaming connection be opened, and 'close' to request the streaming connection
        //           to close.
        // version - Indicates the streaming connection 'version' to use; it's here for backward comp. support
        //           if we need to change how commands are handled in a future product release.
        // DSId - The FlexClient id value that uniquely identifies the swf making the request.
        String command = req.getParameter(COMMAND_PARAM_NAME);

        // Only HTTP 1.1 is supported, disallow HTTP 1.0.
        if (req.getProtocol().equals(HTTP_1_0))
        {
            if (Log.isError())
                log.error("Endpoint with id '" + getId() + "' cannot service the streaming request made with " +
                " HTTP 1.0. Only HTTP 1.1 is supported.");

            try
            {
                // Return an HTTP status code 400 to indicate that the client's request was syntactically invalid (bad command).
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException ignore)
            {}
            return; // Abort further server processing.
        }

        if (!(command.equals(OPEN_COMMAND) || command.equals(CLOSE_COMMAND)))
        {
            if (Log.isError())
                log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as the supplied command '"
                        + command + "' is invalid.");

            try
            {
                // Return an HTTP status code 400 to indicate that the client's request was syntactically invalid (bad command).
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException ignore)
            {}
            return; // Abort further server processing.
        }

        String flexClientId = req.getParameter(Message.FLEX_CLIENT_ID_HEADER);
        if (flexClientId == null)
        {
            if (Log.isError())
                log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as no FlexClient id"
                        + " has been supplied in the request.");

            try
            {
                // Return an HTTP status code 400 to indicate that the client's request was syntactically invalid (missing id).
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException ignore)
            {}
            return; // Abort further server processing.
        }

        // Validate that the provided FlexClient id exists and is associated with the current session.
        // We don't do this validation with CLOSE_COMMAND because CLOSE_COMMAND can come in on a
        // different session. For example, when the session expires due to timeout, the streaming client
        // using that session sends a CLOSE_COMMAND on a new session to let the server know to clean client's
        // corresponding server constructs. In that case, server already knows that session has expired so
        // we can simply omit this validation.
        FlexClient flexClient = null;
        List flexClients = FlexContext.getFlexSession().getFlexClients();
        boolean validFlexClientId = false;
        for (Iterator iter = flexClients.iterator(); iter.hasNext();)
        {
            flexClient = (FlexClient)iter.next();
            if (flexClient.getId().equals(flexClientId) && flexClient.isValid())
            {
                validFlexClientId = true;
                break;
            }
        }
        if (!command.equals(CLOSE_COMMAND) && !validFlexClientId)
        {
            if (Log.isError())
                log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as either the supplied"
                        + " FlexClient id '" + flexClientId + " is not valid, or the FlexClient with that id is not valid.");

            try
            {
                // Return an HTTP status code 400 to indicate that the client's request was syntactically invalid (invalid id).
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException ignore)
            {}
            return; // Abort further server processing.
        }

        if (flexClient != null)
        {
            if (command.equals(OPEN_COMMAND))
                handleFlexClientStreamingOpenRequest(req, res, flexClient);
            else if (command.equals(CLOSE_COMMAND))
                handleFlexClientStreamingCloseRequest(req, res, flexClient, req.getParameter(STREAM_ID_PARAM_NAME));
        }
    }

    /**
     * Helper method to write a chunk of bytes to the output stream in an HTTP
     * "Transfer-Encoding: chunked" format.
     * If the bytes array is null or empty, a terminal chunk will be written to
     * signal the end of the response.
     * Once the chunk is written to the output stream, the stream will be flushed immediately (no buffering).
     *
     * @param bytes The array of bytes to write as a chunk in the response; or if null, the signal to write the final chunk to complete the response.
     * @param os The output stream the chunk will be written to.
     * @param response The HttpServletResponse, used to flush the chunk to the client.
     *
     * @throws IOException if writing the chunk to the output stream fails.
     */
    protected void streamChunk(byte[] bytes, ServletOutputStream os, HttpServletResponse response) throws IOException
    {
        if ((bytes != null) && (bytes.length > 0))
        {
            byte[] chunkLength = Integer.toHexString(bytes.length).getBytes("ASCII");
            os.write(chunkLength);
            os.write(CRLF_BYTES);
            os.write(bytes);
            os.write(CRLF_BYTES);
            response.flushBuffer();
        }
        else // Send final 'EOF' chunk for the response.
        {
            os.write(ZERO_BYTE);
            os.write(CRLF_BYTES);
            response.flushBuffer();
        }
    }

    /**
     * Helper method invoked by the endpoint request handler thread cycling in wait-notify.
     * Serializes messages and streams each to the client as a response chunk using streamChunk().
     *
     * @param messages The messages to serialize and push to the client.
     * @param os The output stream the chunk will be written to.
     * @param response The HttpServletResponse, used to flush the chunk to the client.
     */
    protected abstract void streamMessages(List messages, ServletOutputStream os, HttpServletResponse response) throws IOException;

    //--------------------------------------------------------------------------
    //
    // Private methods.
    //
    //--------------------------------------------------------------------------

    /**
     * Utility method used at EndpointPushNotifier construction to monitor it for timeout.
     *
     * @param notifier The EndpointPushNotifier to monitor.
     */
    private void monitorTimeout(EndpointPushNotifier notifier)
    {
        if (pushNotifierTimeoutManager != null)
            pushNotifierTimeoutManager.scheduleTimeout(notifier);
    }
}