/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging.client;

/**
 * Defines the interface to handle asynchronous poll results.
 */
public interface AsyncPollHandler
{
    /**
     * Invoked by the <tt>FlexClient</tt> when an asynchronous poll result is available.
     * 
     * @param flushResult The flush result containing messages to return in the poll response and
     *         an optional wait time before the client should issue its next poll.
     */
    void asyncPollComplete(FlushResult flushResult);
}
