package com.grendelscan.commons.flex.arrays;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.complexTypes.AmfActionMessageRoot;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

public class AmfMessageBodies extends AmfObjectArray
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public AmfMessageBodies(final AbstractAmfDataContainer<?> parent, final int transactionId, final AmfActionMessageRoot amfRoot)
    {
        super("Message bodies", AmfDataType.kAmfMessageBodies, parent, transactionId);
    }

    @Override
    public boolean isDeletable()
    {
        return false;
    }

    @Override
    public boolean isNameLocked()
    {
        return true;
    }

    @Override
    public boolean isTypeLocked()
    {
        return true;
    }

}
