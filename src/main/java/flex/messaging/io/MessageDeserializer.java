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
package flex.messaging.io;

import java.io.IOException;
import java.io.InputStream;

import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;

/**
 * An interface to allow for either AMF or AMFX based deserializers
 * to process requests.
 *
 * @author Peter Farland
 */
public interface MessageDeserializer
{
    void initialize(SerializationContext context, InputStream in, AmfTrace trace);

    void readMessage(ActionMessage m, ActionContext context) throws ClassNotFoundException, IOException;

    Object readObject() throws ClassNotFoundException, IOException;
}
