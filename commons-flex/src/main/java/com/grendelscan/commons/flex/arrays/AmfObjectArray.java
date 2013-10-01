package com.grendelscan.commons.flex.arrays;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.interfaces.ArbitraryUnnamedChildren;

public class AmfObjectArray extends AmfPrimitiveArray implements ArbitraryUnnamedChildren
{
    private static final long serialVersionUID = 1L;

    public AmfObjectArray(final String name, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, AmfDataType.kObjectArray, parent, transactionId);
    }

    public AmfObjectArray(final String name, final AmfDataType type, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, type, parent, transactionId);
    }

    @Override
    public AmfDataType[] getChildTypes()
    {
        return AmfDataType.getCreatableTypes();
    }

}
