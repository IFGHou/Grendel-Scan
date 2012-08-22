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
 * AMF3 type markers and constants for AVM+ Serialization.
 *
 * @author Peter Farland
 * @see flex.messaging.io.amf.AmfTypes for AMF 0 Type Markers.
 */
public interface Amf3Types
{
    // AMF marker constants
    int kUndefinedType  = 0;
    int kNullType       = 1;
    int kFalseType      = 2;
    int kTrueType       = 3;
    int kIntegerType    = 4;
    int kDoubleType     = 5;
    int kStringType     = 6;
    int kXMLType        = 7;
    int kDateType       = 8;
    int kArrayType      = 9;
    int kObjectType     = 10;
    int kAvmPlusXmlType = 11;
    int kByteArrayType  = 12;

    String EMPTY_STRING = "";

    /**
     * Internal use only.
     * @exclude
     */
    int UINT29_MASK = 0x1FFFFFFF; // 2^29 - 1

    /**
     * The maximum value for an <code>int</code> that will avoid promotion to an
     * ActionScript Number when sent via AMF 3 is 2<sup>28</sup> - 1, or <code>0x0FFFFFFF</code>.
     */
    int INT28_MAX_VALUE = 0x0FFFFFFF; // 2^28 - 1

    /**
     * The minimum value for an <code>int</code> that will avoid promotion to an
     * ActionScript Number when sent via AMF 3 is -2<sup>28</sup> or <code>0xF0000000</code>.
     */
    int INT28_MIN_VALUE = 0xF0000000; // -2^28 in 2^29 scheme
}
