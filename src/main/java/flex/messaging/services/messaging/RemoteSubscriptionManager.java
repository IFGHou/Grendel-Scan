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
package flex.messaging.services.messaging;

import java.util.List;

import flex.messaging.MessageClient;
import flex.messaging.MessageDestination;
import flex.messaging.cluster.RemoveNodeListener;
import flex.messaging.log.Log;
import flex.messaging.services.MessageService;
import flex.messaging.util.StringUtils;

/**
 * The RemoteSubscriptionManager monitors subscriptions from other
 * servers, not other clients.  One MessageClient instance is used for
 * each remote server.  It clientId is the address of the remote server.
 * Using a separate instance of this class keeps the subscriptions
 * of local clients separate from remote clients.
 *
 * @author Jeff Vroom
 * @exclude
 */
public class RemoteSubscriptionManager extends SubscriptionManager implements RemoveNodeListener
{
    private Object syncLock = new Object();

    /*
     * A monitor lock used for synchronizing the attempt to request subscriptions
     * across the cluster during startup.
     */
    private static final Object initRemoteSubscriptionsLock = new Object();

    public RemoteSubscriptionManager(MessageDestination destination)
    {
        this(destination, false);
    }

    public RemoteSubscriptionManager(MessageDestination destination, boolean enableManagement)
    {
        super(destination, enableManagement);
    }

    public void setSessionTimeout(long sessionConfigValue)
    {
    }

    public long getSessionTimeout()
    {
        return 0; // not used for remote subscriptions
    }

    public void addSubscriber(String flexClientId, Object clientId, String selector, String subtopic)
    {
        synchronized (syncLock)
        {
            /*
             * Only process subscriptions for servers whose subscription state we have received
             * We may receive a subscribe/unsubscribe from a peer before we get their
             * subscription state... we ignore these since they will be included in the
             * state we receive later
             */
            if (allSubscriptions.get(clientId) != null)
                super.addSubscriber(clientId, selector, subtopic, null);
            else if (Log.isDebug())
                Log.getLogger(MessageService.LOG_CATEGORY).debug("Ignoring new remote subscription for server: " + clientId + " whose subscription state we have not yet received.  selector: " + selector + " subtopic: " + subtopic);
        }
    }

    public void removeSubscriber(String flexClientId, Object clientId, String selector, String subtopic, String endpoint)
    {
        synchronized (syncLock)
        {
            /* Only process subscriptions for servers whose subscription state we have received */
            if (allSubscriptions.get(clientId) != null)
                super.removeSubscriber(clientId, selector, subtopic, null);
        }
    }

    @Override protected void sendSubscriptionToPeer(boolean subscribe, String selector, String subtopic)
    {
        // Don't do this for remote subscriptions
    }

    @Override protected MessageClient newMessageClient(Object clientId, String endpointId)
    {
        return new RemoteMessageClient(clientId, destination, endpointId);
    }

    /**
     * Takes the selector and subtopic list from this address and
     * for each one create a RemoteSubscription which gets
     * registered in this table.  We also register the remote
     * subscription with a "per server" index so we can easily
     * remove them later on.
     */
    public void setSubscriptionState(Object state, Object address)
    {
        MessageClient client = newMessageClient(address, null);

        if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Received subscription state for destination: " + destination.getId() + " from server: " + address + StringUtils.NEWLINE + state);

        /*
         * need to be sure we do not accept any remote sub/unsub messages
         * from a given server until we have received its subscription state
         *
         * Also, we need to ensure we do not process any remote subscribe/unsubs
         * from a remote server until we finish this list.
         */
        synchronized (syncLock)
        {
            allSubscriptions.put(address, client);

            List list = (List) state;

            for (int i = 0; i < list.size(); i+=2)
            {
                addSubscriber(null, address, (String) list.get(i), (String) list.get(i+1));
            }
        }
        synchronized (initRemoteSubscriptionsLock)
        {
            initRemoteSubscriptionsLock.notifyAll();
        }
    }

    /**
     * This method waits for some time for the receipt of the subscription
     * state for the server with the given address.  If we fail to receive
     * a message after waiting for the 5 seconds, a warning is printed.
     */
    public void waitForSubscriptions(Object addr)
    {
        /* If we have not gotten the response yet from this client... */
        if (getSubscriber(addr) == null)
        {
            synchronized (initRemoteSubscriptionsLock)
            {
                try
                {
                    if (Log.isDebug())
                        Log.getLogger(MessageService.LOG_CATEGORY).debug("Waiting for subscriptions from cluster node: " + addr + " for destination: " + destination.getId());

                    initRemoteSubscriptionsLock.wait(5000);

                    if (Log.isDebug())
                        Log.getLogger(MessageService.LOG_CATEGORY).debug("Done waiting for subscriptions from cluster node: " + addr + " for destination: " + destination.getId());
                }
                catch (InterruptedException exc) {}
            }
            if (getSubscriber(addr) == null && Log.isWarn())
                Log.getLogger(MessageService.LOG_CATEGORY).warn("No response yet from request subscriptions request for server: " + addr + " for destination: " + destination.getId());
        }
        else if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Already have subscriptions from server: " + addr + " for destination: " + destination.getId());
    }

    /**
     * Called when a cluster node gets removed.  We need to make sure that all subscriptions
     * for this node are removed.
     */
    @Override public void removeClusterNode(Object address)
    {
        if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Cluster node: " + address + " subscriptions being removed for destination:" + destination.getId() + " before: " + StringUtils.NEWLINE + getDebugSubscriptionState());

        MessageClient client = getSubscriber(address);
        if (client != null)
        {
            client.invalidate();
        }

        if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Cluster node: " + address + " subscriptions being removed for destination:" + destination.getId() + " after: " + StringUtils.NEWLINE + getDebugSubscriptionState());
    }

    @Override protected void monitorTimeout(MessageClient client)
    {
        // Remote subscriptions do not timeout
    }

}
