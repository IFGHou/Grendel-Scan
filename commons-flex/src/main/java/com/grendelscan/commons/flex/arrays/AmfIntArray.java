package com.grendelscan.commons.flex.arrays;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

public class AmfIntArray extends AmfPrimitiveArray
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public AmfIntArray(final String name, final AbstractAmfDataContainer<?> parent, final int transactionId)
    {
        super(name, AmfDataType.kIntArray, parent, transactionId);
    }

    @Override
    public AmfDataType[] getChildTypes()
    {
        return new AmfDataType[] { AmfDataType.kInteger };
    }
}
