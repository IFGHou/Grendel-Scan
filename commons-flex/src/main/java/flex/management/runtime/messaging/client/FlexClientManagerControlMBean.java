/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.management.runtime.messaging.client;

import java.io.IOException;

import flex.management.BaseControlMBean;

/**
 * Defines the runtime monitoring and management interface for managed flex client managers.
 */
public interface FlexClientManagerControlMBean extends BaseControlMBean
{
    /**
     * Returns ids of managed clients.
     * 
     * @return An array of client ids.
     * @throws IOException
     *             Throws IOException.
     */
    String[] getClientIds() throws IOException;

    /**
     * Returns the number of subscriptions for the client with the clientId.
     * 
     * @param clientId
     *            The client id.
     * @return The number of subscriptions for the client with the cliendId
     * @throws IOException
     *             Throws IOException.
     */
    Integer getClientSubscriptionCount(String clientId) throws IOException;

    /**
     * Returns the number of sessiosn for the client with the clientId.
     * 
     * @param clientId
     *            The client id.
     * @return The number of sessions for the client with the cliendId
     * @throws IOException
     *             Throws IOException.
     */
    Integer getClientSessionCount(String clientId) throws IOException;

    /**
     * Returns the last use by the client with the clientId.
     * 
     * @param clientId
     *            The client id.
     * @return The last use by the client with the clientId
     * @throws IOException
     *             Throws IOException.
     */
    Long getClientLastUse(String clientId) throws IOException;

    /**
     * Returns the number of clients.
     * 
     * @return The number of clients.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getFlexClientCount() throws IOException;
}
