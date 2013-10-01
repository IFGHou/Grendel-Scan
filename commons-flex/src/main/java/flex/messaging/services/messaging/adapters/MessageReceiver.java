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
package flex.messaging.services.messaging.adapters;

import javax.jms.JMSException;

/**
 * An interface used by <code>JMSConsumer</code> to receive messages from JMS.
 * 
 * @exclude
 */
interface MessageReceiver
{
    /**
     * Called by <code>JMSConsumer</code> as it starts up.
     * 
     * @throws JMSException
     */
    void startReceive() throws JMSException;

    /**
     * Called by <code>JMSConsumer</code> as it stops.
     */
    void stopReceive();
}
