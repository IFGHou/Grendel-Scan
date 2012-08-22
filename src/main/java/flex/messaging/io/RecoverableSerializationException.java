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

/**
 * This exception class should be used by the deserializers to indicate
 * that a non fatal exception occurred during serialization.
 * The exception is such that the message body can still be created
 * and the message can be processed in the usual stream.
 * The BatchProcessFilter will add an error message to the response
 * of any messages which have a recoverable serialization exception.
 * @author Cathy Reilly
 */
public class RecoverableSerializationException extends SerializationException
{
    static final long serialVersionUID = 2671402324412964558L;

    public RecoverableSerializationException()
    {
        super();
    }
}
