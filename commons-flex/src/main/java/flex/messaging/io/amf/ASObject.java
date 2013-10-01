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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a Flash Actionscript Object (typed or untyped)
 * <p>
 * Making a class rather than just deserializing to a HashMap was chosen for the following reasons:<br/>
 * 1) "types" are not going to be native to Hashmap, table, etc.<br/>
 * 2) it helps in making the deserializer/serializer reflexive.
 * </p>
 * 
 * @author Jim Whitfield (jwhitfield@macromedia.com)
 * @author Peter Farland
 * @version 1.0
 */
public class ASObject extends HashMap
{
    static final long serialVersionUID = 1613529666682805692L;
    private boolean inHashCode = false;
    private boolean inToString = false;

    /**
     * the named type, if any.
     */
    String namedType = null;

    /**
     * Create an Actionscript object.
     */
    public ASObject()
    {
        super();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Create a named Actionscript object.
    // * @param name the type of the object
    // */
    // public ASObject(String name)
    // {
    // super();
    // namedType = name;
    // }

    /**
     * get the named type, if any. (otherwise, return null, implying it is unnamed).
     * 
     * @return the type.
     */
    public String getType()
    {
        return namedType;
    }

    /**
     * Sets the named type. <br/>
     * This operation is mostly meaningless on an object that came in off the wire, but will be helpful for objects that will be serialized out to Flash.
     * 
     * @param type
     *            the type of the object.
     */
    public void setType(String type)
    {
        namedType = type;
    }

    /**
     * Return the hashcode of this object. The hashcode is defined to be the sum of the hashcodes of each entry.
     * 
     * @return
     */
    @Override
    public int hashCode()
    {
        int h = 0;
        if (!inHashCode)
        {
            inHashCode = true;
            try
            {
                Iterator i = entrySet().iterator();
                while (i.hasNext())
                {
                    h += i.next().hashCode();
                }
            }
            finally
            {
                inHashCode = false;
            }
        }
        return h;
    }

    /**
     * Returns a string representation of this object. The string representation consists of a list of key-value mappings in the order returned by the map's <tt>entrySet</tt> view's iterator, enclosed
     * in braces (<tt>"{}"</tt>). Adjacent mappings are separated by the characters <tt>", "</tt> (comma and space). Each key-value mapping is rendered as the key followed by an equals sign (
     * <tt>"="</tt>) followed by the associated value. Keys and values are converted to strings as by <tt>String.valueOf(Object)</tt>.
     * <p>
     * 
     * This implementation creates an empty string buffer, appends a left brace, and iterates over the map's <tt>entrySet</tt> view, appending the string representation of each <tt>map.entry</tt> in
     * turn. After appending each entry except the last, the string <tt>", "</tt> is appended. Finally a right brace is appended. A string is obtained from the stringbuffer, and returned.
     * <p>
     * 
     * If the value is found to be recursive, <tt>"..."</tt> (three periods) are printed to indicate the loop.
     * 
     * @return a String representation of this map.
     */
    @Override
    public String toString()
    {
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf('.');

        StringBuffer buffer = new StringBuffer();
        buffer.append(className.substring(dotIndex + 1));
        buffer.append("(").append(System.identityHashCode(this)).append(')');
        buffer.append('{');
        if (!inToString)
        {
            inToString = true;
            try
            {
                boolean pairEmitted = false;

                Iterator i = entrySet().iterator();
                while (i.hasNext())
                {
                    if (pairEmitted)
                    {
                        buffer.append(", ");
                    }
                    Map.Entry e = (Map.Entry) (i.next());
                    buffer.append(e.getKey()).append('=').append(e.getValue());
                    pairEmitted = true;
                }
            }
            finally
            {
                inToString = false;
            }
        }
        else
        {
            buffer.append("...");
        }
        buffer.append('}');
        return buffer.toString();
    }
}
