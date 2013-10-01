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

/**
 * Translates a java.lang.Boolean or java.lang.String instances into a java.lang.Boolean instance.
 * <p>
 * Note that for Strings, only &quot;true&quot; will be (case insensitively) converted to a true Boolean value. All other values will be interpreted as false.
 * </p>
 * 
 * @author Brian Deitte
 * @author Peter Farland
 * 
 * @exclude
 */
public class BooleanDecoder extends ActionScriptDecoder
{
    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        Object result = null;

        if (encodedObject == null)
        {
            result = Boolean.FALSE;
        }
        else if (encodedObject instanceof Boolean)
        {
            result = encodedObject;
        }
        else if (encodedObject instanceof String)
        {
            String str = (String) encodedObject;
            result = Boolean.valueOf(str);
        }
        else
        {
            DecoderFactory.invalidType(encodedObject, desiredClass);
        }

        return result;
    }
}
