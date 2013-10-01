package com.grendelscan.commons.flex.arrays;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.flex.AbstractAmfData;
import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfOutputStream;
import com.grendelscan.commons.flex.AmfUtils;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.interfaces.Orderable;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.http.dataHandling.containers.ExpandableDataContainer;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NumberedListDataReference;

public abstract class AmfPrimitiveArray extends AbstractAmfDataContainer<NumberedListDataReference> implements Orderable, ExpandableDataContainer<NumberedListDataReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AmfPrimitiveArray.class);
    private static final long serialVersionUID = 1L;
    private final ArrayList<AbstractAmfData> data;

    public AmfPrimitiveArray(final String name, final AmfDataType type, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, type, parent, false, transactionId);
        data = new ArrayList<AbstractAmfData>(1);
    }

    @Override
    public void addChild(final AbstractAmfData value)
    {
        data.add(value);
        value.setDeletable(true);
        renumberChildren();
    }

    @Override
    public void addChild(@SuppressWarnings("unused") final Data child)
    {
        throw new NotImplementedException("Can only have AMF children");
    }

    @Override
    public void addChild(final int index, final AbstractAmfData value)
    {
        data.add(index, value);
        value.setDeletable(true);
        renumberChildren();
    }

    @Override
    public void addChild(final NumberedListDataReference reference, final Data child)
    {
        data.add(reference.getIndex(), (AbstractAmfData) child);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#childrenDebugString()
     */
    @Override
    public String childrenDebugString()
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (AbstractAmfData child : getChildren())
        {
            sb.append("\n\t[");
            sb.append(i++);
            sb.append("]\n");
            sb.append(StringUtils.indentLines(child.debugString(), 1));
        }
        return sb.toString();
    }

    public AbstractAmfData get(final int index)
    {
        return data.get(index);
    }

    @Override
    public Data getChild(final NumberedListDataReference reference)
    {
        return data.get(reference.getIndex());
    }

    @Override
    public ArrayList<AbstractAmfData> getChildren()
    {
        return data;
    }

    @Override
    public NumberedListDataReference getChildsReference(final Data child)
    {
        int index = data.indexOf(child);
        if (index < 0)
        {
            throw new IllegalArgumentException("The passed data object is not a child of this container");
        }
        return new NumberedListDataReference(index);
    }

    @Override
    abstract public AmfDataType[] getChildTypes();

    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return getChild((NumberedListDataReference) reference);
    }

    @Override
    public Data[] getDataChildren()
    {
        return data.toArray(new Data[data.size()]);
    }

    public int getSize()
    {
        return data.size();
    }

    @Override
    public boolean isMutable()
    {
        return true;
    }

    @Override
    public boolean isValueLocked()
    {
        return true;
    }

    public void remove(final AbstractAmfData child)
    {
        data.remove(child);
    }

    @Override
    public void removeChild(final Data child)
    {
        data.remove(child);
    }

    @Override
    public void removeChild(final int index)
    {
        data.remove(index);
        renumberChildren();
    }

    public void renumberChildren()
    {
        for (int i = 0; i < data.size(); i++)
        {
            AbstractAmfData datum = data.get(i);
            datum.setName(getName() + "[" + i + "]");
        }
    }

    @Override
    public void replaceChild(final NumberedListDataReference reference, final Data child)
    {
        data.set(reference.getIndex(), (AbstractAmfData) child);
    }

    @Override
    public void setName(final String name)
    {
        super.setName(name);
        renumberChildren();
    }

    protected void writeArrayHeaderToStream(final AmfOutputStream outputStream)
    {
        try
        {
            writeCodeToStream(outputStream);
            if (useAmf3Code)
            {
                // Write out an invalid reference, storing the length in the unused
                // 28-bits.
                AmfUtils.writeUInt29(outputStream, getSize() << 1 | 1);

                // Send an empty string to imply no named keys
                AmfUtils.writeAMFUTF(outputStream, false, "", useAmf3Code);
            }
            else
            {
                outputStream.writeInt(getSize());
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Problem writing AMF array header: " + e.toString(), e);
        }
    }

    @Override
    public void writeBytes(final OutputStream out)
    {
        AmfOutputStream outputStream = AmfOutputStreamRegistry.getStream(out);
        writeArrayHeaderToStream(outputStream);

        for (Data d : data)
        {
            AbstractAmfData datum = (AbstractAmfData) d;
            datum.writeBytes(outputStream);
        }
    }

}
