/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.endpoints;

import flex.messaging.Server;

/**
 * Extension interface for <tt>Endpoint</tt> that adds support for a referenced <tt>Server</tt> that the endpoint may use.
 */
public interface Endpoint2 extends Endpoint
{
    /**
     * Returns the <tt>Server</tt> that the endpoint is using; <code>null</code> if no server has been assigned.
     */
    Server getServer();

    /**
     * Sets the <tt>Server</tt> that the endpoint will use.
     */
    void setServer(Server server);
}
