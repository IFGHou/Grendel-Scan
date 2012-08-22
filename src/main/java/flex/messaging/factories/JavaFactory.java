/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.factories;

import javax.servlet.ServletContext;

import flex.messaging.DestructibleFlexFactory;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexContext;
import flex.messaging.FlexFactory;
import flex.messaging.FlexSession;
import flex.messaging.FlexSessionListener;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationException;
import flex.messaging.config.ConfigurationManager;
import flex.messaging.log.Log;
import flex.messaging.services.ServiceException;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;

/**
 * This class implements the FlexFactory interface to constructs Flex messaging
 * components.  The JavaFactory uses the class name, specified as the source
 * attribute to determine the class for instances.   The scope attribute can be one of
 * session, application or request to determine its lifecycle.  If you
 * use application or session, you can specify the optional attribute-id
 * parameter to control the name of the key for storing the component.  Two destinations
 * using the same attribute-id will use the same component.  The component is stored
 * in the ServletContext (for application scoped components) and in the
 * session (for session scoped components) so you can use these components in your
 * JSP as well.
 *
 * @author Jeff Vroom
 */
public class JavaFactory implements FlexFactory, DestructibleFlexFactory
{
    private static final String ATTRIBUTE_ID = "attribute-id";

    private static final int SINGLETON_ERROR = 10656;
    private static final int SESSION_NOT_FOUND = 10652;
    private static final int INVALID_CLASS_FOUND = 10654;

    /**
     * @exclude
     * Default constructor
     */
    public JavaFactory()
    {
    }

    /**
     * This method can be used to provide additional configuration parameters
     * for the initializing this factory instance itself.
     */
    @Override public void initialize(String id, ConfigMap configMap) {}

    /**
     * This method is called when we initialize the definition of an instance which
     * will be looked up by this factory.  It should validate that the properties
     * supplied are valid to define an instance.  Any valid properties used for
     * this configuration must be accessed to avoid warnings about unused
     * configuration elements.  If your factory is only used for application
     * scoped components, you do not need to implement this method as the lookup
     * method itself can be used to validate its configuration.
     */
    @Override public FactoryInstance createFactoryInstance(String id, ConfigMap properties)
    {
        JavaFactoryInstance instance = new JavaFactoryInstance(this, id, properties);

        if (properties == null)
        {
            // Use destination id as the default attribute id to prevent unwanted sharing.
            instance.setSource(instance.getId());
            instance.setScope(SCOPE_REQUEST);
            instance.setAttributeId(id);
        }
        else
        {
            instance.setSource(properties.getPropertyAsString(SOURCE, instance.getId()));
            instance.setScope(properties.getPropertyAsString(SCOPE, SCOPE_REQUEST));
            // Use destination id as the default attribute id to prevent unwanted sharing.
            instance.setAttributeId(properties.getPropertyAsString(ATTRIBUTE_ID, id));
        }

        if (instance.getScope().equalsIgnoreCase(SCOPE_APPLICATION))
        {
            try
            {
                ServletContext ctx = FlexContext.getServletConfig().getServletContext();

                synchronized (ctx)
                {
                    Object inst = ctx.getAttribute(instance.getAttributeId());
                    if (inst == null)
                    {
                        inst = instance.createInstance();
                        ctx.setAttribute(instance.getAttributeId(), inst);
                    }
                    else
                    {
                        Class configuredClass = instance.getInstanceClass();
                        Class instClass = inst.getClass();
                        if (configuredClass != instClass &&
                                !configuredClass.isAssignableFrom(instClass))
                        {
                            ServiceException e = new ServiceException();
                            e.setMessage(INVALID_CLASS_FOUND, new Object[] {
                                    instance.getAttributeId(), "application", instance.getId(),
                                    instance.getInstanceClass(), inst.getClass()});
                            e.setCode("Server.Processing");
                            throw e;
                        }
                    }
                    instance.applicationInstance = inst;

                    // increment attribute-id reference count on MB
                    MessageBroker mb = FlexContext.getMessageBroker();
                    if (mb != null)
                    {
                        mb.incrementAttributeIdRefCount(instance.getAttributeId());
                    }
                }
            }
            catch (Throwable t)
            {
                ConfigurationException ex = new ConfigurationException();
                ex.setMessage(SINGLETON_ERROR, new Object[] { instance.getSource(), id });
                ex.setRootCause(t);

                if (Log.isError())
                    Log.getLogger(ConfigurationManager.LOG_CATEGORY).error(ex.getMessage() + StringUtils.NEWLINE + ExceptionUtil.toString(t));

                throw ex;
            }
        }
        else if(instance.getScope().equalsIgnoreCase(SCOPE_SESSION))
        {
            // increment attribute-id reference count on MB for Session scoped instances
            MessageBroker mb = FlexContext.getMessageBroker();
            if (mb != null)
            {
                mb.incrementAttributeIdRefCount(instance.getAttributeId());
            }
        }
        return instance;
    }

