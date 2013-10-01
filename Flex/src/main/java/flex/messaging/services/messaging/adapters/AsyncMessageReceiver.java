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
package flex.messaging.services.messaging.adapters;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * A <code>MessageReceiver</code> that receives messages asynchronously from JMS.
 *
 * @exclude
 */
class AsyncMessageReceiver implements MessageReceiver, ExceptionListener, MessageListener
{
    private JMSConsumer jmsConsumer;

    /**
     * Constructs a new AsyncMessageReceiver.
     *
     * @param jmsConsumer JMSConsumer associated with the AsyncMessageReceiver.
     */
    public AsyncMessageReceiver(JMSConsumer jmsConsumer)
    {
        this.jmsConsumer = jmsConsumer;
    }

    /**
     * Implements MessageReceiver.startReceive.
     */
    @Override public void startReceive() throws JMSException
    {
        jmsConsumer.setMessageListener(this);
    }

    /**
     * Implements MessageReceiver.stopReceive.
     */
    @Override public void stopReceive()
    {
        // Nothing to do.
    }

    /**
     * Implements javax.jms.ExceptionListener.onException.
     *
     * @param exception JMS exception received from the JMS server.
     */
    @Override public void onException(JMSException exception)
    {
        jmsConsumer.onException(exception);
    }

    /**
     * Implements javax.jms.MessageListener.onMessage.
     *
     * @param message JMS message received from the JMS server.
     */
    @Override public void onMessage(Message message)
    {
        jmsConsumer.onMessage(message);
    }
}
