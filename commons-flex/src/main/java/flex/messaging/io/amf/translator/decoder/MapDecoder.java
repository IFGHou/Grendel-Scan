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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import flex.messaging.io.amf.translator.TranslationException;

/**
 * Decodes an ActionScript object to a Java Map.
 * 
 * @exclude
 */
public class MapDecoder extends ActionScriptDecoder
{
    @Override
    public boolean hasShell()
    {
        return true;
    }

    protected boolean isSuitableMap(Object encodedObject, Class desiredClass)
    {
        return (encodedObject != null && encodedObject instanceof Map && desiredClass.isAssignableFrom(encodedObject.getClass()));
    }

    @Override
    public Object createShell(Object encodedObject, Class desiredClass)
    {
        try
        {
            if (isSuitableMap(encodedObject, desiredClass))
            {
                return encodedObject;
            }
            else
            {
                if (desiredClass.isInterface() || !Map.class.isAssignableFrom(desiredClass))
                {
                    if (SortedMap.class.isAssignableFrom(desiredClass))
                    {
                        return new TreeMap();
                    }
                    else
                    {
                        return new HashMap();
                    }
                }
                else
                {
                    return desiredClass.newInstance();
                }
            }
        }
        catch (Exception e)
        {
            TranslationException ex = new TranslationException("Could not create Map " + desiredClass, e);
            ex.setCode("Server.Processing");
            throw ex;
        }
    }

    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (shell == null || encodedObject == null)
            return null;

        // Don't decode if we already have a suitable Map.
        if (isSuitableMap(encodedObject, desiredClass))
        {
            return encodedObject;
        }

        return decodeMap((Map) shell, (Map) encodedObject);
    }

    protected Map decodeMap(Map shell, Map map)
    {
        if (shell != map)
            shell.putAll(map);
        else
            shell = map;

        return shell;
    }
}
