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

import flex.messaging.io.SerializationContext;
import flex.messaging.io.TypeMarshallingContext;

/**
 * Base class for Amf I/O. 
 * @exclude
 */
abstract class AmfIO
{
    protected final SerializationContext context;
    /*
     *  DEBUG LOGGING.
     */
    protected boolean isDebug;
    protected AmfTrace trace;

    /*
     *  OPTIMIZATION.
     */
    private char[] tempCharArray = null;
    private byte[] tempByteArray = null;

    AmfIO(SerializationContext context)
    {
        this.context = context;
    }

    /**
     * Turns on "trace" debugging for AMF responses.
     * @param trace the trace object
     */
    public void setDebugTrace(AmfTrace trace)
    {
        this.trace = trace;
        isDebug = this.trace != null;
    }

    /**
     * Clear all object reference information so that the instance
     * can be used to deserialize another data structure.
     * 
     * Reset should be called before reading a top level object,
     * such as a new header or a new body.
     */
    public void reset()
    {
        TypeMarshallingContext marshallingContext = TypeMarshallingContext.getTypeMarshallingContext();
        marshallingContext.reset();
    }

    /**
     * Returns an existing array with a length of at least the specified
     * capacity.  This method is for optimization only.  Do not use the array
     * outside the context of this method and do not call this method again
     * while the array is being used.
     * @param capacity minimum length
     * @return a character array 
     */
    final char[] getTempCharArray(int capacity)
    {
        char[] result = this.tempCharArray;
        if ((result == null) || (result.length < capacity))
        {
            result = new char[capacity * 2];
            tempCharArray = result;
        }
        return result;
    }

    /**
     * Returns an existing array with a length of at least the specified
     * capacity.  This method is for optimization only.  Do not use the array
     * outside the context of this method and do not call this method again
     * while the array is being used.
     * @param capacity minimum length
     * @return a byte array
     */
    final byte[] getTempByteArray(int capacity)
    {
        byte[] result = this.tempByteArray;
        if ((result == null) || (result.length < capacity))
        {
            result = new byte[capacity * 2];
            tempByteArray = result;
        }
        return result;
    }
}
