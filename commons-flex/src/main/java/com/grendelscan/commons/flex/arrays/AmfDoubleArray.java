package com.grendelscan.commons.flex.arrays;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.complexTypes.AmfActionMessageRoot;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

public class AmfDoubleArray extends AmfPrimitiveArray
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public AmfDoubleArray(final String name, final AbstractAmfDataContainer<?> parent, final int transactionId, final AmfActionMessageRoot amfRoot)
    {
        super(name, AmfDataType.kDoubleArray, parent, transactionId);
    }

    @Override
    public AmfDataType[] getChildTypes()
    {
        return new AmfDataType[] { AmfDataType.kDouble };
    }

}
