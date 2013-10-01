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
package flex.messaging.services;

import flex.management.runtime.messaging.services.RemotingServiceControl;
import flex.management.runtime.messaging.services.remoting.RemotingDestinationControl;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import flex.messaging.MessageException;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;
import flex.messaging.messages.MessagePerformanceUtils;
import flex.messaging.messages.RemotingMessage;
import flex.messaging.services.remoting.RemotingDestination;
import flex.messaging.util.MethodMatcher;

/**
 * The <code>RemotingService</code> processes <code>RemotingMessage</code>s.
 * A <code>RemotingMessage</code> informs a service adapter which
 * method to invoke on a service, provides the input parameters for the invocation,
 * as well as any other settings relevant to the adapter, such as RunAs credential
 * information for secured services.
 * <p>
 * The <code>RemotingService</code> must be initialized with configuration settings
 * before it can successfully process any RemotingMessages.
 * </p>
 * <p>
 * Note that the <code>RemotingService</code> translates a destination into a
 * service name or &quot;source&quot; that is meaningful to an
 * adapter before invocation.
 * </p>
 *
 * @author Sean Neville
 * @author Peter Farland
 */
public class RemotingService extends AbstractService
{
    /**
     * Log category for <code>RemotingService</code>.
     */
    public static final String LOG_CATEGORY = LogCategories.SERVICE_REMOTING;

    // Errors
    private static final int UNKNOWN_DESTINATION = 10650;

    // RemotingService internal
    private MethodMatcher methodMatcher;

    private RemotingServiceControl controller;

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>RemotingService</code>.
     */
    public RemotingService()
    {
        this(false);
    }

    /**
     * Constructs a <code>RemotingService</code> with the indicated management.
     *
     * @param enableManagement <code>true</code> if the <code>RemotingService</code>
     * is manageable; otherwise <code>false</code>.
     */
    public RemotingService(boolean enableManagement)
    {
        super(enableManagement);
    }

    //--------------------------------------------------------------------------
    //
    // Public Getters and Setters for RemotingService properties
    //
    //--------------------------------------------------------------------------

    /**
     * Creates a <code>RemotingDestination</code> instance, sets its id, sets it manageable
     * if the <code>AbstractService</code> that created it is manageable,
     * and sets its <code>Service</code> to the <code>AbstractService</code> that
     * created it.
     *
     * @param id The id of the <code>RemotingDestination</code>.
     * @return The <code>Destination</code> instanced created.
     */
    @Override public Destination createDestination(String id)
    {
        RemotingDestination destination = new RemotingDestination();
        destination.setId(id);
        destination.setManaged(isManaged());
        destination.setService(this);

        return destination;
    }

    /**
     * Casts the <code>Destination</code> into <code>RemotingDestination</code>
     * and calls super.addDestination.
     *
     * @param destination The <code>Destination</code> instance to be added.
     */
    @Override public void addDestination(Destination destination)
    {
        RemotingDestination remotingDest = (RemotingDestination)destination;
        super.addDestination(remotingDest);
    }

    //--------------------------------------------------------------------------
    //
    // Other Public APIs
    //
    //--------------------------------------------------------------------------
    /**
     * @exclude
     */
    public MethodMatcher getMethodMatcher()
    {
        if (methodMatcher == null)
            methodMatcher = new MethodMatcher();

        return methodMatcher;
    }

    /**
     * Processes messages of type <code>RemotingMessage</code> by invoking the
     * requested destination&quot;s adapter.
     * <p>
     * Note that this method catches all Exceptions and throws only
     * runtime <code>MessageException</code>s back to the MessageBroker as a best
     * practice for the &quot;<code>MessageBroker</code> to <code>Service</code>&quot;
     * contract.
     * </p>
     * @param msg the <code>RemotingMessage</code> to process
     * @return Object the result of the service message invocation.
     */
    @Override public Object serviceMessage(Message msg)
    {
        // TODO: fix by adding this method to the MBean.
        /*
        if (isManaged())
        {
            controller.incrementServiceMessageCount();
        }
        */

        if (msg instanceof RemotingMessage)
        {
            RemotingMessage message = (RemotingMessage)msg;
            RemotingDestination destination = (RemotingDestination)getDestination(msg);
            RemotingDestinationControl destinationControl = (destination.isManaged()) ? (RemotingDestinationControl)destination.getControl() : null;

            if (destination != null)
            {
                ServiceAdapter adapter = destination.getAdapter();
                long startTime = 0;
                if (destinationControl != null)
                    startTime = System.currentTimeMillis();
                try
                {
                    MessagePerformanceUtils.markServerPreAdapterTime(message);
                    Object result = adapter.invoke(message);
                    MessagePerformanceUtils.markServerPostAdapterTime(message);

                    if (Log.isDebug())
                    {
                        Log.getLogger(LOG_CATEGORY).debug("Adapter '{0}' called '{1}.{2}({3})'",
                                new Object[] {adapter.getId(),
                                              message.getSource(),
                                              message.getOperation(),
                                              message.getParameters()});
                        Log.getLogger(LOG_CATEGORY).debug("Result: '{0}'", new Object[] {result});
                    }

                    if (destinationControl != null)
                    {
                        // Record a successful invocation and its processing duration.
                        // Cast to an int is safe because no remoting invocation will have a longer duration in millis than Integer.MAX_VALUE.
                        destinationControl.incrementInvocationSuccessCount((int)(System.currentTimeMillis() - startTime));
                    }

                    return result;
                }
                catch (MessageException ex)
                {
                    if (destinationControl != null)
                    {
                        // Record a faulted invocation and its processing duration.
                        // Cast to an int is safe because no remoting invocation will have a longer duration in millis than Integer.MAX_VALUE.
                        destinationControl.incrementInvocationFaultCount((int)(System.currentTimeMillis() - startTime));
                    }

                    throw ex;
                }
                catch (Throwable t)
                {
                    if (destinationControl != null)
                    {
                        // Record a faulted invocation and its processing duration.
                        // Cast to an int is safe because no remoting invocation will have a longer duration in millis than Integer.MAX_VALUE.
                        destinationControl.incrementInvocationFaultCount((int)(System.currentTimeMillis() - startTime));
                    }

                    throw new MessageException(t);
                }
            }
            else
            {
                // Destination '{id}' is not registered on the Remoting Service.
                ServiceException e = new ServiceException();
                e.setMessage(UNKNOWN_DESTINATION, new Object[] {msg.getDestination()});
                throw e;
            }
        }
        else
        {
            // The 'Remoting' Service can only process messages of type 'RemotingMessage'.
            ServiceException e = new ServiceException();
            e.setMessage(UNKNOWN_MESSAGE_TYPE, new Object[]{"Remoting", "RemotingMessage"});
            throw e;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Protected/private APIs
    //
    //--------------------------------------------------------------------------

    /**
     * Returns the log category of the <code>RemotingService</code>.
     *
     * @return The log category of the component.
     */
    @Override protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Invoked automatically to allow the <code>RemotingService</code> to setup its corresponding
     * MBean control.
     *
     * @param broker The <code>MessageBroker</code> that manages this <code>RemotingService</code>.
     */
    @Override protected void setupServiceControl(MessageBroker broker)
    {
        controller = new RemotingServiceControl(this, broker.getControl());
        controller.register();
        setControl(controller);
    }
}
