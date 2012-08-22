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

import java.util.Iterator;

import flex.messaging.MessageClient;
import flex.messaging.MessageDestination;

/**
 * @author Jeff Vroom
 * @exclude
 */
public class RemoteMessageClient extends MessageClient
{
    /**
     * @exclude
     */
    private static final long serialVersionUID = -4743740983792418491L;

    public RemoteMessageClient(Object clientId, MessageDestination destination, String endpointId)
    {
        super(clientId, destination, endpointId, false /* do not use session */);
    }

    /**
     * Invalidates the RemoteMessageClient.
     */
    @Override public void invalidate()
    {
        synchronized (lock)
        {
            if (!valid)
                return;
        }
        
        for (Iterator it = subscriptions.iterator(); it.hasNext(); )
        {
            SubscriptionInfo si = (SubscriptionInfo) it.next();

            destination.getRemoteSubscriptionManager().removeSubscriber(clientId,
                                                                        si.selector, si.subtopic, null);
        }
    }
}
