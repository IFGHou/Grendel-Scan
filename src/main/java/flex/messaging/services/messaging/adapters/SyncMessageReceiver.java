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

import javax.jms.JMSException;
import javax.jms.Message;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import flex.messaging.log.Log;

/**
 * A <code>MessageReceiver</code> that receives messages from JMS using
 * synchronous <code>javax.jms.MessageConsumer.receive</code> call.
 *
 * @exclude
 */
class SyncMessageReceiver implements MessageReceiver
{
    private ScheduledExecutorService messageReceiverService;
    private boolean isScheduled = false;

    private JMSConsumer jmsConsumer;
    private int syncMaxReceiveThreads;
    private long syncReceiveIntervalMillis;
    private long syncReceiveWaitMillis;

    /**
     * Constructs a new <code>SyncMessageReceiver</code> with default delivery settings.
     *
     * @param jmsConsumer JMSConsumer associated with the SynMessageReceiver.
     */
    public SyncMessageReceiver(JMSConsumer jmsConsumer)
    {
        this.jmsConsumer = jmsConsumer;
        syncReceiveIntervalMillis = JMSConfigConstants.defaultSyncReceiveIntervalMillis;
        syncReceiveWaitMillis = JMSConfigConstants.defaultSyncReceiveWaitMillis;
        syncMaxReceiveThreads = 1; // Always use one thread.
    }

    /**
     * Returns the interval of the sync receive message call.
     *
     * @return The interval of the sync receive message call.
     */
    public long getSyncReceiveIntervalMillis()
    {
        return syncReceiveIntervalMillis;
    }

    /**
     * Sets the interval of the receive message call. This property
     * is optional and defaults to 100.
     *
     * @param syncReceiveIntervalMillis A positive long that indicates
     * the interval of the receive message call.
     */
    public void setSyncReceiveIntervalMillis(long syncReceiveIntervalMillis)
    {
        if (syncReceiveIntervalMillis < 1)
            syncReceiveIntervalMillis = JMSConfigConstants.defaultSyncReceiveIntervalMillis;
        this.syncReceiveIntervalMillis = syncReceiveIntervalMillis;
    }

    /**
     * Returns how long a JMS proxy waits for a message before returning.
     *
     * @return How long a JMS proxy waits for a message before returning.
     */
    public long getSyncReceiveWaitMillis()
    {
        return syncReceiveWaitMillis;
    }

    /**
     * Sets how long a JMS proxy waits for a message before returning.
     * This property is optional and defaults to zero (no wait).
     *
     * @param syncReceiveWaitMillis A non-negative value that indicates how
     * long a JMS proxy waits for a message before returning. Zero means no
     * wait, negative one means wait until a message arrives.
     */
    public void setSyncReceiveWaitMillis(long syncReceiveWaitMillis)
    {
        if (syncReceiveWaitMillis < -1)
            syncReceiveWaitMillis = JMSConfigConstants.defaultSyncReceiveWaitMillis;
        this.syncReceiveWaitMillis = syncReceiveWaitMillis;
    }

    /**
     * Implements MessageReceiver.startReceive.
     */
    @Override public void startReceive()
    {
        if (!isScheduled)
        {
            if (Log.isDebug())
                Log.getLogger(JMSAdapter.LOG_CATEGORY).debug(Thread.currentThread()
                        + " JMS consumer sync receive thread for JMS destination '"
                        + jmsConsumer.destinationJndiName + "' is starting to poll the JMS server for new messages.");

            ThreadFactory mrtf = new MessageReceiveThreadFactory();
            messageReceiverService = Executors.newScheduledThreadPool(syncMaxReceiveThreads, mrtf);
            messageReceiverService.scheduleAtFixedRate(new MessageReceiveThread(), syncReceiveIntervalMillis, syncReceiveIntervalMillis, TimeUnit.MILLISECONDS);
            isScheduled = true;
        }
    }

    /**
     * Implements MessageReceivers.stopReceive.
     */
    @Override public void stopReceive()
    {
        if (messageReceiverService != null)
            messageReceiverService.shutdown();
    }

    /**
     * Used internally to receive messages as determined by syncReceiveWaitMillis.
     */
    private Message receiveMessage() throws JMSException
    {
        if (syncReceiveWaitMillis == -1)
            return jmsConsumer.receive();
        else if (syncReceiveWaitMillis == 0)
            return jmsConsumer.receiveNoWait();
        else if (syncReceiveWaitMillis > 0)
            return jmsConsumer.receive(syncReceiveWaitMillis);
        return null;
    }

    /**
     * Thread Factory used to create message receive threads.
     */
    class MessageReceiveThreadFactory implements ThreadFactory
    {

        /**
         * Used to uniquely identify each new message receive thread created.
         */
        private int receiveThreadCount;

        /**
         * Creates a new message receive thread.
         * Synchronized to uniquely identify each receive thread at construct time safely.
         *
         * @param worker The runnable to assign to the new thread.
         */
        @Override public synchronized Thread newThread(Runnable r)
        {
            Thread t = new Thread(r);
            t.setName("MessageReceiveThread" + "-" + receiveThreadCount++);
            if (Log.isDebug())
                Log.getLogger(JMSAdapter.LOG_CATEGORY).debug("Created message receive thread: " + t.getName());
            return t;
        }

    }

    /**
     * Message receive threads that perform sync javax.jms.MessageConsumer.receive
     * calls.
     */
    class MessageReceiveThread implements Runnable
    {
        @Override public void run()
        {
            try
            {
                while (true)
                {
                    Message message = receiveMessage();
                    if (message == null) break;
                    jmsConsumer.onMessage(message);
                }
            }
            catch (JMSException jmsEx)
            {
                jmsConsumer.onException(jmsEx);
            }
        }
    }
}
