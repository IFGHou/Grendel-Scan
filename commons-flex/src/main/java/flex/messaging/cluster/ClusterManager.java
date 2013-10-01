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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import flex.messaging.config.ClusterSettings;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.util.ClassUtil;

/**
 * @exclude The manager of all clusters defined in services-config.xml, and the broker for the clusters created for clustered destinations.
 * 
 * @author neville
 */
public class ClusterManager
{
    private MessageBroker broker;

    // name=clusterId value=clusterInstance
    private Map clusters;

    private Map clustersForDestination;

    // name=clusterId value=propsFile
    private Map clusterConfig;

    // name=clusterId value=ClusterSettings
    private Map clusterSettings;

    private Map backendSharedForDestination;

    private Cluster defaultCluster;

    private String defaultClusterId;

    public ClusterManager(MessageBroker broker)
    {
        this.broker = broker;
        this.clusters = new HashMap();
        this.clusterConfig = new HashMap();
        this.clusterSettings = new HashMap();
        this.clustersForDestination = new HashMap();
        this.backendSharedForDestination = new HashMap();
    }

    public MessageBroker getMessageBroker()
    {
        return broker;
    }

    public Cluster getDefaultCluster()
    {
        return defaultCluster;
    }

    public String getDefaultClusterId()
    {
        return defaultClusterId;
    }

    public void invokeServiceOperation(String serviceType, String destinationName, String operationName, Object[] params)
    {
        Cluster c = getCluster(serviceType, destinationName);
        ArrayList newParams = new ArrayList(Arrays.asList(params));
        newParams.add(0, serviceType);
        newParams.add(1, destinationName);
        c.broadcastServiceOperation(operationName, newParams.toArray());
    }

    public void invokePeerToPeerOperation(String serviceType, String destinationName, String operationName, Object[] params, Object targetAddress)
    {
        Cluster c = getCluster(serviceType, destinationName);
        ArrayList newParams = new ArrayList(Arrays.asList(params));
        newParams.add(0, serviceType);
        newParams.add(1, destinationName);
        c.sendPointToPointServiceOperation(operationName, newParams.toArray(), targetAddress);
    }

    public boolean isDestinationClustered(String serviceType, String destinationName)
    {
        return getCluster(serviceType, destinationName) != null;
    }

    public boolean isBackendShared(String serviceType, String destinationName)
    {
        String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);

        Boolean shared = (Boolean) backendSharedForDestination.get(destKey);

        if (shared == null)
            return false;

