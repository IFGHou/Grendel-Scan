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
import flex.messaging.util.ClassUtil;

/**
 * Decodes an ASObject to a Java object based on the
 * type information returned from the ASObject.getType().
 *
 * If the TranslationContext has been set up to support
 * _remoteClass then this property may be used as a back up.
 *
 * @exclude
 */
public class TypedObjectDecoder extends ActionScriptDecoder
{
    @Override public boolean hasShell()
    {
        return true;
    }

    @Override public Object createShell(Object encodedObject, Class desiredClass)
    {
        Object shell = null;

        Class cls;
        String type = TypeMarshallingContext.getType(encodedObject);

        if (type != null)
        {
            TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
             cls = ClassUtil.createClass(type, context.getClassLoader());
        }
        else
        {
            cls = desiredClass;
        }

        shell = ClassUtil.createDefaultInstance(cls, null);

        return shell;
    }

    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        Object bean = shell;
        if (bean == null)
            return null;

        return decodeTypedObject(bean, encodedObject);
    }

    protected Object decodeTypedObject(Object bean, Object encodedObject)
    {
        PropertyProxy beanProxy = PropertyProxyRegistry.getProxyAndRegister(bean);
        PropertyProxy encodedProxy = PropertyProxyRegistry.getProxyAndRegister(encodedObject);

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
                        // We may need to honor our loose-typing rules for individual types as,
                        // unlike a Collection, an Array has a fixed element type. We'll use our handy
                        // decoder suite again to find us the right decoder...
                        ActionScriptDecoder decoder = DecoderFactory.getDecoder(value, wClass);
                        decodedObject = decoder.decodeObject(value, wClass);
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
