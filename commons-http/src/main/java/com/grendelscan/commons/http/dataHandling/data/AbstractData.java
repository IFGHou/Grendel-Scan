/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.data;

import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;

/**
 * @author david
 * 
 */
public abstract class AbstractData implements Data
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private DataContainer<?> parent;
    private int transactionId;

    public AbstractData(final DataContainer<?> parent, final int transactionId)
    {
        this.parent = parent;
        this.transactionId = transactionId;
    }

    public String abstractDataDebugString()
    {
        StringBuilder sb = new StringBuilder();
        // sb.append("Parent:\n");
        // if (parent == null)
        // {
        // sb.append("\t<null>\n");
        // }
        // else
        // {
        // sb.append(StringUtils.indentLines(parent.debugString(), 1));
        // }
        sb.append("Reference: ");
        sb.append(getReference().debugString());
        sb.append("\nTransaction ID: ");
        sb.append(transactionId);
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataContainers.Data#getParent()
     */
    @Override
    public DataContainer<?> getParent()
    {
        return parent;
    }

    @Override
    public DataReference getReference()
    {
        return parent.getChildsReference(this);
    }

    @Override
    public DataReferenceChain getReferenceChain()
    {
        return DataContainerUtils.getReferenceChain(this);
    }

    @Override
    public final int getTransactionId()
    {
        return transactionId;
    }

    // @Override
    // public DataReference getReference()
    // {
    // return reference;
    // }

    // public final void setReference(DataReference reference)
    // {
    // this.reference = reference;
    // }

    @Override
    public boolean isDataAncestor(final DataContainer<?> container)
    {
        DataContainer<?> ancestor = parent;
        while (ancestor != null)
        {
            if (ancestor == container)
            {
                return true;
            }
            ancestor = ancestor.getParent();
        }
        return false;
    }

    @Override
    public void removeFromCollection()
    {
        parent.removeChild(this);
    }

    protected void setParent(final DataContainer<?> parent)
    {
        this.parent = parent;
    }

    @Override
    public void setTransactionId(final int transactionId)
    {
        this.transactionId = transactionId;
    }

}
