/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.client;

import java.util.List;

/**
 * Stores the messages that should be written to the network as a result of a flush invocation on a FlexClient's outbound queue.
 */
public class FlushResult
{
    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs a <tt>FlushResult</tt> instance to return from a flush invocation on a FlexClient's outbound queue. This instance stores the list of messages to write over the network to the client
     * as well as an optional wait time in milliseconds for when the next flush should be invoked.
     */
    public FlushResult()
    {
    }

    // --------------------------------------------------------------------------
    //
    // Properties
    //
    // --------------------------------------------------------------------------

    // ----------------------------------
    // messages
    // ----------------------------------

    private List messages;

    /**
     * Returns the messages to write to the network for this flush invocation. This list may be null, in which case no messages are written.
     * 
     * @return The messages to write to the network for this flush invocation.
     */
    public List getMessages()
    {
        return messages;
    }

    /**
     * Sets the messages to write to the network for this flush invocation.
     * 
     * @param value
     *            The messages to write to the network for this flush invocation.
     */
    public void setMessages(List value)
    {
        messages = value;
    }

    // ----------------------------------
    // nextFlushWaitTimeMillis
    // ----------------------------------

    private int nextFlushWaitTimeMillis = 0;

    /**
     * Returns the wait time in milliseconds for when the next flush invocation should occur. If this value is 0, the default, a delayed flush is not scheduled and the next flush will depend upon the
     * underlying Channel/Endpoint. For client-side polling Channels the next flush invocation will happen when the client sends its next poll request at its regular interval. For client-side Channels
     * that support direct writes to the client a flush invocation is triggered when the next message is added to the outbound queue.
     * 
     * @return The wait time in milliseconds before flush is next invoked. A value of 0, the default, indicates that the default flush behavior for the underlying Channel/Endpoint should be used.
     */
    public int getNextFlushWaitTimeMillis()
    {
        return nextFlushWaitTimeMillis;
    }

    /**
     * Sets the wait time in milliseconds for when the next flush invocation should occur. If this value is 0, the default, a delayed flush is not scheduled and the next flush will depend upon the
     * underlying Channel/Endpoint. For client-side polling Channels the next flush invocation will happen when the client sends its next poll request at its regular interval. For client-side Channels
     * that support direct writes to the client a flush invocation is triggered when the next message is added to the outbound queue. Negative value assignments are treated as 0.
     * 
     * @param value
     *            The wait time in milliseconds before flush will be invoked.
     */
    public void setNextFlushWaitTimeMillis(int value)
    {
        nextFlushWaitTimeMillis = (value < 1) ? 0 : value;
    }
}
