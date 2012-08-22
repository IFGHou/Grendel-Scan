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

import java.util.Iterator;
import java.util.Map;

import flex.messaging.io.TypeMarshallingContext;

/**
 * @exclude
 */
public class ReferenceAwareMapDecoder extends MapDecoder
{
    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (shell == null) return null;

        Map shellMap = (Map)shell;
        Map encodedMap = (Map)encodedObject;

        TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
        context.getKnownObjects().put(encodedObject, shell);

        ActionScriptDecoder decoder = null;
        Object key = null;
        Object value = null;
        Object decodedValue = null;
        for (Iterator keys = encodedMap.keySet().iterator(); keys.hasNext();)
        {
            key = keys.next();
            value = encodedMap.get(key);

            if (value == null)
            {
                shellMap.put(key, null);
                continue;
            }

            //Check whether we need to restore a client
            //side reference to a known object
            Object ref = null;

            if (canUseByReference(value))
                ref = context.getKnownObjects().get(value);

            if (ref == null)
            {
                decoder = DecoderFactory.getReferenceAwareDecoder(value, value.getClass());
                decodedValue = decoder.decodeObject(value, value.getClass());

                if (canUseByReference(decodedValue))
                {
                    context.getKnownObjects().put(value, decodedValue);
                }
            }
            else
            {
                decodedValue = ref;
            }

            shellMap.put(key, decodedValue);
        }

        return shellMap;
    }
}
