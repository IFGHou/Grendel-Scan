package com.grendelscan.commons.flex.arrays;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.complexTypes.AmfActionMessageRoot;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

public class AmfMessageHeaders extends AmfObjectArray
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public AmfMessageHeaders(final AbstractAmfDataContainer<?> parent, final int transactionId, final AmfActionMessageRoot amfRoot)
    {
        super("Message headers", AmfDataType.kAmfMessageHeaders, parent, transactionId);
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
