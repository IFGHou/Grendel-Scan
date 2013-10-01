/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author david
 * 
 */
public class NumberedListDataReference implements DataReference
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private int index;

    public NumberedListDataReference(final int index)
    {
        this.index = index;
    }

    @Override
    public NumberedListDataReference clone()
    {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return "Index #" + index;
    }

    public int getIndex()
    {
        return index;
    }

    public final void setIndex(final int index)
    {
        this.index = index;
    }

}
