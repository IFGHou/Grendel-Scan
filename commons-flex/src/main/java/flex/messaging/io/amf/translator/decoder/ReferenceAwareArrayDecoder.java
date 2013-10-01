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

import java.lang.reflect.Array;
import java.util.Collection;

import flex.messaging.io.TypeMarshallingContext;

/**
 * @exclude
 */
public class ReferenceAwareArrayDecoder extends ArrayDecoder
{
    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (shell == null || encodedObject == null)
            return null;

        Class arrayElementClass = desiredClass.getComponentType();

        if (encodedObject instanceof Collection)
        {
            return decodeArray(shell, (Collection) encodedObject, arrayElementClass);
        }
        else if (encodedObject.getClass().isArray())
        {
            return decodeArray(shell, encodedObject, arrayElementClass);
        }
        else if (encodedObject instanceof String && Character.class.equals(arrayElementClass))
        {
            return decodeArray(shell, (String) encodedObject, arrayElementClass);
        }
        else
        {
            return shell;
        }
    }

    @Override
    protected Object decodeArray(Object shellArray, Collection collection, Class arrayElementClass)
    {
        Object[] array = collection.toArray();
        TypeMarshallingContext.getTypeMarshallingContext().getKnownObjects().put(array, shellArray);
        return decodeArray(shellArray, array, arrayElementClass);
    }

    @Override
    protected Object decodeArray(Object shellArray, Object array, Class arrayElementClass)
    {
        Object encodedValue = null;
        Object decodedValue = null;
        TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();

        ActionScriptDecoder decoder = null;
        int n = 0;
        int len = Array.getLength(array);
        for (int i = 0; i < len; i++)
        {
            encodedValue = Array.get(array, i);

            if (encodedValue == null)
            {
                Array.set(shellArray, n, null);
            }
            else
            {
                // Check whether we need to restore a client
                // side reference to a known object
                Object ref = null;

                if (canUseByReference(encodedValue))
                    ref = context.getKnownObjects().get(encodedValue);

                if (ref == null)
                {
                    decoder = DecoderFactory.getReferenceAwareDecoder(encodedValue, arrayElementClass);
                    decodedValue = decoder.decodeObject(encodedValue, arrayElementClass);

                    if (canUseByReference(decodedValue))
                    {
                        context.getKnownObjects().put(encodedValue, decodedValue);
                    }
                }
                else
                {
                    decodedValue = ref;
                }

                try
                {
                    Array.set(shellArray, n, decodedValue);
                }
                catch (IllegalArgumentException ex)
                {
                    Array.set(shellArray, n, null);
                }
            }
            n++;
        }

        return shellArray;
    }
}
