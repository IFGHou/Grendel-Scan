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
package flex.management.runtime.messaging.services.messaging;

import java.io.IOException;

import flex.management.BaseControlMBean;

/**
 * Defines the runtime monitoring and management interface for
 * <code>SubscriptionManager</code>s.
 *
 * @author shodgson
 */
public interface SubscriptionManagerControlMBean extends BaseControlMBean
{
    /**
     * Returns the count of active subscribers.
     *
     * @return The count of active subscribers.
     * @throws IOException Throws IOException.
     */
    Integer getSubscriberCount() throws IOException;

    /**
     * Returns the ids for all active subscribers.
     *
     * @return The ids for all active subscribers.
     * @throws IOException Throws IOException.
     */
    String[] getSubscriberIds() throws IOException;

    /**
     * Unsubscribes the target subscriber.
     *
     * @param subscriberId The id for the subscriber to unsubscribe.
     * @throws IOException Throws IOException.
     */
    void removeSubscriber(String subscriberId) throws IOException;

    /**
     * Unsubscribes all active subscribers.
     *
     * @throws IOException Throws IOException.
     */
    void removeAllSubscribers() throws IOException;
}
