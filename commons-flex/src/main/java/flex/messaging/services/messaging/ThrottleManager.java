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
package flex.messaging.services.messaging;

import java.util.HashMap;
import java.util.Map;

import flex.management.ManageableComponent;
import flex.management.runtime.messaging.services.messaging.ThrottleManagerControl;
import flex.messaging.MessageException;
import flex.messaging.config.ThrottleSettings;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;

/**
 * @exclude
 * 
 *          The ThrottleManager provides functionality to limit the frequency of messages routed through the system in message/second terms. Message frequency can be managed on a per-client basis and
 *          also on a per-destination basis by tweaking different parameters. Each MessageDestination has one ThrottleManager.
 * 
 *          Message frequency can be throttled differently for incoming messages, which are messages published by Flash/Flex producers, and for outgoing messages, which are messages consumed by
 *          Flash/Flex subscribers that may have bene produced by either Flash clients or external message producers (such as data feeds, JMS publishers, etc).
 * 
 */
public class ThrottleManager extends ManageableComponent
{
    // --------------------------------------------------------------------------
    //
    // Public Static Constants
    //
    // --------------------------------------------------------------------------

    public static final String LOG_CATEGORY = LogCategories.SERVICE_MESSAGE;
    public static final String TYPE = "ThrottleManager";

    // --------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    // --------------------------------------------------------------------------

    private static final Object classMutex = new Object();
    private static final int MESSAGE_TIMES_SIZE = 15;

    // --------------------------------------------------------------------------
    //
    // Private Static Variables
    //
    // --------------------------------------------------------------------------

    private static int instanceCount = 0;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>ThrottleManager</code> instance.
     */
    public ThrottleManager()
    {
        this(false);
    }

    /**
     * Constructs a <code>ThrottleManager</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>ThrottleManager</code> is manageable; otherwise <code>false</code>.
     */
    public ThrottleManager(boolean enableManagement)
    {
        super(enableManagement);
        synchronized (classMutex)
        {
            super.setId(TYPE + ++instanceCount);
        }
        setThrottleSettings(new ThrottleSettings());
    }

    // --------------------------------------------------------------------------
    //
    // Variables
    //
    // --------------------------------------------------------------------------

    private Map<Object, ThrottleMark> inboundClientMarks;
    private Map<Object, ThrottleMark> outboundClientMarks;
    private ThrottleMark inboundDestinationMark;
    private ThrottleMark outboundDestinationMark;
    private ThrottleSettings settings;

    // --------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    // --------------------------------------------------------------------------

    /**
     * Stops the throttle manager.
     */
    @Override
    public void stop()
    {
        super.stop();

        // Remove management.
        if (isManaged() && getControl() != null)
        {
            getControl().unregister();
            setControl(null);
            setManaged(false);
        }
    }

    // --------------------------------------------------------------------------
    //
    // Public Methods
    //
    // --------------------------------------------------------------------------

    /**
     * This is a no-op because throttle manager's id is generated internally.
     * 
     * @param id
     *            The id.
     */
    @Override
    public void setId(String id)
    {
        // No-op
    }

    /**
     * @exclude Used by the MessageClient in its cleanup process.
     * 
     * @param clientId
     *            The id of the MessageClient.
     */
    public void removeClientThrottleMark(Object clientId)
    {
        if (inboundClientMarks != null)
            inboundClientMarks.remove(clientId);

        if (outboundClientMarks != null)
            outboundClientMarks.remove(clientId);
    }

    /**
     * Sets the throttling settings of the throttle manager.
     * 
     * @param throttleSettings
     *            The throttling settings for the throttle manager.
     */
    public void setThrottleSettings(ThrottleSettings throttleSettings)
    {
        settings = throttleSettings;
        if (settings.isDestinationThrottleEnabled())
        {
            inboundDestinationMark = new ThrottleMark(settings.getDestinationName());
            outboundDestinationMark = new ThrottleMark(settings.getDestinationName());
        }
        if (settings.isClientThrottleEnabled())
        {
            inboundClientMarks = new HashMap<Object, ThrottleMark>();
            outboundClientMarks = new HashMap<Object, ThrottleMark>();
        }
    }

