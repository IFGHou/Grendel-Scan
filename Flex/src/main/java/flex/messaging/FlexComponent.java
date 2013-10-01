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

package flex.messaging;

/**
 * Defines the lifecycle interface for FlexComponents, allowing
 * the server to manage the running state of server components
 * through a consistent interface.
 */
public interface FlexComponent extends FlexConfigurable
{
    /**
     * Invoked to start the component.
     * The {@link FlexConfigurable#initialize(String, flex.messaging.config.ConfigMap)} method inherited 
     * from the {@link FlexConfigurable} interface must be invoked before this method is invoked.
     * Once this method returns, {@link #isStarted()} must return true.
     */
    void start();

    /**
     * Invoked to stop the component.
     * Once this method returns, {@link #isStarted()} must return false.
     */
    void stop();

    /**
     * Indicates whether the component is started and running.
     * 
     * @return <code>true</code> if the component has started; 
     *         otherwise <code>false</code>.
     */
    boolean isStarted();   
}
