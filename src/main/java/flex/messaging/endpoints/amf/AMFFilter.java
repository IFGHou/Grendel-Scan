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
package flex.messaging.endpoints.amf;

import java.io.IOException;

import flex.messaging.io.amf.ActionContext;

/**
 * Filters perform pre- and post-processing duties on the ActionContext,
 * which contains the message/invocation as well as conextual information
 * about it, following the standard pipe-and-filter design pattern.
 *
 * @author PS Neville
 */
public abstract class AMFFilter
{
    protected AMFFilter next;

    public AMFFilter()
    {
    }

    public void setNext(AMFFilter next)
    {
        this.next = next;
    }

    public AMFFilter getNext()
    {
        return next;
    }

    /**
     * The core business method.
     */
    public abstract void invoke(final ActionContext context) throws IOException;

}
