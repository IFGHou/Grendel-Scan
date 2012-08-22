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
package flex.messaging.io.amf.translator.decoder;

import java.lang.reflect.Array;
import java.util.Collection;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.translator.TranslationException;

/**
 * Decodes native Java Array, java.util.Collection, or
 * java.lang.String (to char[]) instances to a native
 * Java Array instance with desired component type.
 *
 * This class does not handle the case where the source
 * encodedObject is modified while decoding.
 *
 * @author Brian Deitte
 * @author Peter Farland
 *
 * @exclude
 */
public class ArrayDecoder extends ActionScriptDecoder
{
    @Override public boolean hasShell()
    {
        return true;
    }

    @Override public Object createShell(Object encodedObject, Class desiredClass)
    {
        Class arrayElementClass = desiredClass.getComponentType();

        int size = 10;

        // If we have an encodedObject as a source then we check it's size to optimize
        // array creation. We may have been called by
        if (encodedObject != null)
        {
            if (encodedObject.getClass().isArray())
            {
                size = Array.getLength(encodedObject);
            }
            else if (encodedObject instanceof Collection)
            {
                size = ((Collection)encodedObject).size();
            }
            else
            {
                TranslationException ex = new TranslationException("Could not create Array " + arrayElementClass);
                ex.setCode("Server.Processing");
                throw ex;
            }
        }

        Object shell = Array.newInstance(arrayElementClass, size);
        return shell;
    }

    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (shell == null || encodedObject == null)
            return null;

        Class arrayElementClass = desiredClass.getComponentType();

        if (encodedObject instanceof Collection)
        {
            return decodeArray(shell, (Collection)encodedObject, arrayElementClass);
        }
        else if (encodedObject.getClass().isArray())
        {
            return decodeArray(shell, encodedObject, arrayElementClass);
        }
        else if (encodedObject instanceof String && Character.class.equals(arrayElementClass))
        {
            return decodeArray(shell, (String)encodedObject, arrayElementClass);
        }

        return null; // FIXME: Throw an exception!
    }

    protected Object decodeArray(Object shellArray, String string, Class arrayElementClass)
    {
        if (Character.class.equals(arrayElementClass))
        {
            return string.toCharArray();
        }

        return null; // FIXME: Throw an exception!
    }

    protected Object decodeArray(Object shellArray, Collection collection, Class arrayElementClass)
    {
        return decodeArray(shellArray, collection.toArray(), arrayElementClass);
    }

    protected Object decodeArray(Object shellArray, Object array, Class arrayElementClass)
    {
        Object encodedValue = null;
        Object decodedValue = null;

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
                // We may need to honor our loose-typing rules for individual types as,
                // unlike a Collection, an Array has a fixed element type. We'll use our handy
                // decoder suite again to find us the right decoder...
                ActionScriptDecoder decoder;
                if (SerializationContext.getSerializationContext().restoreReferences)
                    decoder = DecoderFactory.getReferenceAwareDecoder(encodedValue, arrayElementClass);
                else
                    decoder = DecoderFactory.getDecoder(encodedValue, arrayElementClass);

                decodedValue = decoder.decodeObject(encodedValue, arrayElementClass);

                try
                {
                    Array.set(shellArray, n, decodedValue);
                }
                catch (IllegalArgumentException ex)
                {
                    // FIXME: At least log this as a error...
                    // TODO: Should we report a failed Array element set?
                    // Perhaps the action here could be configurable on the translation context?
                    Array.set(shellArray, n, null);
                }
            }
            n++;
        }

        return shellArray;
    }
}
