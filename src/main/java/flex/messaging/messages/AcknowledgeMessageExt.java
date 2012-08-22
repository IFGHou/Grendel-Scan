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
package flex.messaging.messages;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;

import flex.messaging.io.ClassAlias;

/**
 * @exclude
 */
public class AcknowledgeMessageExt extends AcknowledgeMessage implements
        Externalizable, ClassAlias
{
    private static final long serialVersionUID = -8764729006642310394L;                                                                                           
    public static final String CLASS_ALIAS = "DSK";

    public AcknowledgeMessageExt()
    {
        this(null);
    }

    public AcknowledgeMessageExt(AcknowledgeMessage message)
    {
        super();
        _message = message;
    }

    @Override public String getAlias()
    {
        return CLASS_ALIAS;
    }

    @Override public void writeExternal(ObjectOutput output) throws IOException
    {
        if (_message != null)
            _message.writeExternal(output);
        else
            super.writeExternal(output);
    }

    private AcknowledgeMessage _message;

}
