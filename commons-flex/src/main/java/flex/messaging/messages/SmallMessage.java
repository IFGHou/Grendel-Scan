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

package flex.messaging.messages;

/**
 * A marker interface that is used to indicate that a Message has an alternative smaller form for serialization.
 * 
 * @exclude
 */
public interface SmallMessage extends Message
{
    /**
     * This method must be implemented by subclasses that have an <code>java.io.Externalizable</code> or "small" form, or null to indicate that a small form is not available.
     * 
     * @return An alternative representation of a flex.messaging.messages.Message so that the size of the serialized message is smaller.
     */
    Message getSmallMessage();
}
