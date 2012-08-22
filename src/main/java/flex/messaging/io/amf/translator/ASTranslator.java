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

import flex.messaging.io.SerializationContext;
import flex.messaging.io.TypeMarshaller;
import flex.messaging.io.amf.translator.decoder.ActionScriptDecoder;
import flex.messaging.io.amf.translator.decoder.DecoderFactory;
import flex.messaging.util.ClassUtil;
import flex.messaging.util.Trace;

/**
 * ASTranslator provides the ability to convert between ASObjects used by
 * Flex and Java objects in your application.
 */
public class ASTranslator implements TypeMarshaller
{
    /** {@inheritDoc} */
    @Override public Object createInstance(Object source, Class desiredClass)
    {
        ActionScriptDecoder decoder = DecoderFactory.getDecoderForShell(desiredClass);

        Object instance = null;
        if (decoder.hasShell())
        {
            instance = decoder.createShell(source, desiredClass);
        }
        else
        {
            instance = ClassUtil.createDefaultInstance(desiredClass, null);
        }

        return instance;
    }

    /**
     * Translate an object to another object of type class.
     * obj types should be ASObject, Boolean, String, Double, Date, ArrayList
     */
    @Override public Object convert(Object source, Class desiredClass)
    {
        if (source == null && !desiredClass.isPrimitive())
        {
            return null;
        }

        SerializationContext serializationContext = SerializationContext.getSerializationContext();

        ActionScriptDecoder decoder;
        if (serializationContext.restoreReferences)
            decoder = DecoderFactory.getReferenceAwareDecoder(source, desiredClass);
        else
            decoder = DecoderFactory.getDecoder(source, desiredClass);

        if (Trace.remote)
        {
            Trace.trace("Decoder for " + (source == null ? "null" : source.getClass().toString()) +
                    " with desired " + desiredClass + " is " + decoder.getClass());
        }

        Object result = decoder.decodeObject(source, desiredClass);
        return result;
    }
}