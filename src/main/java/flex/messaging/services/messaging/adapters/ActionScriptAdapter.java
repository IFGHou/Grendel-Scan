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
package flex.messaging.services.messaging.adapters;

import flex.management.runtime.messaging.services.messaging.adapters.ActionScriptAdapterControl;
import flex.messaging.Destination;
import flex.messaging.messages.Message;
import flex.messaging.services.MessageService;

/**
 * An ActionScript object based adapter for the MessageService
 * that supports simple publish/subscribe messaging between
 * ActionScript based clients.
 */
public class ActionScriptAdapter extends MessagingAdapter
{
    private ActionScriptAdapterControl controller;
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a default <code>ActionScriptAdapter</code>.
     */
    public ActionScriptAdapter()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    // Public Getters and Setters for ServiceAdapter properties
    //                             
    //--------------------------------------------------------------------------

    /**
     * Casts the <code>Destination</code> into <code>MessageDestination</code>
     * and calls super.setDestination.
     * 
     * @param destination
     */
    @Override public void setDestination(Destination destination)
    {
        Destination dest = destination;
        super.setDestination(dest);
    }
    
    //--------------------------------------------------------------------------
    //
    // Other Public APIs
    //                 
    //--------------------------------------------------------------------------
    
    /**
     * Handle a data message intended for this adapter.
     */
    @Override public Object invoke(Message message)
    {
        MessageService msgService = (MessageService)getDestination().getService();
        msgService.pushMessageToClients(message, true);
        msgService.sendPushMessageFromPeer(message, true);
        return null;
    }

    /**
     * Invoked automatically to allow the <code>ActionScriptAdapter</code> to setup its corresponding
     * MBean control.
     * 
     * @param broker The <code>Destination</code> that manages this <code>ActionScriptAdapter</code>.
     */
    @Override protected void setupAdapterControl(Destination destination)
    {
        controller = new ActionScriptAdapterControl(this, destination.getControl());
        controller.register();
        setControl(controller);
    }
}
