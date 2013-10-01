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

/**
 * Converts java.util.Date, java.sql.Date, java.util.Calendar or
 * java.lang.Number (via longValue) instances to a java.util.Date,
 * taking into consideration the SQL specific java.sql.Date type
 * which is required by Hibernate users.
 *
 * If the incoming type was an AMF 3 Date, we remember the translation
 * to Calendar in our list of known objects as Dates are considered
 * complex objects and can be sent by reference. We want to retain
 * pointers to Date instances in our representation of an ActionScript
 * object graph.
 *
 * @author Peter Farland
 *
 * @exclude
 */
public class DateDecoder extends ActionScriptDecoder
{
    @Override public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        java.util.Date result = null;

        if (java.sql.Date.class.isAssignableFrom(desiredClass))
        {
            if (encodedObject instanceof java.util.Date)
            {
                java.util.Date date = (java.util.Date)encodedObject;
                result = new java.sql.Date(date.getTime());
            }
            else if (encodedObject instanceof Calendar)
            {
                Calendar calendar = (Calendar)encodedObject;
                result = new java.sql.Date(calendar.getTimeInMillis());
            }
            else if (encodedObject instanceof Number)
            {
                Number number = (Number)encodedObject;
                result = new java.sql.Date(number.longValue());
            }
        }
        else if (java.sql.Timestamp.class.isAssignableFrom(desiredClass))
        {
            if (encodedObject instanceof java.util.Date)
            {
                java.util.Date date = (java.util.Date)encodedObject;
                result = new java.sql.Timestamp(date.getTime());
            }
            else if (encodedObject instanceof Calendar)
            {
                Calendar calendar = (Calendar)encodedObject;
                result = new java.sql.Timestamp(calendar.getTimeInMillis());
            }
            else if (encodedObject instanceof Number)
            {
                Number number = (Number)encodedObject;
                result = new java.sql.Timestamp(number.longValue());
            }
        }
        else if (java.sql.Time.class.isAssignableFrom(desiredClass))
        {
            if (encodedObject instanceof java.util.Date)
            {
                java.util.Date date = (java.util.Date)encodedObject;
                result = new java.sql.Time(date.getTime());
            }
            else if (encodedObject instanceof Calendar)
            {
                Calendar calendar = (Calendar)encodedObject;
                result = new java.sql.Time(calendar.getTimeInMillis());
            }
            else if (encodedObject instanceof Number)
            {
                Number number = (Number)encodedObject;
                result = new java.sql.Time(number.longValue());
            }
        }
        else if (java.util.Date.class.isAssignableFrom(desiredClass))
        {
            if (encodedObject instanceof java.util.Date)
            {
                result = (java.util.Date)encodedObject;
            }
            else if (encodedObject instanceof Calendar)
            {
                Calendar calendar = (Calendar)encodedObject;
                result = calendar.getTime();
            }
            else if (encodedObject instanceof Number)
            {
                Number number = (Number)encodedObject;
                result = new java.util.Date(number.longValue());
            }
        }

        if (result == null)
        {
            DecoderFactory.invalidType(encodedObject, desiredClass);
        }

        return result;
    }
}
