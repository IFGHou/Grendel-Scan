/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author david
 * 
 */
public class FilenameComponentDataReference implements DataReference
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final boolean name;
    public static final FilenameComponentDataReference NAME_COMPONENT = new FilenameComponentDataReference(true);
    public static final FilenameComponentDataReference EXTENSION_COMPONENT = new FilenameComponentDataReference(false);

    private FilenameComponentDataReference(final boolean name)
    {
        this.name = name;
    }

    @Override
    public FilenameComponentDataReference clone()
    {
        return new FilenameComponentDataReference(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return name ? "Is the file name" : "Is the file extension";
    }

    public final boolean isName()
    {
        return name;
    }

}
