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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import flex.messaging.util.UUIDUtils;

/**
 * This is the type of message returned by the MessageBroker to endpoints after the broker has routed an endpoint's message to a service.
 * 
 * @author neville
 * @exclude
 */
public class AcknowledgeMessage extends AsyncMessage
{
    /**
     * This number was generated using the 'serialver' command line tool. This number should remain consistent with the version used by ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = 228072709981643313L;

    public AcknowledgeMessage()
    {
        this.messageId = UUIDUtils.createUUID(false);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * @exclude
     */
    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException
    {
        super.readExternal(input);

        short[] flagsArray = readFlags(input);
        for (int i = 0; i < flagsArray.length; i++)
        {
            short flags = flagsArray[i];
            short reservedPosition = 0;

            // For forwards compatibility, read in any other flagged objects
            // to preserve the integrity of the input stream...
            if ((flags >> reservedPosition) != 0)
            {
                for (short j = reservedPosition; j < 6; j++)
                {
                    if (((flags >> j) & 1) != 0)
                    {
                        input.readObject();
                    }
                }
            }
        }
    }

    /**
     * @exclude
     */
    @Override
    public Message getSmallMessage()
    {
        if (getClass() == AcknowledgeMessage.class)
            return new AcknowledgeMessageExt(this);
        return null;
    }

    /**
     * @exclude
     */
    @Override
    public void writeExternal(ObjectOutput output) throws IOException
    {
        super.writeExternal(output);

        short flags = 0;
        output.writeByte(flags);
    }
}
