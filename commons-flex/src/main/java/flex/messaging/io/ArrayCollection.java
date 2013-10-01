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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Used to map to client mx.collections.ArrayCollection to java.util.Lists in Java.
 */
public class ArrayCollection extends ArrayList implements Externalizable
{
    private static final long serialVersionUID = 8037277879661457358L;

    private SerializationDescriptor descriptor = null;

    public ArrayCollection()
    {
        super();
    }

    public ArrayCollection(Collection c)
    {
        super(c);
    }

    public ArrayCollection(int initialCapacity)
    {
        super(initialCapacity);
    }

    public Object[] getSource()
    {
        return toArray();
    }

    public void setDescriptor(SerializationDescriptor desc)
    {
        this.descriptor = desc;
    }

    public void setSource(Object[] s)
    {
        if (s != null)
        {
            if (size() > 0)
                clear();

            for (int i = 0; i < s.length; i++)
            {
                add(s[i]);
            }
        }
        else
        {
            clear();
        }
    }

    public void setSource(Collection s)
    {
        addAll(s);
    }

    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException
    {
        Object s = input.readObject();
        if (s instanceof Collection)
            s = ((Collection) s).toArray();
        Object[] source = (Object[]) s;
        setSource(source);
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException
    {
        if (descriptor == null)
            output.writeObject(getSource());
        else
        {
            Object[] source = getSource();
            if (source == null)
                output.writeObject(null);
            else
            {
                for (int i = 0; i < source.length; i++)
                {
                    Object item = source[i];
                    if (item == null)
                        source[i] = null;
                    else
                    {
                        PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
                        proxy = (PropertyProxy) proxy.clone();
                        proxy.setDescriptor(descriptor);
                        proxy.setDefaultInstance(item);
                        source[i] = proxy;
                    }
                }
                output.writeObject(source);
            }
        }
    }
}
