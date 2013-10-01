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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.stack.IpAddress;

/**
 * @exclude ClusterNode is an encapsulation for pairing a physical host and a logical software group, which is in effect a mapping between a physical address used by the cluster infrastruture and a
 *          service destination used by the message infrastructure.
 * 
 * @author neville
 */
public class ClusterNode
{
    private final String host;
    private final Map destKeyToChannelMap;

    ClusterNode(Address address)
    {
        IpAddress addr = (IpAddress) address;
        host = addr.getIpAddress().getCanonicalHostName();
        destKeyToChannelMap = new HashMap();
    }

    String getHost()
    {
        return host;
    }

    Map getDestKeyToChannelMap()
    {
        return destKeyToChannelMap;
    }

    Map getEndpoints(String serviceType, String destName)
    {
        String destKey = serviceType + ":" + destName;
        synchronized (destKeyToChannelMap)
        {
            Map channelEndpoints = (Map) destKeyToChannelMap.get(destKey);
            if (channelEndpoints == null)
            {
                channelEndpoints = new HashMap();
                destKeyToChannelMap.put(destKey, channelEndpoints);
            }
            return channelEndpoints;
        }
    }

    void addEndpoint(String serviceType, String destName, String channelId, String endpointUrl)
    {
        synchronized (destKeyToChannelMap)
        {
            Map channelEndpoints = getEndpoints(serviceType, destName);
            channelEndpoints.put(channelId, endpointUrl);
        }
    }

    boolean containsEndpoint(String serviceType, String destName, String channelId, String endpointUrl)
    {
        Map channelEndpoints = getEndpoints(serviceType, destName);
        return channelEndpoints.containsKey(channelId) && channelEndpoints.get(channelId).equals(endpointUrl);
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("ClusterNode[");
        synchronized (destKeyToChannelMap)
        {
            for (Iterator iter = destKeyToChannelMap.keySet().iterator(); iter.hasNext();)
            {
                String destKey = (String) iter.next();
                sb.append(" channels for ");
                sb.append(destKey);
                sb.append("(");
                Map channelEndpoints = (Map) destKeyToChannelMap.get(destKey);
                for (Iterator dit = channelEndpoints.keySet().iterator(); dit.hasNext();)
                {
                    String channelId = (String) dit.next();
                    String endpointUrl = (String) channelEndpoints.get(channelId);
                    sb.append(channelId);
                    sb.append("=");
                    sb.append(endpointUrl);
                    if (dit.hasNext())
                        sb.append(", ");
                }
                sb.append(")");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }
}
