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

import java.util.EventObject;

import javax.jms.Message;

/**
 * Event dispatched to the JMSMessageListener when a JMS message is received by the source.
 * 
 * @see flex.messaging.services.messaging.adapters.JMSMessageListener
 * @exclude
 */
public class JMSMessageEvent extends EventObject
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private Message message;

    /**
     * Create a new JMSMessageEvent with the source and message.
     * 
     * @param source
     *            The source of the message.
     * @param jmsException
     *            The actual JMS message.
     */
    JMSMessageEvent(JMSConsumer source, javax.jms.Message message)
    {
        super(source);
        this.message = message;
    }

    /**
     * Return the JMS message of the event.
     * 
     * @return The JMS message of the event.
     */
    public Message getJMSMessage()
    {
        return message;
    }
}
