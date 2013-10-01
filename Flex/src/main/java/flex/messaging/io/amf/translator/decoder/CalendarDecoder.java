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

import java.util.Calendar;
import java.util.Date;

/**
 * Converts instances of java.util.Date, java.util.Calendar, and
 * java.lang.Number to instances of java.util.Calendar. If the incoming
 * object was not a Calendar, we create a new Calendar instance using the
 * default timezone and locale.
 *
 * If the incoming type was an AMF 3 Date we remember the translation
 * to Calendar in our list of known objects as Dates are considered
 * complex objects and can be sent by reference. We want to retain
 * pointers to Date instances in our representation of an ActionScript
 * object graph.
 *
 * @author Peter Farland
 *
 * @exclude
 */
public class CalendarDecoder extends ActionScriptDecoder
{
    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        Object result = null;

        if (encodedObject instanceof Date)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date)encodedObject);
            result = calendar;
        }
        else if (encodedObject instanceof Calendar)
        {
            result = encodedObject;
        }
        else if (encodedObject instanceof Number)
        {
            Calendar calendar = Calendar.getInstance();
            Number number = (Number)encodedObject;
            calendar.setTimeInMillis(number.longValue());
            result = calendar;
        }
        else
        {
            DecoderFactory.invalidType(encodedObject, desiredClass);
        }


        return result;
    }
}
