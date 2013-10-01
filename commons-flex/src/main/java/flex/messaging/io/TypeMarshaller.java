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
package flex.messaging.io;

/**
 * A utility to convert between data types, useful for mapping loosely typed client classes to more strongly typed server classes.
 */
public interface TypeMarshaller
{
    /**
     * Creates an instance of the desired class without populating the type.
     * 
     * @param source
     *            The raw <tt>Object</tt> to be converted into an instance of the desired class.
     * @param desiredClass
     *            The type to which the source needs to be converted.
     * @return An instance of the desired class.
     */
    Object createInstance(Object source, Class desiredClass);

    /**
     * Converts the supplied source instance to an instance of the desired <tt>Class</tt>.
     * 
     * @param source
     *            The source instance.
     * @param desiredClass
     *            The type to which the source needs to be converted.
     * @return The converted instance of the desired class.
     */
    Object convert(Object source, Class desiredClass);

}
