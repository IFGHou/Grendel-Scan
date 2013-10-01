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
package flex.messaging.io.amf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

/**
 * This simple interface allows the MessageDeserializer to handle multiple versions of AMF.
 * 
 * Entry point for deserializing an individual ActionMessage request message for AMF, AMFX or RTMP.
 * 
 * @author Peter Farland
 * 
 * @see flex.messaging.io.amf.Amf0Input Classic Version 0 Deserializer
 * @see flex.messaging.io.amf.Amf3Input AVM+ Version 3 Deserializer
 * 
 * @see flex.messaging.io.amf.ActionMessageOutput The serialization equivalent
 */
public interface ActionMessageInput extends ObjectInput
{
    // java.io.ObjectInput METHODS

    /**
     * Reads in an AMF formatted Object.
     * 
     * <p>
     * The following is a guide to the ActionScript to Java type mappings:
     * <table cellpadding="2" cellspacing="2" border="1">
     * <tr>
     * <td width="50%"><b>ActionScript Type (AMF 3)</b></td>
     * <td width="50%"><b>Java Type</b></td>
     * </tr>
     * <tr>
     * <td>String</td>
     * <td>java.lang.String</td>
     * </tr>
     * <tr>
     * <td>Boolean</td>
     * <td>java.lang.Boolean</td>
     * </tr>
     * <tr>
     * <td>int<br />
     * uint</td>
     * <td>java.lang.Integer</td>
     * </tr>
     * <tr>
     * <td>Number</td>
     * <td>java.lang.Double</td>
     * </tr>
     * <tr>
     * <td>Date</td>
     * <td>java.util.Date</td>
     * </tr>
     * <tr>
     * <td>ByteArray</td>
     * <td>java.util.Byte.TYPE[]</td>
     * </tr>
     * <tr>
     * <td>mx.collections.ArrayCollection</td>
     * <td>flex.messaging.io.ArrayCollection</td>
     * </tr>
     * <tr>
     * <td>Array</td>
     * <td>java.lang.Object[] (Native Array)</td>
     * </tr>
     * <tr>
     * <td>Object</td>
     * <td>java.util.Map</td>
     * </tr>
     * <tr>
     * <td>flash.utils.IExternalizable</td>
     * <td>java.io.Externalizable</td>
     * </tr>
     * <tr>
     * <td>Typed Object (other than the above)</td>
     * <td>An instance of type Class (java.lang.Object)</td>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>null</td>
     * </tr>
     * </table>
     * </p>
     * 
     * <p>
     * <table cellpadding="2" cellspacing="2" border="1">
     * <tr>
     * <td width="50%"><b>ActionScript Type (AMF 0)</b></td>
     * <td width="50%"><b>Java Type</b></td>
     * </tr>
     * <tr>
     * <td>String</td>
     * <td>java.lang.String</td>
     * </tr>
     * <tr>
     * <td>Boolean</td>
     * <td>java.lang.Boolean</td>
     * </tr>
     * <tr>
     * <td>int<br />
     * uint</td>
     * <td>java.lang.Double</td>
     * </tr>
     * <tr>
     * <td>Number</td>
     * <td>java.lang.Double</td>
     * </tr>
     * <tr>
     * <td>Date</td>
     * <td>java.util.Date</td>
     * </tr>
     * <tr>
     * <td>ByteArray</td>
     * <td>(Not supported)</td>
     * </tr>
     * <tr>
     * <td>mx.collections.ArrayCollection</td>
     * <td>flex.messaging.io.ArrayCollection</td>
     * </tr>
     * <tr>
     * <td>Array</td>
     * <td>java.lang.Object[] (Native Array)</td>
     * </tr>
     * <tr>
     * <td>Object</td>
     * <td>java.util.Map</td>
     * </tr>
     * <tr>
     * <td>flash.utils.IExternalizable</td>
     * <td>(Not supported)</td>
     * </tr>
     * <tr>
     * <td>Typed Object (other than the above)</td>
     * <td>An instance of type Class (java.lang.Object)</td>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>null</td>
     * </tr>
     * </table>
     * </p>
     */
    @Override
    Object readObject() throws ClassNotFoundException, IOException;

    //
    // INITIALIZATION UTILITIES
    //

    /**
     * Internal use only.
     * 
     * @exclude
     */
    void reset();

    /**
     * Internal use only.
     * 
     * @exclude
     */
    void setDebugTrace(AmfTrace trace);

    /**
     * Internal use only.
     * 
     * @exclude
     */
    void setInputStream(InputStream in);
}