    /**
     * Returns the instance specified by the source
     * and properties arguments.  For the factory, this may mean
     * constructing a new instance, optionally registering it in some other
     * name space such as the session or JNDI, and then returning it
     * or it may mean creating a new instance and returning it.
     * This method is called for each request to operate on the
     * given item by the system so it should be relatively efficient.
     * <p>
     * If your factory does not support the scope property, it report an error
     * if scope is supplied in the properties for this instance.
     * </p>
     *
     * @param inst the FactoryInstance to lookup.
     * @return the constructed and initialized component for this factory instance.
     */
    @Override public Object lookup(FactoryInstance inst)
    {
        JavaFactoryInstance factoryInstance = (JavaFactoryInstance) inst;
        Object instance;

        if (factoryInstance.getScope().equalsIgnoreCase(SCOPE_APPLICATION))
        {
            instance = factoryInstance.applicationInstance;
        }
        else if (factoryInstance.getScope().equalsIgnoreCase(SCOPE_SESSION))
        {
            // See if an instance already exists in this http session first
            FlexSession session = FlexContext.getFlexSession();
            if (session != null)
            {
                instance = session.getAttribute(factoryInstance.getAttributeId());
                if (instance != null)
                {
                    Class configuredClass = factoryInstance.getInstanceClass();
                    Class instClass = instance.getClass();
                    if (configuredClass != instClass &&
                        !configuredClass.isAssignableFrom(instClass))
                    {
                        ServiceException e = new ServiceException();
                        e.setMessage(INVALID_CLASS_FOUND, new Object[] {
                                        factoryInstance.getAttributeId(),
                                        "session",
                                        factoryInstance.getId(),
                                        factoryInstance.getInstanceClass(), instance.getClass()});
                        e.setCode("Server.Processing");
                        throw e;
                    }
                }
                else
                {
                    // none exists - create it the first time for each session
                    instance = factoryInstance.createInstance();
                    session.setAttribute(factoryInstance.getAttributeId(), instance);
                }
            }
            else
                instance = null;

            if (instance == null)
            {
                ServiceException e = new ServiceException();
                e.setMessage(SESSION_NOT_FOUND, new Object[] {factoryInstance.getId()});
                e.setCode("Server.Processing");
                throw e;
            }
        }
        else
        {
            instance = factoryInstance.createInstance();
        }
        return instance;
    }

    /**
     * This method is called when a component using this factory is being destroyed.
     * When appropriate, it frees up resources that were used by the factory instance
     * and are no longer needed
     *
     * @param inst The FactoryInstance to be cleaned up
     */
    @Override public void destroyFactoryInstance(FactoryInstance inst)
    {
        JavaFactoryInstance factoryInstance = (JavaFactoryInstance) inst;

        // if we are stopping a destination with an Application or Session scoped assembler, we may
        // have to remove the assembler from the ServletContext or Session
        if (factoryInstance != null)
        {
            MessageBroker mb = FlexContext.getMessageBroker();
            String attributeId = factoryInstance.getAttributeId();

            if (FlexFactory.SCOPE_APPLICATION.equals(factoryInstance.getScope()))
            {

                ServletContext ctx = FlexContext.getServletConfig().getServletContext();

                // this should never be the case, but just in case
                if (ctx == null)
                    return;

                synchronized (ctx)
                {
                    // remove from ServletContext if reference count is zero
                    int refCount = (mb != null) ? mb.decrementAttributeIdRefCount(attributeId) : 0;
                    if (refCount <= 0)
                    {
                        // remove assembler from servlet context
                        ctx.removeAttribute(attributeId);
                    }
                }
            }
            else if (FlexFactory.SCOPE_SESSION.equals(factoryInstance.getScope()))
            {
                FlexSession session = FlexContext.getFlexSession();

                // if this is being stopped during runtime config, we should have a session available to us
                // However, if this is being stopped on MessageBroker shutdown, we will not have a session
                // but do not need to worry about clean up in that case as the entire session will be cleaned up
                if (session == null)
                    return;

                // remove from Session if reference count is zero
                int refCount = (mb != null) ? mb.decrementAttributeIdRefCount(attributeId) : 0;
                if (refCount <= 0)
                {
                    // remove assembler from servlet context
                    session.removeAttribute(attributeId);
                }
            }

            // Remove this instance from Session created listeners
            // Only helps if listener was created by the factory, but this is common (aka assembler classes)
            if (factoryInstance.applicationInstance instanceof FlexSessionListener)
            {
                FlexSession.removeSessionCreatedListener((FlexSessionListener) factoryInstance.applicationInstance);
            }
        }
    }
}

