/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author david
 * 
 */
public class NameOrValueReference implements DataReference
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final boolean name;
    public static final NameOrValueReference NAME = new NameOrValueReference(true);
    public static final NameOrValueReference VALUE = new NameOrValueReference(false);

    private NameOrValueReference(final boolean name)
    {
        this.name = name;
    }

    @Override
    public NameOrValueReference clone()
    {
        return new NameOrValueReference(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return name ? "Is the name component" : "Is the value component";
    }

    public final boolean isName()
    {
        return name;
    }

}
