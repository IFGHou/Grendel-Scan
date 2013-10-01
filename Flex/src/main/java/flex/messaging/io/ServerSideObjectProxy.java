package flex.messaging.io;

import java.util.List;

import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfServerSideObject;
import com.grendelscan.utils.AmfUtils;


public class ServerSideObjectProxy extends AbstractProxy
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ServerSideObjectProxy()
    {
	    this(null);
    }
	
	protected ServerSideObjectProxy(Object defaultInstance)
    {
	    super(defaultInstance);
    }

	@Override public String getAlias(Object instance)
	{
		return AmfServerSideObject.class.getName();
	}
	
	@Override public List getPropertyNames(Object instance)
	{
		return ((AmfServerSideObject) instance).getPropertyNames();
	}
	
	@Override public Class getType(Object instance, String propertyName)
	{
		AmfServerSideObject sso =  (AmfServerSideObject) instance;
		Object property = sso.getProperty(propertyName);
		if (property == null)
		{
			return Object.class;
		}
		else
		{
			return property.getClass();
		}
	}
	
	@Override public Object getValue(Object instance, String propertyName)
	{
		AmfServerSideObject sso =  (AmfServerSideObject) instance;
		return sso.getProperty(propertyName);
	}
	
	@Override public void setValue(Object instance, String propertyName, Object value)
	{
		AmfServerSideObject sso =  (AmfServerSideObject) instance;
		sso.setProperty(propertyName, AmfUtils.parseAmfData(value, sso, -3, true));
	}
	
}
