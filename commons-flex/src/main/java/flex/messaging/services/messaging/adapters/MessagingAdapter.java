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
package flex.messaging.services.messaging.adapters;

import flex.messaging.config.ConfigMap;
import flex.messaging.config.DestinationSettings;
import flex.messaging.security.MessagingSecurity;
import flex.messaging.services.ServiceAdapter;
import flex.messaging.services.messaging.Subtopic;

/**
 * Base adapter class for publish/subscribe messaging adapters. You extend this class if you want to implement your own messaging adapter. A custom messaging adapter has the ability to implement
 * authorization logic for specific subtopics, and may also control how messages are routed. A more advanced messaging adapter can take control over the subscription process and have complete control
 * over the subscription process for producers and consumers.
 * <p>
 * All messaging adapters must provide an implementation for the invoke method. A simple invoke implementation which would give you similar functionality as the ActionScriptAdapter is simply:
 * 
 * <pre>
 * public Object invoke(Message message)
 * {
 *     MessageService msgService = (MessageService) service;
 *     msgService.pushMessageToClients(message, true);
 *     msgService.sendPushMessageFromPeer(message, true);
 *     return null;
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * This method is called for each data message sent from the client. It gets a reference to the MessageService which is controlling delivery of messages for this adapter. It uses the
 * pushMessageToClients method to send the message to all clients connected to this server. It then uses the sendPushMessageFromPeer method to send the message to other servers which will then route
 * the message to each client connected to those servers. In both cases, we pass the "evalSelector" parameter as true. This indicates that the message service will only send the message to those
 * clients whose selector pattern evaluates to true for the supplied message. If you supply false, the selector pattern is ignored and the message is delivered to the clients even if the pattern
 * evaluates to false.
 * </p>
 * <p>
 * The default behavior is for the messaging adapter to use the builtin FDS subscription mechanism. The client sends subscribe and unsubscribe command messages which are managed by the MessageService,
 * not the adapter. If you override the "handlesSubscriptions" method to return true, your adapter's manage method is called for each of these command messages instead. You must then override this
 * method to provide an implementation for these operations. See the docs on the CommandMessage class for details on the message format.
 * </p>
 * 
 * @see flex.messaging.services.ServiceAdapter
 * @see flex.messaging.services.MessageService
 * @see flex.messaging.messages.Message
 * @see flex.messaging.messages.CommandMessage
 */
public abstract class MessagingAdapter extends ServiceAdapter implements MessagingSecurity
{
    /**
     * Constraint manager used to assert authorization of send and subscribe related operations.
     */
    private MessagingSecurityConstraintManager constraintManager;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>MessagingAdapter</code> instance.
     */
    public MessagingAdapter()
    {
        this(false);
    }

    /**
     * Constructs a <code>MessagingAdapter</code> instance.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>MessagingAdapter</code> has a corresponding MBean control for management; otherwise <code>false</code>.
     */
    public MessagingAdapter(boolean enableManagement)
    {
        super(enableManagement);
    }

    // --------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    // --------------------------------------------------------------------------

    /**
     * Initializes the <code>MessagingAdapter</code> with the properties. Subclasses should call <code>super.initialize</code>.
     * 
     * @param id
     *            Id of the <code>MessagingAdapter</code>.
     * @param properties
     *            Properties for the <code>MessagingAdapter</code>.
     */
    @Override
    public void initialize(String id, ConfigMap properties)
    {
        super.initialize(id, properties);

        if (properties == null || properties.size() == 0)
            return;

        ConfigMap server = properties.getPropertyAsMap(DestinationSettings.SERVER_ELEMENT, null);
        if (server != null)
        {
            if (constraintManager == null)
                constraintManager = new MessagingSecurityConstraintManager(getDestination().getService().getMessageBroker());
            constraintManager.createConstraints(server);
        }
    }

    /**
     * Verifies that the <code>MessagingAdapter</code> is in valid state before it is started. If subclasses override, they must call <code>super.validate()</code>.
     */
    @Override
    protected void validate()
    {
        if (isValid())
            return;

        super.validate();

        // Initialize constraint manager to avoid null pointer checks even though
        // it won't do any real constraint checking.
        if (constraintManager == null)
            constraintManager = new MessagingSecurityConstraintManager(getDestination().getService().getMessageBroker());
    }

    // --------------------------------------------------------------------------
    //
    // Public Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Implements flex.messaging.security.MessagingSecurity. This method is invoked before a client subscribe request is processed, so that custom application logic can determine whether the client
     * should be allowed to subscribe to the specified subtopic. You can access the current user via <code>FlexContext.getUserPrincipal()</code>.
     * 
     * @param subtopic
     *            The subtopic the client is attempting to subscribe to.
     * @return true to allow the subscription, false to prevent it.
     */
    @Override
    public boolean allowSubscribe(Subtopic subtopic)
    {
        return true;
    }

    /**
     * Implements flex.messaging.security.MessagingSecurity. This method is invoked before a client message is sent to a subtopic, so that custom application logic can determine whether the client
     * should be allowed to send to the specified subtopic. You can access the current user via <code>FlexContext.getUserPrincipal()</code>.
     * 
     * @param subtopic
     *            The subtopic the client is attempting to send a message to.
     * @return true to allow the message to be sent, false to prevent it.
     */
    @Override
    public boolean allowSend(Subtopic subtopic)
    {
        return true;
    }

    /**
     * Gets the <code>MessagingSecurityConstraintManager</code> of the <code>MessagingAdapter</code>.
     * 
     * @return The <code>MessagingSecurityConstraintManager</code> of the <code>MessagingAdapter</code>.
     */
    public MessagingSecurityConstraintManager getSecurityConstraintManager()
    {
        return constraintManager;
    }

    /**
     * Sets the <code>MessagingSecurityConstraintManager</code> of the <code>MessagingAdapter</code>.
     * 
     * @param constraintManager
     *            The <code>MessagingSecurityConstraintManager</code> of the <code>MessagingAdapter</code>.
     */
    public void setSecurityConstraintManager(MessagingSecurityConstraintManager constraintManager)
    {
        this.constraintManager = constraintManager;
    }
}
