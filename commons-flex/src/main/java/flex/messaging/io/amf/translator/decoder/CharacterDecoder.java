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
 * Decode a java.lang.String or java.lang.Character to a java.lang.Character instance.
 * <p>
 * Note that a String must be non-zero length and only the first character in the String will be used.
 * </p>
 * 
 * @author Peter Farland
 * 
 * @exclude
 */
public class CharacterDecoder extends ActionScriptDecoder
{
    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        Character result = null;

        if (encodedObject == null)
        {
            char c = 0;
            result = new Character(c);
        }
        else if (encodedObject instanceof String)
        {
            String str = (String) encodedObject;

            char[] chars = str.toCharArray();

            if (chars.length > 0)
            {
                result = new Character(chars[0]);
            }
        }
        else if (encodedObject instanceof Character)
        {
            result = (Character) encodedObject;
        }

        if (result == null)
        {
            DecoderFactory.invalidType(encodedObject, desiredClass);
        }

        return result;
    }
}
