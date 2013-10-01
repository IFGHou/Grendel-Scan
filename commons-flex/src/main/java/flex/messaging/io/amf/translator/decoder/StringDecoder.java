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
 * Decode an ActionScript String, Number or Boolean to a Java String.
 * 
 * @exclude
 */
public class StringDecoder extends ActionScriptDecoder
{
    public StringDecoder()
    {
    }

    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        if (encodedObject instanceof String)
        {
            return encodedObject;
        }
        else if (encodedObject instanceof Number)
        {
            Number num = (Number) encodedObject;
            return new Double(num.doubleValue()).toString();
        }
        else if (encodedObject instanceof Boolean)
        {
            Boolean bool = (Boolean) encodedObject;
            if (bool.booleanValue())
            {
                return "true";
            }
            else
            {
                return "false";
            }
        }

        return shell;
    }
}
