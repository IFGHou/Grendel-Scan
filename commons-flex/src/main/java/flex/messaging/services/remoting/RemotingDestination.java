/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.services.remoting;

import flex.management.runtime.messaging.services.remoting.RemotingDestinationControl;
import flex.messaging.FactoryDestination;
import flex.messaging.MessageBroker;
import flex.messaging.MessageException;
import flex.messaging.log.LogCategories;
import flex.messaging.services.RemotingService;
import flex.messaging.services.Service;
import flex.messaging.util.MethodMatcher;

/**
 * A logical reference to a RemotingDestination.
 */
public class RemotingDestination extends FactoryDestination
{
    static final long serialVersionUID = -8454338922948146048L;
    /** Log category for <code>RemotingDestination</code>. */
    public static final String LOG_CATEGORY = LogCategories.SERVICE_REMOTING;

    private static final String REMOTING_SERVICE_CLASS = "flex.messaging.services.RemotingService";

    // errors
    public static final int NO_MESSAGE_BROKER = 10163;
    private static final int NO_REMOTING_SERVICE = 10657;

    // RemotingDestination internal
    private MethodMatcher methodMatcher;

    private RemotingDestinationControl controller;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>RemotingDestination</code> instance.
     */
    public RemotingDestination()
    {
        this(false);
    }

    /**
     * Constructs a <code>RemotingDestination</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>RemotingDestination</code> is manageable; otherwise <code>false</code>.
     */
    public RemotingDestination(boolean enableManagement)
    {
        super(enableManagement);
    }

    /**
     * Retrieves the RemotingDestination for the supplied server id. If serverId is null, the default MessageBroker instance is returned. You use this version of this method to retrieve a
     * DataDestination if you are not in the context of processing a current message when you need the RemotingDestination.
     * 
     * @param serverId
     *            id of the server containing the remoting destination to be retrieved.
     * @param destinationName
     *            Name of the remoting destination to be retrieved.
     * 
     * @return remoting destination corresponding to the supplied server id and destination name
     */
    public static RemotingDestination getRemotingDestination(String serverId, String destinationName)
    {
        MessageBroker broker = MessageBroker.getMessageBroker(serverId);

        if (broker == null)
        {
            // Unable to locate a MessageBroker initialized with server id ''{0}''
            MessageException me = new MessageException();
            me.setMessage(NO_MESSAGE_BROKER, new Object[] { serverId });
            throw me;
        }

        RemotingService rs = (RemotingService) broker.getServiceByType(REMOTING_SERVICE_CLASS);
        if (rs == null)
        {
            // MessageBroker with server id ''{0}'' does not contain a service with class flex.messaging.remoting.RemotingService
            MessageException me = new MessageException();
            me.setMessage(NO_REMOTING_SERVICE, new Object[] { serverId });
            throw me;
        }

        return (RemotingDestination) rs.getDestination(destinationName);
    }

    // --------------------------------------------------------------------------
    //
    // Public Getters and Setters for Destination properties
    //
    // --------------------------------------------------------------------------

    /**
     * Returns the log category of the <code>RemotingDestination</code>.
     * 
     * @return The log category of the component.
     */
    @Override
    public String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Casts the <code>Service</code> into <code>RemotingService</code> and calls super.setService.
     * 
     * @param service
     *            the <code>RemotingService</code> to associate with this destination.
     */
    @Override
    public void setService(Service service)
    {
        RemotingService remotingService = (RemotingService) service;
        super.setService(remotingService);
        setMethodMatcher(remotingService.getMethodMatcher());
    }

    // --------------------------------------------------------------------------
    //
    // Other public APIs
    //
    // --------------------------------------------------------------------------
    /**
     * @exclude
     */
    public MethodMatcher getMethodMatcher()
    {
        return methodMatcher;
    }

    /**
     * @exclude
     */
    public void setMethodMatcher(MethodMatcher matcher)
    {
        methodMatcher = matcher;
    }

    // --------------------------------------------------------------------------
    //
    // Protected/private APIs
    //
    // --------------------------------------------------------------------------

    /**
     * Invoked automatically to allow the <code>RemotingDestination</code> to setup its corresponding MBean control.
     * 
     * @param service
     *            The <code>Service</code> that manages this <code>RemotingDestination</code>.
     */
    @Override
    protected void setupDestinationControl(Service service)
    {
        controller = new RemotingDestinationControl(this, service.getControl());
        controller.register();
        setControl(controller);
    }
}
