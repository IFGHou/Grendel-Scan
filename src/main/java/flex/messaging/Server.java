/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
 * The interface for a shared server instance that may be associated with a
 * <tt>MessageBroker</tt> and used by endpoints.
 */
public interface Server extends FlexComponent
{
    /**
     * Returns the id for the server.
     * Endpoints can lookup server instances that have been associated with a <tt>MessageBroker</tt> using {@link MessageBroker#getServer(String)}.
     */
    String getId();


// TODO UCdetector: Remove unused code: 
//     /**
//      * Sets the id for the server.
//      */
//     void setId(String value);
}