    /**
     * Tries to throttle the incoming message at the destination and the client level.
     * 
     * @param msg
     *            Message to be throttled.
     * @return The result of the throttling.
     */
    public ThrottleResult throttleIncomingMessage(Message msg)
    {
        // destination-level throttling comes before client-level, because if it
        // fails then it doesn't matter what the client-level throttle reports.
        ThrottleResult result = new ThrottleResult(ThrottleResult.RESULT_OK);

        if (settings.getInboundPolicy() != ThrottleSettings.POLICY_NONE)
        {
            result = throttleDestinationLevel(msg, true);
            if (result.getResultCode() == ThrottleResult.RESULT_OK)
            {
                // client-level throttling allows the system to further refine a
                // different throttle for individual clients, which may be a subset
                // but never a superset of destination-level throttle settings
                result = throttleClientLevel(msg, msg.getClientId(), true);
            }
        }
        return result;
    }

    /**
     * Tries to throttle the outgoing message at the destination and the client level.
     * 
     * @param msg
     *            The message to be throttled.
     * @param clientId
     *            The id of the client that the message is intended to.
     * @return The result of the throttling.
     */
    public ThrottleResult throttleOutgoingMessage(Message msg, Object clientId)
    {
        ThrottleResult result = new ThrottleResult(ThrottleResult.RESULT_OK);
        if (settings.getOutboundPolicy() != ThrottleSettings.POLICY_NONE)
        {
            if (clientId == null)
                result = throttleDestinationLevel(msg, false);
            else
                result = throttleClientLevel(msg, clientId, false);
        }
        return result;
    }

    // --------------------------------------------------------------------------
    //
    // Protected and private methods.
    //
    // --------------------------------------------------------------------------

    @Override
    protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    private ThrottleResult throttleDestinationLevel(Message msg, boolean incoming)
    {
        ThrottleResult result = new ThrottleResult(ThrottleResult.RESULT_OK);
        if (settings.isDestinationThrottleEnabled())
        {
            if (incoming)
            {
                try
                {
                    inboundDestinationMark.assertValid(msg.getTimestamp(), settings.getIncomingDestinationFrequency());
                }
                catch (RuntimeException e)
                {
                    int throttleResultCode = getResultCode(settings.getInboundPolicy());

                    // Update the management metrics.
                    if (throttleResultCode != ThrottleResult.RESULT_OK && isManaged())
                        ((ThrottleManagerControl) getControl()).incrementDestinationIncomingMessageThrottleCount();

                    String s = "Message throttled: Too many messages sent to destination " + inboundDestinationMark.id + " in too small of a time interval.  " + e.getMessage();
                    MessageException me = new MessageException(s);
                    result = new ThrottleResult(throttleResultCode, me);
                }
            }
            else
            {
                try
                {
                    outboundDestinationMark.assertValid(msg.getTimestamp(), settings.getOutgoingDestinationFrequency());
                }
                catch (RuntimeException e)
                {
                    int throttleResultCode = getResultCode(settings.getOutboundPolicy());

                    // Update the management metrics.
                    if (throttleResultCode != ThrottleResult.RESULT_OK && isManaged())
                        ((ThrottleManagerControl) getControl()).incrementDestinationOutgoingMessageThrottleCount();

                    String s = "Message throttled: Too many messages routed by destination " + outboundDestinationMark.id + " in too small of a time interval";
                    MessageException me = new MessageException(s);
                    result = new ThrottleResult(throttleResultCode, me);
                }
            }
        }
        return result;
    }

