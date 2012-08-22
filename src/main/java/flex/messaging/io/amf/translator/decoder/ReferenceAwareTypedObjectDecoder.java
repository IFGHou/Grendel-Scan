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
import java.util.List;

import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.TypeMarshallingContext;
import flex.messaging.io.amf.translator.TranslationException;

/**
 * @exclude
 */
public class ReferenceAwareTypedObjectDecoder extends TypedObjectDecoder
{
    @Override protected Object decodeTypedObject(Object bean, Object encodedObject)
    {
        TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
        context.getKnownObjects().put(encodedObject, bean);

        PropertyProxy beanProxy = PropertyProxyRegistry.getProxy(bean);
        PropertyProxy encodedProxy = PropertyProxyRegistry.getProxy(encodedObject);

        List propertyNames = beanProxy.getPropertyNames(bean);
        if (propertyNames != null)
        {
            Iterator it = propertyNames.iterator();
            while (it.hasNext())
            {
                String propName = (String)it.next();

                Class wClass = beanProxy.getType(bean, propName);

                // get property value from encodedObject
                Object value = encodedProxy.getValue(encodedObject, propName);

                Object decodedObject = null;
                try
                {
                    if (value != null)
                    {
                        //Check whether we need to restore a client
                        //side reference to a known object
                        Object ref = null;
    
                        if (canUseByReference(value))
                            ref = context.getKnownObjects().get(value);
    
                        if (ref == null)
                        {
                            ActionScriptDecoder decoder = DecoderFactory.getReferenceAwareDecoder(value, wClass);
                            decodedObject = decoder.decodeObject(value, wClass);
    
                            if (canUseByReference(decodedObject))
                            {
                                context.getKnownObjects().put(value, decodedObject);
                            }
                        }
                        else
                        {
                            decodedObject = ref;
                        }
                    }
    
                    // TODO: Perhaps we could update NumberDecoder, CharacterDecoder and
                    // BooleanDecoder to do this for us?
                    if (decodedObject == null && wClass.isPrimitive())
                    {
                        decodedObject = getDefaultPrimitiveValue(wClass);
                    }
    
                    beanProxy.setValue(bean, propName, decodedObject);
                }
                catch (Exception e)
                {
                    TranslationException ex = new TranslationException("Could not set object " + decodedObject + " on " + bean.getClass() + "'s " + propName);
                    ex.setCode("Server.Processing");
                    ex.setRootCause(e);
                    throw ex;
                }
            }
        }

        return bean;
    }


}
