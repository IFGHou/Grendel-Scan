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
package flex.messaging.cluster;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.JChannelFactory;
import org.jgroups.Message;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.w3c.dom.Element;

import flex.messaging.FlexContext;
import flex.messaging.log.Log;
import flex.messaging.services.Service;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;

/**
 * @exclude A collection of ClusterNode instances scoped to a specific service destination.
 * 
 * @author neville
 */
public class JGroupsCluster extends Cluster implements RequestHandler
{
    private final MessageDispatcher broadcastDispatcher;
    private final List broadcastHandlers;
    private final JChannel clusterChannel;
    private final ClusterManager clusterManager;
    private final ClusterMembershipListener clusterMembershipListener;
    private final Map clusterNodes;
    private final String clusterId;

    public JGroupsCluster(ClusterManager clusterManager, String clusterId, Element props)
    {
        this.broadcastHandlers = new ArrayList();
        this.clusterManager = clusterManager;
        this.clusterMembershipListener = new ClusterMembershipListener(this);
        this.clusterNodes = Collections.synchronizedMap(new HashMap());
        this.clusterId = clusterId;

        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Joining cluster with id: " + clusterId);

        configureBroadcastHandlers();

        try
        {
            JChannelFactory channelFactory = new JChannelFactory(props);
            clusterChannel = (JChannel) channelFactory.createChannel();
            // Disable delivery of messages we send to ourself.
            clusterChannel.setOpt(Channel.LOCAL, Boolean.FALSE);
            // clusterChannel.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);
            broadcastDispatcher = new MessageDispatcher(clusterChannel, null, clusterMembershipListener, this);
            clusterChannel.connect(clusterId);
        }
        catch (ChannelException cex)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10200, new Object[] { clusterId, props });
            cx.setRootCause(cex);
            throw cx;
        }
    }

    /**
     * Retrieve a List of Maps, where each Map contains channel id keys mapped to endpoint URLs. There is exactly one endpoint URL for each channel id. This List represents all of the known endpoint
     * URLs for all of the channels in the Cluster.
     */
    @Override
    public List getAllEndpoints(String serviceType, String destName)
    {
        List channelToEndpointMaps = new ArrayList();

        synchronized (clusterNodes)
        {
            for (Iterator iter = clusterNodes.keySet().iterator(); iter.hasNext();)
            {
                Address addr = (Address) iter.next();
                if (!clusterMembershipListener.isZombie(addr))
                {
                    ClusterNode node = (ClusterNode) clusterNodes.get(addr);
                    Map nodeEndpoints = node.getEndpoints(serviceType, destName);

                    // Dedupe channel endpoints
                    if (nodeEndpoints.size() > 0)
                    {
                        for (Iterator iter1 = channelToEndpointMaps.iterator(); iter1.hasNext();)
                        {
                            Map nodeEndpoints2 = (Map) iter1.next();
                            for (Iterator iter2 = nodeEndpoints2.values().iterator(); iter2.hasNext();)
                            {
                                String endpointUrl = (String) iter2.next();
                                if (nodeEndpoints.containsValue(endpointUrl))
                                {
                                    // Remove the duplicate
                                    for (Iterator iter3 = nodeEndpoints.values().iterator(); iter3.hasNext();)
                                    {
                                        String endpointUrl2 = (String) iter3.next();
                                        if (endpointUrl2.equals(endpointUrl))
                                            iter3.remove();
                                    }
                                }
                            }
                        }
                        if (nodeEndpoints.size() > 0)
                        {
                            channelToEndpointMaps.add(nodeEndpoints);
                        }
                    }
                }
            }
        }
        return channelToEndpointMaps;
    }

    /**
     * Shutdown the cluster.
     */
    @Override
    public void destroy()
    {
        try
        {
            clusterChannel.close();
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Clusters broadcast messages across their physical nodes, and when they receive those messages they locate a BroadcastHandler capable of handling the broadcast. This method configures those
     * handlers.
     */
    void configureBroadcastHandlers()
    {
        broadcastHandlers.add(new RemoteEndpointHandler());
        broadcastHandlers.add(new ServiceOperationHandler());
    }

    /**
     * This method is called in response to a ClusterMembershipListener's notification that an Address has joined the Cluster.
     */
    void addClusterNode(Address address)
    {
        // we avoid adding the node until it has channel and endpoint
        // information which interests us, but we log the fact that the
        // member was discovered
        if (Log.isDebug())
        {
            Log.getLogger(LOG_CATEGORY).debug("Cluster node from address " + address + " joined the cluster for " + clusterId);
        }
    }

    /**
     * In response to a ClusterMembershipListener's notification that an Address has abandoned the Cluster, remove the node and avoid using its channel and endpoint information in the future.
     */
    void removeClusterNode(Address address)
    {
        clusterNodes.remove(address);

        sendRemoveNodeListener(address);

        if (Log.isDebug())
        {
            Log.getLogger(LOG_CATEGORY).debug("Cluster node from address " + address + " abandoned the cluster for " + clusterId);
        }
    }

    /**
     * Return the physical address of the local ClusterNode.
     */
    Address getJGroupsLocalAddress()
    {
        return clusterChannel.getLocalAddress();
    }

    @Override
    public Object getLocalAddress()
    {
        return getJGroupsLocalAddress();
    }

    /**
     * Add a local endpoint URL for a local channel. After doing so, broadcast the information to peers so that they will be aware of the URL.
     */
    @Override
    public void addLocalEndpointForChannel(String serviceType, String destName, String channelId, String endpointUrl, int endpointPort)
    {
        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Adding clustered destination endpoint. cluster-id=" + clusterId + " destination=" + destName + " channelId=" + channelId + " endpoint url=" + endpointUrl + " endpointPort=" + endpointPort);

        Address myAddr = getJGroupsLocalAddress();
        ClusterNode myNode = getNodeForAddress(myAddr);
        endpointUrl = canonicalizeUrl(channelId, endpointUrl, endpointPort, myNode);
        myNode.addEndpoint(serviceType, destName, channelId, endpointUrl);
        broadcastClusterOperation("addEndpointForChannel", serviceType, destName, channelId, endpointUrl, null);
    }

    /**
     * This method is invoked by the RemoteEndpointEndpointHandler when it receives a message from a remote peer. The remote peer has broadcast some channel id and endpoint URL information, which we
     * add to our local list of such information, and we also tell the sender about our own channel and endpoint information.
     */
    void addEndpointForChannel(Address address, String serviceType, String destName, String channelId, String endpointUrl)
    {
        ClusterNode node = getNodeForAddress(address);
        if (!node.containsEndpoint(serviceType, destName, channelId, endpointUrl))
        {
            node.addEndpoint(serviceType, destName, channelId, endpointUrl);
            broadcastMyEndpoints(address);
        }
    }

    /**
     * Tell a specific remote peer about our local channel and endpoint URL information.
     */
    void broadcastMyEndpoints(Address address)
    {
        // tell the new node about my channel id -> endpoint urls
        Vector destination = new Vector();
        destination.add(address);
        ClusterNode myNode = getNodeForAddress(clusterChannel.getLocalAddress());
        Map destKeyToChannelMap = myNode.getDestKeyToChannelMap();
        synchronized (destKeyToChannelMap)
        {
            for (Iterator destIt = destKeyToChannelMap.keySet().iterator(); destIt.hasNext();)
            {
                String destKey = (String) destIt.next();
                int ix = destKey.indexOf(":");
                String serviceType = destKey.substring(0, ix);
                String destName = destKey.substring(ix + 1);
                Map channelEndpoints = myNode.getEndpoints(serviceType, destName);
                for (Iterator iter = channelEndpoints.keySet().iterator(); iter.hasNext();)
                {
                    String channelId = (String) iter.next();
                    String endpointUrl = (String) channelEndpoints.get(channelId);
                    broadcastClusterOperation("addEndpointForChannel", serviceType, destName, channelId, endpointUrl, destination);
                }
            }
        }
    }

    /**
     * Broadcast a cluster-related operation, such as channel and endpoint information, out to the remote peers.
     */
    void broadcastClusterOperation(String clusterOperation, String serviceType, String destName, String channelId, String endpointUrl, Vector destinations)
    {
        List operationInfo = new ArrayList();
        operationInfo.add(serviceType);
        operationInfo.add(destName);
        operationInfo.add(channelId);
        operationInfo.add(endpointUrl);
        broadcastOperation(RemoteEndpointHandler.class.getName(), clusterOperation, operationInfo, destinations);
    }

    /**
     * Broadcast a service-related operation, which usually includes a Message as a method parameter. This method allows a local service to process a Message and then send the Message to the services
     * on all peer nodes so that they may perform the same processing.
     */
    @Override
    public void broadcastServiceOperation(String serviceOperation, Object[] params)
    {
        ArrayList operationInfo = new ArrayList();
        operationInfo.addAll(Arrays.asList(params));
        broadcastOperation(ServiceOperationHandler.class.getName(), serviceOperation, operationInfo, null);
    }

    /**
     * Send a service-related operation in point-to-point fashion to one and only one member of the cluster. This is similar to the broadcastServiceOperation except that this invocation is sent to the
     * first node among the cluster members that does not have the local node's address.
     */
    @Override
    public void sendPointToPointServiceOperation(String serviceOperation, Object[] params, Object targetAddress)
    {
        ArrayList operationInfo = new ArrayList();
        operationInfo.addAll(Arrays.asList(params));
        // for point to point invocations, add the sender's address as a param
        operationInfo.add(getJGroupsLocalAddress());
        Vector targetDestination = new Vector();
        if (targetAddress != null)
        {
            targetDestination.add(targetAddress);
        }
        else
        {
            for (int i = 0; i < clusterChannel.getView().getMembers().size(); i++)
            {
                Address a = clusterChannel.getView().getMembers().get(i);
                if (!a.equals(getJGroupsLocalAddress()))
                {
                    targetDestination.add(a);
                    break;
                }
            }
        }
        broadcastOperation(ServiceOperationHandler.class.getName(), serviceOperation, operationInfo, targetDestination);
    }

    /**
     * Returns the Address instances for each of the servers in the cluster.
     */
    @Override
    public List getMemberAddresses()
    {
        return clusterChannel.getView().getMembers();
    }

    /**
     * This is the core broadcast operation.
     */
    private void broadcastOperation(String handlerClass, String operationName, List operationParams, Vector destinations)
    {
        try
        {
            operationParams.add(0, handlerClass);
            operationParams.add(1, operationName);
            Message operationMessage = new Message(null, getJGroupsLocalAddress(), (Serializable) operationParams);
            // null destinations implies a broadcast to all members (but ourself cause local is off)
            broadcastDispatcher.castMessage(destinations, operationMessage, GroupRequest.GET_NONE, 0);
        }
        catch (IllegalArgumentException iae)
        {
            String message = iae.getMessage();
            String notSerializableType = null;
            if ((message != null) && message.startsWith("java.io.NotSerializableException"))
                notSerializableType = message.substring(message.indexOf(": ") + 2);

            if (notSerializableType != null)
            {
                ClusterException cx = new ClusterException();
                cx.setMessage(10212, new Object[] { clusterId, notSerializableType });
                cx.setRootCause(iae);
                throw cx;
            }
            else
            {
                ClusterException cx = new ClusterException();
                cx.setMessage(10204, new Object[] { clusterId });
                cx.setRootCause(iae);
                throw cx;
            }
        }
        catch (Exception e)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10204, new Object[] { clusterId });
            cx.setRootCause(e);
            throw cx;
        }
    }

    /**
     * Receive a message broadcast by another peer.
     */
    @Override
    public Object handle(Message msg)
    {
        if (msg.getSrc() != getJGroupsLocalAddress())
        {
            List operationInfo = (List) msg.getObject();
            String handlerClass = (String) operationInfo.get(0);
            String operationName = (String) operationInfo.get(1);
            try
            {
                // We only have the message broker, but make that available to
                // the handler thread.
                FlexContext.setThreadLocalObjects(null, null, clusterManager.getMessageBroker(), null, null, null);
                for (Iterator iter = broadcastHandlers.iterator(); iter.hasNext();)
                {
                    BroadcastHandler handler = (BroadcastHandler) iter.next();
                    if (handler.getClass().getName().equals(handlerClass) && handler.isSupportedOperation(operationName))
                    {
                        handler.handleBroadcast(msg.getSrc(), operationInfo.subList(1, operationInfo.size()));
                        break;
                    }
                }
            }
            finally
            {
                FlexContext.clearThreadLocalObjects();
            }
        }
        return null;
    }

    /**
     * Locate the ClusterNode mapped to the provided physical address. If no ClusterNode exists for the address, create one.
     */
    private ClusterNode getNodeForAddress(Address addr)
    {
        synchronized (clusterNodes)
        {
            ClusterNode node = (ClusterNode) clusterNodes.get(addr);
            if (node == null)
            {
                node = new ClusterNode(addr);
                clusterNodes.put(addr, node);
            }
            return node;
        }
    }

    /**
     * The endpoint in the configuration may may not be fully-qualified, but it must be before it is added to a ClusterNode.
     */
    private String canonicalizeUrl(String channelId, String endpointUrl, int endpointPort, ClusterNode myNode)
    {
        if (endpointUrl.startsWith("/"))
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10203, new Object[] { channelId });
            throw cx;
        }

        if (endpointUrl.indexOf(":///") != -1)
        {
            endpointUrl = StringUtils.substitute(endpointUrl, ":///", "://" + myNode.getHost() + "/");
        }

        if (endpointPort != 0 && endpointUrl.indexOf("" + endpointPort) == -1)
        {
            StringBuffer sb = new StringBuffer(endpointUrl);
            sb.insert(endpointUrl.indexOf("/", endpointUrl.indexOf("://") + 3), ":" + endpointPort);
            endpointUrl = sb.toString();
        }

        return endpointUrl;
    }

    /**
     * This BroadcastHandler implementation handles cluster operations broadcast by a Cluster.
     */
    class RemoteEndpointHandler implements BroadcastHandler
    {
        @Override
        public void handleBroadcast(Object sender, List params)
        {
            // note: the operation name is at index 0, and we do not need it in this handler
            // the serviceType, destName, channel id and endpoint url are expected to be at indexes 1, 2, 3, and 4 respectively
            addEndpointForChannel((Address) sender, (String) params.get(1), (String) params.get(2), (String) params.get(3), (String) params.get(4));
        }

        @Override
        public boolean isSupportedOperation(String name)
        {
            if (name.equals("addEndpointForChannel"))
            {
                return true;
            }
            return false;
        }
    }

    /**
     * This BroadcastHandler implementation handles service operations broadcast by a Cluster.
     */
    class ServiceOperationHandler implements BroadcastHandler
    {
        String[] supportedOperations = new String[] { "pushMessageFromPeer", "peerSyncAndPush", "requestAdapterState", "receiveAdapterState", "sendSubscriptions", "receiveSubscriptions", "subscribeFromPeer", "pushMessageFromPeerToPeer",
                        "peerSyncAndPushOneToPeer" };

        @Override
        public void handleBroadcast(Object sender, List params)
        {
            try
            {
                // param 0 is the method name
                String serviceType = (String) params.get(1);
                // In this case, the destName is not used because the dest is in the
                // message. It is here just to be consistent with the other methods and
                // in case we need to send something to a destination without a message.
                // String destName = (String) params.get(2);
                Service svc = clusterManager.getMessageBroker().getServiceByType(serviceType);
                if (svc != null)
                {
                    String methodName = (String) params.get(0);
                    Object[] paramValues = params.subList(3, params.size()).toArray();
                    Method[] svcMethods = svc.getClass().getMethods();
                    // note: in order to avoid requiring services to have specific formal
                    // types on methods (superclasses aren't honored by reflection) we just
                    // grab the first method we see with the correct name
                    // -- intended for internal use only
                    for (int i = 0; i < svcMethods.length; i++)
                    {
                        if (svcMethods[i].getName().equals(methodName))
                        {
                            svcMethods[i].invoke(svc, paramValues);
                            break;
                        }
                    }
                }
            }
            catch (InvocationTargetException ite)
            {
                Throwable th = ite.getCause();
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling message pushed from cluster: " + th + StringUtils.NEWLINE + "Exception=" + ExceptionUtil.toString(th));
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10205, new Object[] { clusterId });
                cx.setRootCause(th);
                throw cx;
            }
            catch (Exception e)
            {
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling message pushed from cluster: " + e + StringUtils.NEWLINE + "Exception=" + ExceptionUtil.toString(e));
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10205, new Object[] { clusterId });
                cx.setRootCause(e);
                throw cx;
            }
        }

        @Override
        public boolean isSupportedOperation(String name)
        {
            for (int i = 0; i < supportedOperations.length; i++)
            {
                if (name.equals(supportedOperations[i]))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
