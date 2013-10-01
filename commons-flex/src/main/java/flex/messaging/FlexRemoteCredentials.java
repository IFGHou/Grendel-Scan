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

package flex.messaging;

/**
 * A wrapper object used for holding onto remote credentials. When you are using the proxy service, the remote credentials are used for authenticating against the proxy server. The remote credentials
 * are distinct from the local credentials used to authenticate against the local server. You use this class along with the FlexSession methods getRemoteCredentials and putRemoteCredentials to
 * associate the remote credentials with a specific destination.
 */
public class FlexRemoteCredentials
{
    private String service;

    private String destination;

    private String username;

    private Object credentials;

    /**
     * Normally you do not have to create the FlexRemoteCredentials as they are created automatically when the client specifies them via the setRemoteCredentials method in ActionScript. You'd use this
     * if you wanted to set your remote credentials on the server and not have them specified on the client.
     */
    public FlexRemoteCredentials(String service, String destination, String username, Object credentials)
    {
        super();
        this.service = service;
        this.destination = destination;
        this.username = username;
        this.credentials = credentials;
    }

    /**
     * Returns the user name from the remote credentials
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Returns the credentials themselves (usually a password)
     */
    public Object getCredentials()
    {
        return credentials;
    }

    /**
     * Returns the id of the service these credentials are registered for.
     */
    public String getService()
    {
        return service;
    }

    /**
     * Returns the destination for the service.
     */
    public String getDestination()
    {
        return destination;
    }
}
