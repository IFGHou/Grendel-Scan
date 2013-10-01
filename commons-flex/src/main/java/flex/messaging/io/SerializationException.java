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
package flex.messaging.io;

import flex.messaging.MessageException;

/**
 * Typically signifies that a fatal exception happened during deserialization or serialization. The messaging framework should try to get a meaningful message back to the client in a response, however
 * this is not always possible, especially for batched AMF messages, so at the very least the error should be logged.
 * 
 * A special sub-class RecoverableSerializationException can be thrown for non-fatal serialization exceptions.
 * 
 * @author Peter Farland
 * @see flex.messaging.io.RecoverableSerializationException
 */
public class SerializationException extends MessageException
{
    static final long serialVersionUID = -5723542920189973518L;

    public static final String CLIENT_PACKET_ENCODING = "Client.Packet.Encoding";

    public SerializationException()
    {
        setCode(CLIENT_PACKET_ENCODING);
    }
}
