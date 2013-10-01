package flex.messaging.io;

import java.util.List;

import com.grendelscan.commons.flex.AmfUtils;
import com.grendelscan.commons.flex.complexTypes.AmfServerSideObject;
import com.grendelscan.commons.http.dataHandling.references.NameOrValueReference;

public class ServerSideObjectProxy extends AbstractProxy
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ServerSideObjectProxy()
    {
        this(null);
    }

    protected ServerSideObjectProxy(final Object defaultInstance)
    {
        super(defaultInstance);
    }

    @Override
    public String getAlias(final Object instance)
    {
        return AmfServerSideObject.class.getName();
    }

    @Override
    public List getPropertyNames(final Object instance)
    {
        return ((AmfServerSideObject) instance).getPropertyNames();
    }

    @Override
    public Class getType(final Object instance, final String propertyName)
    {
        AmfServerSideObject sso = (AmfServerSideObject) instance;
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

    @Override
    public Object getValue(final Object instance, final String propertyName)
    {
        AmfServerSideObject sso = (AmfServerSideObject) instance;
        return sso.getProperty(propertyName);
    }

    @Override
    public void setValue(final Object instance, final String propertyName, final Object value)
    {
        AmfServerSideObject sso = (AmfServerSideObject) instance;
        sso.setProperty(propertyName, AmfUtils.parseAmfData(value, sso, NameOrValueReference.VALUE, -3));
    }

}
