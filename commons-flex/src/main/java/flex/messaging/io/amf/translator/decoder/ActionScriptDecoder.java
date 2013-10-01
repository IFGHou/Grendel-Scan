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
package flex.messaging.io.amf.translator.decoder;

import java.util.Calendar;
import java.util.Date;

import flex.messaging.io.SerializationContext;

/**
 * Decode an ActionScript object (of some type) to a Java object (of some type).
 * 
 * @exclude
 */
public abstract class ActionScriptDecoder
{
    /**
     * Does this type have a placeholder shell?
     */
    public boolean hasShell()
    {
        return false;
    }

    /**
     * Used for calls only interested in creating a placeholder shell for a type.
     */
    public Object createShell(Object encodedObject, Class desiredClass)
    {
        return null;
    }

    /**
     * 
     * @param shell
     * @param encodedObject
     * @param desiredClass
     */
    public abstract Object decodeObject(Object shell, Object encodedObject, Class desiredClass);

    /**
     * Used by calls wanted to decode an object. If the decoder requires a place holder shell one is created and then the encodedObject is decoded to fill the object shell.
     */
    public Object decodeObject(Object encodedObject, Class desiredClass)
    {
        Object shell = null;

        if (hasShell())
        {
            shell = createShell(encodedObject, desiredClass);
        }

        return decodeObject(shell, encodedObject, desiredClass);
    }

    protected boolean canUseByReference(Object o)
    {
        if (o == null)
            return false;

        else if (o instanceof String)
            return false;

        else if (o instanceof Number)
            return false;

        else if (o instanceof Boolean)
            return false;

        else if (o instanceof Date)
        {
            if (SerializationContext.getSerializationContext().supportDatesByReference)
                return true;
            else
                return false;
        }

        else if (o instanceof Calendar)
            return false;

        else if (o instanceof Character)
            return false;

        return true;
    }

    protected static Object getDefaultPrimitiveValue(Class type)
    {
        if (type == Boolean.TYPE)
            return Boolean.FALSE;
        else if (type == Integer.TYPE)
            return new Integer(0);
        else if (type == Double.TYPE)
            return new Double(0);
        else if (type == Long.TYPE)
            return new Long(0);
        else if (type == Float.TYPE)
            return new Float(0);
        else if (type == Character.TYPE)
            return new Character(Character.MIN_VALUE);
        else if (type == Short.TYPE)
            return new Short((short) 0);
        else if (type == Byte.TYPE)
            return new Byte((byte) 0);

        return null;
    }
}