        return shared.booleanValue();
    }

    public List getClusterMemberAddresses(String serviceType, String destinationName)
    {
        Cluster c = getCluster(serviceType, destinationName);
        if (c == null)
            return Collections.EMPTY_LIST;

        return c.getMemberAddresses();
    }

    /*
     * TODO UCdetector: Remove unused code: public void prepareCluster(ClusterSettings settings) { if (settings.getPropsFileName() == null) { ClusterException cx = new ClusterException();
     * cx.setMessage(10201, new Object[] { settings.getClusterName(), settings.getPropsFileName() }); throw cx; }
     * 
     * InputStream propsFile;
     * 
     * try { propsFile = broker.resolveInternalPath(settings.getPropsFileName()); } catch (Throwable t) { ClusterException cx = new ClusterException(); cx.setMessage(10208, new Object[] {
     * settings.getPropsFileName() }); cx.setRootCause(t); throw cx; }
     * 
     * if (propsFile == null) { ClusterException cx = new ClusterException(); cx.setMessage(10208, new Object[] { settings.getPropsFileName() }); throw cx; } else { try { DocumentBuilderFactory
     * factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(false); factory.setValidating(false); DocumentBuilder builder = factory.newDocumentBuilder(); Document doc =
     * builder.parse(propsFile); if (settings.isDefault()) { defaultClusterId = settings.getClusterName(); } clusterConfig.put(settings.getClusterName(), doc.getDocumentElement());
     * clusterSettings.put(settings.getClusterName(), settings); } catch (Exception ex) { ClusterException cx = new ClusterException(); cx.setMessage(10213); cx.setRootCause(ex); throw cx; } }
     * 
     * }
     */

    public Object getLocalAddress(String serviceType, String destinationName)
    {
        Cluster c = getCluster(serviceType, destinationName);
        if (c == null)
            return null;

        return c.getLocalAddress();
    }

    public Cluster getClusterById(String clusterId)
    {
        return (Cluster) clusters.get(clusterId);
    }

    public Cluster getCluster(String serviceType, String destinationName)
    {
        Cluster cluster = null;
        try
        {
            String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);

            cluster = (Cluster) clustersForDestination.get(destKey);

            if (cluster == null)
                cluster = defaultCluster;
        }
        catch (NoClassDefFoundError nex)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10202, new Object[] { destinationName });
            cx.setRootCause(nex);
            throw cx;
        }
        return cluster;
    }

    public void destroyClusters()
    {
        for (Iterator iter = clusters.keySet().iterator(); iter.hasNext();)
        {
            Cluster cluster = (Cluster) clusters.get(iter.next());
            cluster.destroy();
            iter.remove();
        }
    }

    public void clusterDestinationChannel(String clusterId, String serviceType, String destinationName, String channelId, String endpointUrl, int endpointPort, boolean sharedBackend)
    {
        Cluster cluster = getClusterById(clusterId);
        String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
        if (cluster == null)
        {
            if (!clusterConfig.containsKey(clusterId))
            {
                ClusterException cx = new ClusterException();
                cx.setMessage(10207, new Object[] { destinationName, clusterId });
                throw cx;
            }
            cluster = createCluster(clusterId, serviceType, destinationName);
        }
        else
        {
            clustersForDestination.put(destKey, cluster);
        }
        backendSharedForDestination.put(destKey, sharedBackend ? Boolean.TRUE : Boolean.FALSE);

        if (cluster.getURLLoadBalancing())
            cluster.addLocalEndpointForChannel(serviceType, destinationName, channelId, endpointUrl, endpointPort);
    }

    public void clusterDestination(Destination destination)
    {
        String clusterId = destination.getNetworkSettings().getClusterId();
        String serviceType = destination.getServiceType();
        String destinationName = destination.getId();
        boolean sharedBackend = destination.getNetworkSettings().isSharedBackend();
        List channelIds = destination.getChannels();

        if (clusterId == null)
            clusterId = getDefaultClusterId();

        ClusterSettings cls = (ClusterSettings) clusterSettings.get(clusterId);

        if (cls == null)
        {
            ClusterException ce = new ClusterException();
            ce.setMessage(10217, new Object[] { destination.getId(), clusterId });
            throw ce;
        }

        for (Iterator iter = channelIds.iterator(); iter.hasNext();)
        {
            String channelId = (String) iter.next();
            Endpoint endpoint = broker.getEndpoint(channelId);
            String endpointUrl = endpoint.getUrl();
            int endpointPort = endpoint.getPort();

            // This is only an error if we are using client side url-based load balancing. If
            // there is a HW load balancer, then we can assume the server.name served up by the
            // SWF can be used to access the cluster members. With client side load balancing,
            // the clients need the direct URLs of all of the servers.
            if (cls.getURLLoadBalancing())
            {
                // Ensure that the endpoint URI does not contain any replacement tokens.
                int tokenStart = endpointUrl.indexOf("{");
                if (tokenStart != -1)
                {
                    int tokenEnd = endpointUrl.indexOf("}", tokenStart);
                    if (tokenEnd == -1)
                        tokenEnd = endpointUrl.length();
                    else
                        tokenEnd++;

                    ClusterException ce = new ClusterException();
                    ce.setMessage(10209, new Object[] { destination.getId(), channelId, endpointUrl.substring(tokenStart, tokenEnd) });
                    throw ce;
                }
            }

            clusterDestinationChannel(clusterId, serviceType, destinationName, channelId, endpointUrl, endpointPort, sharedBackend);
        }
    }

    public List getEndpointsForDestination(String serviceType, String destinationName)
    {
        Cluster c = getCluster(serviceType, destinationName);
        if (c != null)
        {
            return c.getAllEndpoints(serviceType, destinationName);
        }
        return null;
    }

    private Cluster createCluster(String clusterId, String serviceType, String destinationName)
    {
        String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
        Element propsFile = (Element) clusterConfig.get(clusterId);
        ClusterSettings cls = (ClusterSettings) clusterSettings.get(clusterId);
        Cluster cluster = null;
        Class clusterClass = ClassUtil.createClass(cls.getImplementationClass());
        Constructor clusterConstructor = null;
        try
        {
            clusterConstructor = clusterClass.getConstructor(new Class[] { ClusterManager.class, String.class, Element.class });
        }
        catch (Exception e)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10210);
            cx.setRootCause(e);
            throw cx;
        }
        try
        {
            cluster = (Cluster) clusterConstructor.newInstance(new Object[] { this, clusterId, propsFile });
            cluster.setURLLoadBalancing(cls.getURLLoadBalancing());
        }
        catch (Exception e)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10211);
            cx.setRootCause(e);
            throw cx;
        }
        clustersForDestination.put(destKey, cluster);
        clusters.put(clusterId, cluster);

        if (defaultClusterId != null && defaultClusterId.equals(clusterId))
            defaultCluster = cluster;

        return cluster;
    }
}
