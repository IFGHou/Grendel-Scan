package com.grendelscan.commons.flex;

import java.io.IOException;
import java.util.ArrayList;

import com.grendelscan.commons.flex.dataTypeDefinitions.AmfConstants;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.http.dataHandling.data.AbstractData;

public abstract class AbstractAmfData extends AbstractData
{
    private static final long serialVersionUID = 1L;
    private String name;
    private AmfDataType type;
    protected final static int UNKNOWN_CONTENT_LENGTH = -1;
    protected boolean typeLocked = false;
    protected boolean nameLocked = false;
    protected boolean valueLocked = false;
    protected boolean deletable = false;
    protected boolean useAmf3Code;
    protected boolean amf3Parent;
    protected boolean forceAmf3Code;

    // protected AbstractAmfData(String name, AmfDataType type, boolean amf3Parent, boolean useAmf3Code,
    // AbstractAmfDataContainer<?> parent, DataReference reference, int transactionId)
    // {
    // super(parent, reference, transactionId);
    // this.name = name;
    // this.type = type;
    // this.useAmf3Code = useAmf3Code;
    // this.amf3Parent = amf3Parent;
    // }

    protected AbstractAmfData(final String name, final AmfDataType type, final AbstractAmfDataContainer<?> parent, final boolean forceAmf3Code, final int transactionId)
    {
        super(parent, transactionId);
        this.name = name;
        this.type = type;
        this.forceAmf3Code = forceAmf3Code;
        if (parent != null)
        {
            amf3Parent = parent.isUseAmf3Code();
        }
        else
        {
            amf3Parent = false;
        }
        useAmf3Code = forceAmf3Code || amf3Parent || type.getAmf3Code() == AmfConstants.a3ObjectType;

    }

    @Override
    public String debugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ");
        sb.append(name);
        sb.append("\nType: ");
        sb.append(type.toString());
        sb.append("\ntypeLocked: ");
        sb.append(typeLocked);
        sb.append("\nnameLocked: ");
        sb.append(nameLocked);
        sb.append("\nvalueLocked: ");
        sb.append(valueLocked);
        sb.append("\ndeletable: ");
        sb.append(deletable);
        sb.append("\nuseAmf3Code: ");
        sb.append(useAmf3Code);
        sb.append("\namf3Parent: ");
        sb.append(amf3Parent);
        sb.append("\nforceAmf3Code: ");
        sb.append(forceAmf3Code);

        return null;
    }

    public abstract ArrayList<AbstractAmfData> getChildren();

    public String getName()
    {
        return name;
    }

    public AmfDataType getType()
    {
        return type;
    }

    public boolean isDeletable()
    {
        return deletable;
    }

    public boolean isNameLocked()
    {
        return nameLocked;
    }

    public boolean isTypeLocked()
    {
        return typeLocked;
    }

    public boolean isUseAmf3Code()
    {
        return useAmf3Code;
    }

    public boolean isValueLocked()
    {
        return valueLocked;
    }

    public void setDeletable(final boolean deletable)
    {
        this.deletable = deletable;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public void setNameLocked(final boolean nameLocked)
    {
        this.nameLocked = nameLocked;
    }

    protected void setParent(final AbstractAmfDataContainer<?> parent)
    {
        super.setParent(parent);
        if (parent != null)
        {
            amf3Parent = parent.isUseAmf3Code();
        }
        else
        {
            amf3Parent = false;
        }
        useAmf3Code = forceAmf3Code || amf3Parent || type.getAmf3Code() == AmfConstants.a3ObjectType;
    }

    public void setType(final AmfDataType type)
    {
        this.type = type;
    }

    // public abstract void writeBytesToStream(AmfOutputStream outputStream) throws IOException;

    public void setTypeLocked(final boolean typeLocked)
    {
        this.typeLocked = typeLocked;
    }

    public void setValueLocked(final boolean valueLocked)
    {
        this.valueLocked = valueLocked;
    }

    public void writeCodeToStream(final AmfOutputStream outputStream) throws IOException
    {
        if (useAmf3Code && !outputStream.isAmf3Active())
        {
            outputStream.write(AmfConstants.kAvmPlusObjectType);
            outputStream.setAmf3Active(true);
        }
        type.writeCode(outputStream, useAmf3Code);
    }

}