    private ThrottleResult throttleClientLevel(Message msg, Object clientId, boolean incoming)
    {
        ThrottleResult result = new ThrottleResult(ThrottleResult.RESULT_OK);
        if (settings.isClientThrottleEnabled())
        {
            ThrottleMark clientLevelMark;
            if (incoming)
            {
                clientLevelMark = inboundClientMarks.get(clientId);
                if (clientLevelMark == null)
                    clientLevelMark = new ThrottleMark(clientId);
                try
                {
                    clientLevelMark.assertValid(msg.getTimestamp(), settings.getIncomingClientFrequency());
                }
                catch (RuntimeException e)
                {
                    int throttleResultCode = getResultCode(settings.getInboundPolicy());

                    // Update the management metrics.
                    if ((throttleResultCode != ThrottleResult.RESULT_OK) && isManaged())
                        ((ThrottleManagerControl) getControl()).incrementClientIncomingMessageThrottleCount();

                    String s = "Message throttled: Too many messages sent by client " + clientId + " in too small of a time interval";
                    MessageException me = new MessageException(s);
                    result = new ThrottleResult(throttleResultCode, me);
                }
                finally
                {
                    inboundClientMarks.put(clientId, clientLevelMark);
                }
            }
            else
            {
                clientLevelMark = outboundClientMarks.get(clientId);
                if (clientLevelMark == null)
                    clientLevelMark = new ThrottleMark(clientId);
                try
                {
                    clientLevelMark.assertValid(msg.getTimestamp(), settings.getOutgoingClientFrequency());
                }
                catch (RuntimeException e)
                {
                    int throttleResultCode = getResultCode(settings.getOutboundPolicy());

                    // Update the management metrics.
                    if ((throttleResultCode != ThrottleResult.RESULT_OK) && isManaged())
                        ((ThrottleManagerControl) getControl()).incrementClientOutgoingMessageThrottleCount();

                    String s = "Message throttled: Too many messages sent to client " + clientId + " in too small of a time interval";
                    MessageException me = new MessageException(s);
                    result = new ThrottleResult(throttleResultCode, me);
                }
                finally
                {
                    outboundClientMarks.put(clientId, clientLevelMark);
                }
            }
        }
        return result;
    }

    /**
     * Given a policy, returns the result code for that policy.
     * 
     * @param policy
     *            The integer representing the policy.
     * @return The result code for the policy.
     */
    private int getResultCode(int policy)
    {
        int n = 0;
        switch (policy)
        {
            case ThrottleSettings.POLICY_IGNORE:
                n = ThrottleResult.RESULT_IGNORE;
                break;
            case ThrottleSettings.POLICY_REPLACE:
                n = ThrottleResult.RESULT_REPLACE;
                break;
            case ThrottleSettings.POLICY_ERROR:
                n = ThrottleResult.RESULT_ERROR;
            default:
                break;
        }
        return n;
    }

    // --------------------------------------------------------------------------
    //
    // Nested Classes
    //
    // --------------------------------------------------------------------------

    /**
     * This class is used to keep track of throttling results.
     */
    public static class ThrottleResult
    {
        public static final int RESULT_OK = 0;
        public static final int RESULT_IGNORE = 1;
        public static final int RESULT_REPLACE = 2;
        public static final int RESULT_ERROR = 3;

        private MessageException exception;
        private int resultCode;

        public ThrottleResult(int resultCode)
        {
            this.resultCode = resultCode;
        }

        public ThrottleResult(int result, MessageException exception)
        {
            this(result);
            this.exception = exception;
        }

        public MessageException getException()
        {
            return exception;
        }

        public int getResultCode()
        {
            return resultCode;
        }
    }

    /**
     * ThrottleMark is used to keep track of the message rates for destinations and clients of those destinations.
     */
    class ThrottleMark
    {
        Object id;
        int messageCount;
        long[] previousMessageTimes;

        /**
         * Creates a new ThrottleMark with the specified id.
         * 
         * @param id
         *            Either the destination or the client id associated with the ThrottleMark.
         */
        ThrottleMark(Object id)
        {
            this.id = id;
            messageCount = 0;
            previousMessageTimes = new long[MESSAGE_TIMES_SIZE];
        }

        void assertValid(long messageTimestamp, int maxFrequency)
        {
            if (maxFrequency > 0)
            {
                // If we have enough messages to start testing.
                if (messageCount >= MESSAGE_TIMES_SIZE)
                {
                    // Time delay between this message and the last N messages.
                    long interval = (messageTimestamp - previousMessageTimes[messageCount % MESSAGE_TIMES_SIZE]) / 1000;
                    long actualFrequency = MESSAGE_TIMES_SIZE / (interval > 0 ? interval : 1);
                    // If the rate is too high, toss this message and do not record it,
                    // so the history represents the rate of messages actually delivered.
                    if (actualFrequency > maxFrequency)
                        throw new RuntimeException("actual frequency=" + actualFrequency + " max frequency=" + maxFrequency);
                }
                // Handle integer wrap
                if (messageCount == Integer.MAX_VALUE)
                    messageCount = 0;
                // Increase the messageCount and update the message times.
                previousMessageTimes[messageCount++ % MESSAGE_TIMES_SIZE] = messageTimestamp;
            }
        }
    }
}
