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

import java.io.IOException;
import java.io.OutputStream;

import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;

/**
 * @author Peter Farland
 * 
 * @exclude
 */
public interface MessageSerializer
{
    void setVersion(int value);

    void initialize(SerializationContext context, OutputStream out, AmfTrace trace);

    void writeMessage(ActionMessage m) throws IOException;

    void writeObject(Object value) throws IOException;
}
