/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;


/**
 * @author david
 *
 */
public class SingleChildReference implements DataReference
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleChildReference.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final static SingleChildReference instance = new SingleChildReference();
	private SingleChildReference()
	{
		
	}
	
	@Override
	public DataReference clone()
	{
		return this;
	}

	public static final SingleChildReference getInstance()
	{
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
	 */
	@Override
	public String debugString()
	{
		return "Single child reference";
	}

}
