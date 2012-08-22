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
package flex.messaging.io.amf.translator;

import flex.messaging.MessageException;

/**
 * @exclude
 */
public class TranslationException extends MessageException
{
    static final long serialVersionUID = 3312487017261810877L;

    public TranslationException(String message)
    {
        super(message);
    }

    public TranslationException(String message, Throwable rootCause)
    {
        super(message);
        setRootCause(rootCause);
    }

/* TODO UCdetector: Remove unused code: 
    public TranslationException(String message, String details)
    {
        super(message);
        setDetails(details);
    }
*/
}
