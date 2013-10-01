/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Jan 15, 2006
 */
package org.cobra_grendel.html;


/**
 * The <code>FormInput</code> class contains the state of an HTML form input item.
 */
public class FormInput
{
    // private final InputStream inputStream;
    // private final String charset;
    public static final FormInput[] EMPTY_ARRAY = new FormInput[0];
    private final java.io.File fileValue;
    private final String name;
    private final String textValue;

    /**
     * Constructs a <code>FormInput</code> with a file value.
     * 
     * @param name
     *            The name of the input.
     * @param value
     *            The value of the input.
     */
    public FormInput(final String name, final java.io.File value)
    {
        this.name = name;
        textValue = null;
        fileValue = value;
    }

    /**
     * Constructs a <code>FormInput</code> with a text value.
     * 
     * @param name
     *            The name of the input.
     * @param value
     *            The value of the input.
     */
    public FormInput(final String name, final String value)
    {
        super();
        this.name = name;
        textValue = value;
        fileValue = null;
    }

    /**
     * Always returns UTF-8.
     * 
     * @deprecated The method is implemented only to provide some backward compatibility.
     */
    @Deprecated
    public String getCharset()
    {
        return "UTF-8";
    }

    public java.io.File getFileValue()
    {
        return fileValue;
    }

    /**
     * Gets data as an input stream. The caller is responsible for closing the stream.
     * 
     * @deprecated Call either {@link #getTextValue()} or {@link #getFileValue()} instead.
     */
    @Deprecated
    public java.io.InputStream getInputStream() throws java.io.IOException
    {
        if (isText())
        {
            return new java.io.ByteArrayInputStream(getTextValue().getBytes("UTF-8"));
        }
        else if (isFile())
        {
            return new java.io.FileInputStream(getFileValue());
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the name of the input.
     */
    public String getName()
    {
        return name;
    }

    public String getTextValue()
    {
        return textValue;
    }

    public boolean isFile()
    {
        return fileValue != null;
    }

    public boolean isText()
    {
        return textValue != null;
    }

    /**
     * Shows a string representation of the <code>FormInput</code> that may be useful in debugging.
     * 
     * @see #getTextValue()
     */
    @Override
    public String toString()
    {
        return "FormInput[name=" + name + ",textValue=" + textValue + "]";
    }
}
