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
package flex.messaging.io.amf;

/**
 * The amf/rtmp data encoding format constants.
 */
public interface AmfTypes
{
    // AMF marker constants
    int kNumberType        = 0;
    int kBooleanType       = 1;
    int kStringType        = 2;
    int kObjectType        = 3;
    int kMovieClipType     = 4;
    int kNullType          = 5;
    int kUndefinedType     = 6;
    int kReferenceType     = 7;
    int kECMAArrayType     = 8;
    int kObjectEndType     = 9;
    int kStrictArrayType   = 10;
    int kDateType          = 11;
    int kLongStringType    = 12;
    int kUnsupportedType   = 13;
    int kRecordsetType     = 14;
    int kXMLObjectType     = 15;
    int kTypedObjectType   = 16;
    int kAvmPlusObjectType = 17;
}


