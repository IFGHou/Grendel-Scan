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
package flex.messaging.util;

import flex.messaging.log.Log;

/**
 * Simple utility to trace an Object graph out to a StringBuffer.
 * 
 * Note that new lines are NOT added after the individual values in complex type properties.
 * 
 * @exclude
 */
public class ObjectTrace
{

    /*
     * This boolean is used for suppressing debug output for selected properties. The logger will check this before printing a property.
     */
    public boolean nextElementExclude;

    public ObjectTrace()
    {
        buffer = new StringBuffer(4096);
    }

    public ObjectTrace(int bufferSize)
    {
        buffer = new StringBuffer(bufferSize);
    }

    @Override
    public String toString()
    {
        return buffer.toString();
    }

    public void write(Object o)
    {
        if (m_nested <= 0)
            buffer.append(indentString());

        buffer.append(String.valueOf(o));
    }

    public void writeNull()
    {
        if (m_nested <= 0)
            buffer.append(indentString());

        buffer.append("null");
    }

    public void writeRef(int ref)
    {
        if (m_nested <= 0)
            buffer.append(indentString());

        buffer.append("(Ref #").append(ref).append(")");
    }

    public void writeString(String s)
    {
        if (m_nested <= 0)
            buffer.append(indentString());

        buffer.append("\"").append(s).append("\"");
    }

    public void startArray(String header)
    {
        if (header != null && header.length() > 0)
        {
            if (m_nested <= 0)
                buffer.append(indentString());

            buffer.append(header).append(newLine);
        }

        m_indent++;
        m_nested++;
    }

    public void arrayElement(int index)
    {
        buffer.append(indentString()).append("[").append(index).append("] = ");
    }

    public void endArray()
    {
        m_indent--;
        m_nested--;
    }

    public void startObject(String header)
    {
        if (header != null && header.length() > 0)
        {
            if (m_nested <= 0)
                buffer.append(indentString());

            buffer.append(header).append(newLine);
        }

        m_indent++;
        m_nested++;
    }

    public void namedElement(String name)
    {
        if (Log.isExcludedProperty(name))
        {
            nextElementExclude = true;
        }

        buffer.append(indentString()).append(name).append(" = ");
    }

    public void endObject()
    {
        m_indent--;
        m_nested--;
    }

    public void newLine()
    {
        boolean alreadyPadded = false;
        int length = buffer.length();

        if (length > 3)
        {
            String tail = buffer.substring(length - 3, length - 1); // Get last two chars in buffer
            alreadyPadded = tail.equals(newLine);
        }

        if (!alreadyPadded)
            buffer.append(newLine);
    }

    /**
     * Uses the static member, m_indent to create a string of spaces of the appropriate indentation.
     */
    protected String indentString()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m_indent; ++i)
        {
            sb.append("  ");
        }
        return sb.toString();
    }

    protected StringBuffer buffer;
    protected int m_indent;
    protected int m_nested;
    public static String newLine = StringUtils.NEWLINE;
}
