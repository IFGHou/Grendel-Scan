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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import flex.messaging.io.amf.translator.TranslationException;

/**
 * Decodes a java.lang.reflect.Array, java.util.Collection,
 * java.lang.String (using toCharArray), to a java.util.Collection
 * instance.
 * <p>
 * If the desired Collection class is an interface then an instance
 * of a standard implementation will be created.
 * </p>
 * <p>
 * If java.util.SortedSet is desired, then a java.util.TreeSet will be created.
 * </p>
 * <p>
 * If a java.util.Set is desired, then a java.util.HashSet will be created.
 * </p>
 * <p>
 * If a java.util.List is desired, then a java.util.ArrayList will be created.
 * </p>
 * <p>
 * If a java.util.Collection is desired, then a java.util.ArrayList will be created.
 * </p>
 *
 * @see java.util.Collection
 *
 * @exclude
 */
public class CollectionDecoder extends ActionScriptDecoder
{
    @Override public boolean hasShell()
    {
        return true;
    }

    protected boolean isSuitableCollection(Object encodedObject, Class desiredClass)
    {
        return (encodedObject instanceof Collection && desiredClass.isAssignableFrom(encodedObject.getClass()));
    }

    @Override public Object createShell(Object encodedObject, Class desiredClass)
    {
        Collection col = null;

        try
        {
            if (encodedObject != null)
            {
                if (isSuitableCollection(encodedObject, desiredClass))
                {
                    col = (Collection)encodedObject;
                }
                else
                {
                    if (desiredClass.isInterface())
                    {
                        if (List.class.isAssignableFrom(desiredClass))
                        {
                            col = new ArrayList();
                        }
                        else if (SortedSet.class.isAssignableFrom(desiredClass))
                        {
                            col = new TreeSet();
                        }
                        else if (Set.class.isAssignableFrom(desiredClass))
                        {
                            col = new HashSet();
                        }
                        else if (Collection.class.isAssignableFrom(desiredClass))
                        {
                            col = new ArrayList();
                        }
                    }
                    else
                    {
                        col = (Collection)desiredClass.newInstance();
                    }
                }
            }
            else
            {
                col = (Collection)desiredClass.newInstance();
            }
        }
        catch (Exception e)
        {
            TranslationException ex = new TranslationException("Could not create Collection " + desiredClass, e);
            ex.setCode("Server.Processing");
            throw ex;
        }

        if (col == null)
        {
            DecoderFactory.invalidType(encodedObject, desiredClass);
        }

        return col;
    }

    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (shell == null || encodedObject == null)
            return null;

        // Don't decode if we already have a suitable Collection. 
        if (isSuitableCollection(encodedObject, desiredClass))
        {
            return encodedObject;
        }

        return decodeCollection((Collection)shell, encodedObject);
    }

    protected Collection decodeCollection(Collection collectionShell, Object encodedObject)
    {
        Object obj = null;

        if (encodedObject instanceof String)
        {
            encodedObject = ((String)encodedObject).toCharArray();
        }
        else if (encodedObject instanceof Collection)
        {
            encodedObject = ((Collection)encodedObject).toArray();
        }

        int len = Array.getLength(encodedObject);

        for (int i = 0; i < len; i++)
        {
            obj = Array.get(encodedObject, i);
            collectionShell.add(obj);
        }

        return collectionShell;
    }
}
