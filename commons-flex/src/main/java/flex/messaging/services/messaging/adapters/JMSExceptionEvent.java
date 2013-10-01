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

import javax.jms.JMSException;

/**
 * Event dispatched to the JMSExceptionListener when a JMS exception is encountered by the source.
 * 
 * @see flex.messaging.services.messaging.adapters.JMSExceptionListener
 */
public class JMSExceptionEvent extends EventObject
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private JMSException jmsException;

    /**
     * Create a new JMSExceptionEvent with the source and exception.
     * 
     * @param source
     *            The source of the exception.
     * @param jmsException
     *            The actual JMS exception.
     */
    JMSExceptionEvent(JMSConsumer source, JMSException jmsException)
    {
        super(source);
        this.jmsException = jmsException;
    }

    /**
     * Return the JMS exception of the event.
     * 
     * @return The JMS exception of the event.
     */
    public JMSException getJMSException()
    {
        return jmsException;
    }
}
